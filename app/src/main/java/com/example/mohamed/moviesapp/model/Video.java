package com.example.mohamed.moviesapp.model;

import java.io.Serializable;

public class Video  implements Serializable {

    private String key;
    private String name;

    public Video(String key,String name){
        this.key = key;
        this.name = name;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
