package com.example.mqttlib.entry;

public class MqttMsg {
    private String msg;
    private Object obj=null;  //附带参数

    public MqttMsg(String msg) {//事件传递参数
        this.msg = msg;
    }
    public MqttMsg(String msg, Object obj) {//事件传递参数
        this.msg = msg;
        this.obj=obj;
    }

    public String getMsg() {//取出事件参数
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {  //获取对象参数
        return obj;
    }

    public void setObj(Object obj) {  //取出对象参数
        this.obj = obj;
    }
}