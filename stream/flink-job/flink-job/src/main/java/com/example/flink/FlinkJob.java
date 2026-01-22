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

    private static void updateTrending(UserEvent event, Jedis jedis) {

        if (!"view".equals(event.getEventType())) {
            return;
        }

        String itemId = event.getItemId();
        String categoryId = jedis.hget("item:" + itemId, "categoryid");

        if (categoryId == null) {
            System.out.println("[TRENDING] Item " + itemId + " has no category");
            return;
        }

        String key = "category:" + categoryId + ":popular";
        jedis.zincrby(key, 1, itemId);
        jedis.zremrangeByRank(key, 0, -101);

        System.out.println(
                "[TRENDING] Increase score of item " + itemId +
                        " in category " + categoryId
        );
    }

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

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
                .map(json -> {
                    UserEvent event = mapper.readValue(json, UserEvent.class);
                    System.out.println("[KAFKA] Receive event: " + event);
                    return event;
                });

        events
                .keyBy(UserEvent::getUserId)
                .process(new UserBehaviorProcess())
                .name("User Behavior Processor");

        env.execute("Realtime Recommendation Job");
    }

    // ================= PROCESS FUNCTION =================

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

            System.out.println("[INIT] UserBehaviorProcess started");
        }

        @Override
        public void processElement(
                UserEvent event,
                Context ctx,
                Collector<Void> out) throws Exception {

            if (!"view".equals(event.getEventType())) {
                return;
            }

            String userId = event.getUserId();
            String currentItem = event.getItemId();

            System.out.println(
                    "\n[PROCESS] User " + userId +
                            " viewed item " + currentItem
            );

            // Load session history
            List<String> items = new ArrayList<>();
            for (String item : recentItems.get()) {
                items.add(item);
            }

            System.out.println("[SESSION] Previous recent items: " + items);

            // Update item-item co-view
            for (String item : items) {
                if (!item.equals(currentItem)) {
                    jedis.zincrby("item:" + item + ":related", 1, currentItem);
                    jedis.zincrby("item:" + currentItem + ":related", 1, item);

                    System.out.println(
                            "[CO-VIEW] Increase relation: " +
                                    item + " <-> " + currentItem
                    );
                }
            }

            // Update session state
            items.add(currentItem);
            if (items.size() > MAX_RECENT_ITEMS) {
                items = items.subList(items.size() - MAX_RECENT_ITEMS, items.size());
            }
            recentItems.update(items);

            System.out.println("[SESSION] Updated recent items: " + items);

            // Sync to Redis (serving)
            jedis.lpush("user:" + userId + ":recent", currentItem);
            jedis.ltrim("user:" + userId + ":recent", 0, MAX_RECENT_ITEMS - 1);

            System.out.println(
                    "[REDIS] Sync recent list for user " + userId
            );

            updateTrending(event, jedis);
        }

        @Override
        public void close() {
            jedis.close();
            System.out.println("[CLOSE] Jedis connection closed");
        }
    }
}
