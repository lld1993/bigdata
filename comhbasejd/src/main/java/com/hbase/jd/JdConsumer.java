package com.hbase.jd;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;


public class JdConsumer
{
    public void testmain()
    {
        Properties props = new Properties();
        props.put("bootstrap.servers", "");
        props.put("group.id", "testw");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer ");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //props.put("auto.offset.reset", "earliest");
        //props.put("zookeeper.connect", "127.0.0.1:2181");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("etl_info"));
        while (true) {
            System.out.printf("----- begin consume---------\n");
            ConsumerRecords<String, String> records = consumer.poll(100);
            int i = 0;
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            i++;
            if (i > 1) {
                throw new RuntimeException("abort");
            }
        }
    }
}
