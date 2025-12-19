package com.example.flink.process;

import com.example.flink.model.UserEvent;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class PopularityProcessFunction
        extends KeyedProcessFunction<String, UserEvent, String> {

    private ValueState<Long> countState;

    @Override
    public void open(org.apache.flink.configuration.Configuration parameters) {
        // Correct constructor (no 3rd parameter)
        ValueStateDescriptor<Long> descriptor = new ValueStateDescriptor<>("count", Long.class);
        countState = getRuntimeContext().getState(descriptor);
    }


    @Override
    public void processElement(UserEvent event, Context context, Collector<String> out) throws Exception {
        if (!"view".equals(event.getEventType()))
            return;

        // Manually handle the default value if current state is null
        Long currentCount = countState.value();
        long count = (currentCount == null ? 0L : currentCount) + 1;

        countState.update(count);
        out.collect(event.getItemId() + "," + count);
    }

}
