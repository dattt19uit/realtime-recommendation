package com.example.flink.sink;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import redis.clients.jedis.Jedis;

public class RedisSink implements SinkFunction<String> {

    private transient Jedis jedis;

    @Override
    public void invoke(String value, Context context) {
        if (jedis == null) {
            jedis = new Jedis("localhost", 6379);
        }

        String[] parts = value.split(",");
        String itemId = parts[0];
        String count = parts[1];

        jedis.set("item:" + itemId, count);
    }
}
