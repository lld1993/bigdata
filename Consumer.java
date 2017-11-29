package com.jd.hbase;

/**
 * Created by danglanliang on 2017/11/24.
 */
import java.io.IOException;
import java.util.*;

import com.google.protobuf.InvalidProtocolBufferException;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.*;

import static org.apache.hadoop.hbase.Version.user;


public class Consumer extends Thread{


    private final ConsumerConnector consumer;
    private final String topic;

    public static void main(String[] args) throws IOException {
        Consumer consumerThread = new Consumer("test1");
        consumerThread.start();
    }
    public Consumer(String topic) {
        consumer =kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig());
        this.topic =topic;
    }

    private static ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        // 设置zookeeper的链接地址
        props.put("zookeeper.connect","cluster3:12181,cluster4:12181,cluster1:12181");
        // 设置group id
        props.put("group.id", "1");
        // kafka的group 消费记录是保存在zookeeper上的, 但这个信息在zookeeper上不是实时更新的, 需要有个间隔时间更新
        props.put("auto.commit.interval.ms", "1000");
        props.put("zookeeper.session.timeout.ms","10000");
        return new ConsumerConfig(props);
    }

    private static byte[] encode(UserProto.User user) {
        return user.toByteArray();
    }

    private static UserProto.User decode(byte[] body)
            throws InvalidProtocolBufferException {
        return UserProto.User.parseFrom(body);
    }

    public void run(){
        //设置Topic=>Thread Num映射关系, 构建具体的流
        Map<String,Integer> topickMap = new HashMap<String, Integer>();
        topickMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[],byte[]>>>  streamMap=consumer.createMessageStreams(topickMap);

        KafkaStream<byte[],byte[]>stream = streamMap.get(topic).get(0);
        ConsumerIterator<byte[],byte[]> it =stream.iterator();

        App app = new App();


        while(it.hasNext()){
            byte[] msg = it.next().message();
            System.out.println(msg);
            System.out.println(new String(msg));
            try {
                String uuid = UUID.randomUUID().toString();
                System.out.println(uuid);
                UserProto.User user2 = decode(msg);
                String username = user2.getUserName();
                String password = user2.getPassword();
                System.out.println(user2);
                System.out.println(username);
                System.out.println(password);
                app.insert("stu",uuid,"info","name",username);
                app.insert("stu",uuid,"score","english",password);


            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }


}
