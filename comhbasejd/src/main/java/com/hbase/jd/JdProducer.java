package com.hbase.jd;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class JdProducer {

    public Producer<String, String> jdproducer;

    public void createProducer(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "");
//        props.put("bootstrap.servers", "");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> pro = new KafkaProducer<String,String>(props);
        this.jdproducer = pro;
    }

    public void sendMsg(String topic,String content) {
        ProducerRecord<String, String> msg = new ProducerRecord<String, String>(topic, content);
        this.jdproducer.send(msg);
    }

    public void closeProducer(){
        this.jdproducer.close();
    }

}
