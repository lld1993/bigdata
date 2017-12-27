package com.hbase.jd;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

class HanldMessageThread implements Runnable {

    private KafkaStream<byte[], byte[]> kafkaStream = null;
    private int num = 0;

    public HanldMessageThread(KafkaStream<byte[], byte[]> kafkaStream, int num) {
        super();
        this.kafkaStream = kafkaStream;
        this.num = num;
    }

    public void run() {
        ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
        System.out.println(iterator);
        while(iterator.hasNext()) {
            String message = new String(iterator.next().message());
            System.out.println("Thread no: " + num + ", message: " + message);
        }
    }

}
