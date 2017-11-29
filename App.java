package com.jd.hbase;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class App extends Thread {

    public static Configuration configuration;
    public static Connection connection;
    public static Admin admin;

//    public static void main(String[] args) throws Exception{
//        listTables();
//        scanData();
//        System.out.println("hello world");
//
//    }


    /**
     * 初始化链接
     */
    public static void init() {
        configuration = HBaseConfiguration.create();

        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭连接
     */
    public static void close() {
        try {
            if (null != admin) {
                admin.close();
            }
            if (null != connection) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看已有表
     *
     * @throws IOException
     */
    public static void listTables() {
        init();
        HTableDescriptor hTableDescriptors[] = null;
        try {
            hTableDescriptors = admin.listTables();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (HTableDescriptor hTableDescriptor : hTableDescriptors) {
            System.out.println(hTableDescriptor.getNameAsString());
        }
        close();
    }

    /**
     * 删除表
     */
    public static void delTable() throws IOException{
        init();

        HTableDescriptor hTableDescriptors[] = null;
        try {
            hTableDescriptors = admin.listTables();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String str;
        for (HTableDescriptor hTableDescriptor : hTableDescriptors) {
            str = hTableDescriptor.getNameAsString();
            if (str.equals("test")) {
                continue;
            }

            TableName tName = TableName.valueOf(str);

            admin.disableTable(tName);
            admin.deleteTable(tName);

        }

        close();
    }


    /**
     *
     * 扫描表
     *
     */
    public static void scanData() throws IOException{
        init();
        Table table = connection.getTable(TableName.valueOf("stu"));
        try {

            ResultScanner scan = table.getScanner(new Scan());

            for (Result rst : scan) {
                String rowKey = Bytes.toString(rst.getRow());
                NavigableMap<byte[], NavigableMap<byte[],byte[]>> familyMap = rst.getNoVersionMap();
                for(byte[] fByte : familyMap.keySet()){
                    NavigableMap<byte[],byte[]> quaMap = familyMap.get(fByte);
                    String familyName = Bytes.toString(fByte);
                    for(byte[] quaByte : quaMap.keySet()){
                        byte[] valueByte = quaMap.get(quaByte);
                        String quaName = Bytes.toString(quaByte);
                        String value = Bytes.toString(valueByte);
                        String result = String.format("rowKey : %s | family : %s | qualifiers : %s | value : %s", rowKey, familyName, quaName, value);
                        System.out.println(result);
                    }
                }
            }
            scan.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }


    /**
     * 插入单行
     *
     * @param tableName 表名称
     * @param rowKey RowKey
     * @param colFamily 列族
     * @param col 列
     * @param value 值
     * @throws IOException
     */
    public void insert(String tableName, String rowKey, String colFamily, String col, String value) throws IOException {
        init();

        System.out.println("init");

        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(value));
        table.put(put);

        table.close();

        System.out.println("close");
        close();
    }

}
