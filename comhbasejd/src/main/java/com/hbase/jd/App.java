package com.hbase.jd;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {

//        JdProducer jdProducer = new JdProducer();
//        jdProducer.createProducer();
//        for (int i=0;i<100;i++) {
//            jdProducer.sendMsg("test_hbase",String.valueOf(i));
//            System.out.println(i);
//        }
//        jdProducer.closeProducer();
//
//        KafkaConsumer kafkaConsumer = new KafkaConsumer();
//        kafkaConsumer.testMain();

        JdConsumer jdConsumer = new JdConsumer();
        jdConsumer.testmain();

    }
}
