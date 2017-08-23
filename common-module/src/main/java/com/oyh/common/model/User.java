package com.oyh.common.model;

import java.io.Serializable;

/**
 * Created by hang.ouyang on 2017/8/23 17:27.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 2065915980932609398L;

    private String username;
    private Integer age;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
    public User(String username, Integer age) {
        this.username = username;
        this.age = age;
    }
}
