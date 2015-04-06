package com.heaven.zyc.core.operator.domain;

import com.heaven.zyc.support.entity.EnumInterface;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午10:17
 * To change this template use File | Settings | File Templates.
 */
public enum Gender implements EnumInterface {

    MAN(1,"男"),WOMAN(2,"女");

    private int value;
    private String desc;

    private Gender(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Gender get(int value) {
        for (Gender item : Gender.values()) {
            if (item.value == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("argument error: " + value);
    }
}
