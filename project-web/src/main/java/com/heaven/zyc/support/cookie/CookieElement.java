package com.heaven.zyc.support.cookie;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-23
 * Time: 下午10:35
 * To change this template use File | Settings | File Templates.
 */
public class CookieElement {

    public static final String OPERATOR_ID = "operator_id";

    private String name;

    private String value;

    private int day;

    public CookieElement(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public CookieElement(String name, String value, int day) {
        this.name = name;
        this.value = value;
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
