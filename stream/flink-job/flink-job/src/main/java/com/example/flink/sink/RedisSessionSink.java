package com.example.flink.sink;

import com.example.flink.model.UserEvent;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import redis.clients.jedis.Jedis;

public class RedisSessionSink implements SinkFunction<UserEvent> {

    private transient Jedis jedis;

    @Override
    public void invoke(UserEvent event, Context context) {
        if (!"view".equals(event.getEventType())) return;

        if (jedis == null) {
            jedis = new Jedis("localhost", 6379);
        }

        String key = "user:" + event.getUserId() + ":recent";

        jedis.lpush(key, event.getItemId());
        jedis.ltrim(key, 0, 9); // giữ 10 item gần nhất
    }
}
