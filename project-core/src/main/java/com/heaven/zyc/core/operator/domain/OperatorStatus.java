package com.heaven.zyc.core.operator.domain;

import com.heaven.zyc.support.entity.EnumInterface;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午10:17
 * To change this template use File | Settings | File Templates.
 */
public enum OperatorStatus implements EnumInterface {

    ACTIVE(1,"激活"),FREEZE(2,"冻结"),DELETE(3,"删除");

    private int value;
    private String desc;

    private OperatorStatus(int value, String desc) {
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

    public static OperatorStatus get(int value) {
        for (OperatorStatus item : OperatorStatus.values()) {
            if (item.value == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("argument error: " + value);
    }
}
