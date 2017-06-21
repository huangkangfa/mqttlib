package com.example.mqttlib.util;

import com.example.mqttlib.entry.MqttMsg;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/5/3 0003.
 */
public class EventBusUtil {
    /**
     * 注册订阅者
     * @param obj
     */
    public static void register(Object obj){
        EventBus.getDefault().register(obj);
    }

    /**
     * 取消注册
     * @param obj
     */
    public static void unregister(Object obj){
        EventBus.getDefault().unregister(obj);
    }

    /**
     * 发送事件
     * @param msg
     */
    public static void sendData(String msg){
        EventBus.getDefault().post(new MqttMsg(msg));
    }

    /**
     * 发送事件
     * @param msg
     * @param obj
     */
    public static void sendData(String msg,Object obj){
        EventBus.getDefault().post(new MqttMsg(msg,obj));
    }

    /**
     * 发送事件
     * @param msg
     */
    public static void sendData(MqttMsg msg){
        EventBus.getDefault().post(msg);
    }

}
