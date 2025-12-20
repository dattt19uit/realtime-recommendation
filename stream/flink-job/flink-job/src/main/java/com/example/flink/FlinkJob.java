package com.example.flink;

import com.example.flink.model.UserEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class FlinkJob {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        // Kafka Source
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("user-events")
                .setGroupId("flink-recommendation")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> rawStream = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source"
        );

        ObjectMapper mapper = new ObjectMapper();

        DataStream<UserEvent> events = rawStream
                .map(json -> mapper.readValue(json, UserEvent.class));

        // Core logic
        events
                .keyBy(UserEvent::getUserId)
                .process(new UserBehaviorProcess())
                .name("User Behavior Processor");

        env.execute("Realtime Recommendation Job");
    }

    //  PROCESS FUNCTION
    public static class UserBehaviorProcess
            extends KeyedProcessFunction<String, UserEvent, Void> {

        private transient ListState<String> recentItems;
        private transient Jedis jedis;

        private static final int MAX_RECENT_ITEMS = 5;

        @Override
        public void open(Configuration parameters) {

            recentItems = getRuntimeContext().getListState(
                    new ListStateDescriptor<>("recent-items", String.class)
            );

            jedis = new Jedis("localhost", 6379);
        }

        @Override
        public void processElement(
                UserEvent event,
                Context ctx,
                Collector<Void> out) throws Exception {

            String userId = event.getUserId();
            String currentItem = event.getItemId();

            // Lưu recent items của user
            jedis.lpush("user:" + userId + ":recent", currentItem);
            jedis.ltrim("user:" + userId + ":recent", 0, MAX_RECENT_ITEMS - 1);

            // 2Lấy items trước đó từ state
            List<String> items = new ArrayList<>();
            for (String item : recentItems.get()) {
                items.add(item);
            }

            // Update item-item co-occurrence
            for (String item : items) {
                if (!item.equals(currentItem)) {
                    jedis.zincrby(
                            "item:" + item + ":related",
                            1,
                            currentItem
                    );
                    jedis.zincrby(
                            "item:" + currentItem + ":related",
                            1,
                            item
                    );
                }
            }

            // Update state
            items.add(currentItem);
            if (items.size() > MAX_RECENT_ITEMS) {
                items = items.subList(items.size() - MAX_RECENT_ITEMS, items.size());
            }
            recentItems.update(items);
        }

        @Override
        public void close() {
            jedis.close();
        }
    }
}
