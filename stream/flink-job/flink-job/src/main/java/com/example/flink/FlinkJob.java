package com.example.flink;

import com.example.flink.model.UserEvent;
import com.example.flink.process.PopularityProcessFunction;
import com.example.flink.sink.RedisSink;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkJob {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 1. Create the KafkaSource using the Builder pattern
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("user-events")
                .setGroupId("flink-consumer")
                .setStartingOffsets(OffsetsInitializer.earliest()) // Define where to start
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        ObjectMapper mapper = new ObjectMapper();

        // 2. Use fromSource instead of addSource
        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .map(json -> mapper.readValue(json, UserEvent.class))
                .keyBy(UserEvent::getUserId)
                .process(new PopularityProcessFunction())
                .addSink(new RedisSink());

        env.execute("Real-time Popularity Job");
    }
}
