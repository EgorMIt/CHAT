package com.example.egor.egorchat;

import java.util.Date;

public class Message {
    private String autor;
    private String text;
    private long time;


    public Message(String text, String autor) {
        this.text = text;
        this.autor = autor;

        time = new Date().getTime(); //текущее время
    }

    public Message() {
    }


    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
