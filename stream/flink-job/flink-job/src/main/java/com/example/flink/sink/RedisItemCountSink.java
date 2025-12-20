package com.example.flink.sink;

import com.example.flink.model.UserEvent;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import redis.clients.jedis.Jedis;

public class RedisItemCountSink implements SinkFunction<UserEvent> {

    private transient Jedis jedis;

    @Override
    public void invoke(UserEvent event, Context context) {

        if (!"view".equals(event.getEventType())) return;

        if (jedis == null) {
            jedis = new Jedis("localhost", 6379);
        }

        String key = "item:" + event.getItemId();

        jedis.incr(key); // tăng count
    }
}
