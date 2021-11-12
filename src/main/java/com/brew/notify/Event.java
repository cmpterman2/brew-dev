package com.brew.notify;

public class Event <K> {

    private String type;
    private long time;
    private K data;

    public Event() {
    }

    public Event(String type, K data) {
        this.type = type;
        this.setTime(System.currentTimeMillis());
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public K getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setData(K data) {
        this.data = data;
    }


    
    
}
