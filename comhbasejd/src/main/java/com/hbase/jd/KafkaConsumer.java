package com.hbase.jd;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * java实现Kafka消费者的示例
 *
 */
public class KafkaConsumer {
    private static final String TOPIC = "test_hbase";
    private static final int THREAD_AMOUNT = 3;

    public void testMain() {

        Properties props = new Properties();
        props.put("zookeeper.connect", "");
        props.put("bootstrap.servers", "");
        props.put("group.id", "group123_info");
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        //每个topic使用多少个kafkastream读取, 多个consumer
        topicCountMap.put(TOPIC, THREAD_AMOUNT);
        //可以读取多个topic
//      topicCountMap.put(TOPIC2, 1);
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        Map<String, List<KafkaStream<byte[], byte[]>>> msgStreams = consumer.createMessageStreams(topicCountMap );
        List<KafkaStream<byte[], byte[]>> msgStreamList = msgStreams.get(TOPIC);

        //使用ExecutorService来调度线程
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_AMOUNT);
        for (int i = 0; i < msgStreamList.size(); i++) {
            KafkaStream<byte[], byte[]> kafkaStream = msgStreamList.get(i);
            executor.submit(new HanldMessageThread(kafkaStream, i));
        }


        //关闭consumer
        try {
            Thread.sleep(2000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (consumer != null) {
            consumer.shutdown();
        }
        if (executor != null) {
            executor.shutdown();
        }
        try {
            if (!executor.awaitTermination(5000000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
    }

}
