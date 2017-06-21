package com.example.mqttlib.entry;

/**
 * Created by huangkangfa on 2017/6/20.
 */

public enum MqttQosLevel {
    AtMostOnce(0),  //最多一次
    AtLeastOnce(1), //至少一次
    ExactlyOnce(2); //准确一次

    private int val;

    MqttQosLevel(int val){
        this.val=val;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
