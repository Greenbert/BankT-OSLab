package com.allen.pojo;

import java.io.Serializable;

/**
 * 客户类
 */
public class Custom implements Serializable {
    private String customid;
    private String personid;
    private String customName;
    private Integer age;
    private Character sex;//“1”:男，“0”：女

    @Override
    public String toString() {
        return "Custom{" +
                "customid='" + customid + '\'' +
                ", personid='" + personid + '\'' +
                ", customName='" + customName + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }

    public String getCustomid() {
        return customid;
    }

    public void setCustomid(String customid) {
        this.customid = customid;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Character getSex() {
        return sex;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public Custom(String customid, String personid, String customName, Integer age, Character sex) {
        this.customid = customid;
        this.personid = personid;
        this.customName = customName;
        this.age = age;
        this.sex = sex;
    }
}
