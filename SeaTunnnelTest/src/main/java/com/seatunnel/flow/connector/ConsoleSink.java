package com.seatunnel.flow.connector;


import com.hazelcast.collection.IQueue;
import com.seatunnel.flow.api.Sink;
import com.seatunnel.flow.common.Row;

public class ConsoleSink implements Sink {
    private IQueue<Row> queue;

    @Override
    public void initialize(IQueue<Row> queue) {
        this.queue = queue;
        System.out.println("ConsoleSink initialized.");
    }

    @Override
    public void consume() {
        while (!queue.isEmpty()) {
            try {
                Row row = queue.take();
                StringBuilder output = new StringBuilder("ConsoleSink processed row: ");
                for (Row.Field field : row.getFields()) {
                    output.append(field.getFieldName())
                            .append("=")
                            .append(field.getValue())
                            .append(", ");
                }
                System.out.println(output.substring(0, output.length() - 2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Console Sink Thread has done ..");
    }
}
