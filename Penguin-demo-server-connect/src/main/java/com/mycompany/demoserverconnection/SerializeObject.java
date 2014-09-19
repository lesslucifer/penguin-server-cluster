/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.demoserverconnection;

import java.io.Serializable;

/**
 *
 * @author hieubui
 */
public class SerializeObject implements Serializable {

    private String prefix = "mr";
    private String name;

    private int age;

    public SerializeObject(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return name + ":" + age;
    }
}
