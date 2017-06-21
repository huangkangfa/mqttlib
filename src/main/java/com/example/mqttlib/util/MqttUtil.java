package com.example.mqttlib.util;//package jz.utils;

import com.example.mqttlib.entry.MqttQosLevel;
import com.example.mqttlib.interfaces.MqttConnectListener;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * mqtt管理
 * Created by huangkangfa on 2017/6/16.
 */

public class MqttUtil{
    /**
     * 单例模式
     */
    private static MqttUtil mqtt;
    private MqttUtil(){}
    public static MqttUtil getInstance(){
        if(mqtt==null){
            synchronized (MqttUtil.class){
                if(mqtt==null){
                    mqtt=new MqttUtil();
                }
            }
        }
        return mqtt;
    }

    /**
     * 连接监听
     */
    private static MqttConnectListener listener;
    //mqtt客户端
    private MqttClient client;
    //mqtt连接设置
    private MqttConnectOptions options;
    //实时监测并重连
    private ScheduledExecutorService scheduler;
    //主题集合
    private List<String> topics;
    //形式-默认是准确的一次
    private int qos= MqttQosLevel.ExactlyOnce.getVal();

    /**
     * 初始化mqtt对象
     * @param host
     * @param userName
     * @param passWord
     * @param clientId
     * @param callback
     */
    public void initMqtt(String host,String userName,String passWord,String clientId,MqttCallback callback) {
        if(client==null){
            topics=new ArrayList<String>();
            init(host,userName,passWord,clientId,callback);
        }
    }

    /**
     * 初始化mqtt对象
     * @param host
     * @param clientId
     * @param callback
     */
    private void init(String host,String clientId,MqttCallback callback) {
        this.init(host,null,null,clientId,callback);
    }

    /**
     * 初始化mqtt对象
     * @param host
     * @param userName
     * @param passWord
     * @param clientId
     * @param callback
     */
    private void init(String host,String userName,String passWord,String clientId,MqttCallback callback) {
        this.init(host,userName,passWord,clientId,10,20,callback);
    }

    /**
     * 初始化mqtt对象
     * @param host
     * @param userName
     * @param passWord
     * @param clientId
     * @param timeout
     * @param heartTime
     * @param callback
     */
    private void init(String host,String userName,String passWord,String clientId,int timeout,int heartTime,MqttCallback callback) {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host,clientId,new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            if(userName!=null){
                //设置连接的用户名
                options.setUserName(userName);
            }
            if(passWord!=null){
                //设置连接的密码
                options.setPassword(passWord.toCharArray());
            }
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(timeout);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(heartTime);
            //设置回调
            client.setCallback(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接并重新订阅主题
     */
    public void connect() {
        if(!client.isConnected()){
            synchronized (MqttUtil.class){
                if(!client.isConnected()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                client.connect(options);
                                EventBusUtil.sendData("REGISTER_THEME");
                                if(listener!=null)
                                    listener.success();
                            } catch (Exception e) {
                                e.printStackTrace();
                                if(listener!=null)
                                    listener.failed();
                            }
                        }
                    }).start();
                }
            }
        }
    }

    /**
     * 开启连接,并使用重连机制
     */
    public void startConnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 断开连接
     */
    public void stopConnect(){
        try {
            scheduler.shutdown();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅
     */
    public void subscribe(){
        try {
            for (int i=0;i<topics.size();i++){
                try {
                    client.unsubscribe(topics.get(i));
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                client.subscribe(topics.get(i),qos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加主题
     * @param topic
     */
    public void addTopic(String topic){
        topics.add(topic);
    }

    /**
     * 清空主题
     */
    public void clearTopics(){
        topics.clear();
    }

    /**
     * 接收数据形式
     * @param mMqttQosLevel
     */
    public void setQos(int mMqttQosLevel){
        qos=mMqttQosLevel;
    }


    /**
     * 设置连接监听
     */
    public void setMqttConnectListener(MqttConnectListener l){
        listener=l;
    }
}
