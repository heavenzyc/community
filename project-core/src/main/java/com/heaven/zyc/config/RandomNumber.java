package com.heaven.zyc.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * 随机数生成工具类
 * Copyright (c) 2012 by PreTang
 * All rights reserved.
 *
 * @author HuangCheng
 * @date 2012-6-20
 */
public class RandomNumber {

    /**
     * 随机产生数字
     *
     * @return
     */
    static public Integer getRandomInteger() {
        return (new Double(Math.random() * 10000).intValue());
    }

    /**
     * 随机产生4位，由数字和字符组成的字符串，字符串为小写
     *
     * @return
     */
    public static String getRandomKey() {
        return getRandomKey(4);
    }

    /**
     * 随机字符串数生成方法
     *
     * @param length
     * @return
     */
    public static String getRandomKey(Integer length) {
        String[] beforeShuffle = new String[]{"0", "1", "2", "3", "4", "5", "6", "7",
                "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z"};
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    /**
     * 随机产生数字类型的字符串
     *
     * @param length 字符串长度
     * @return
     */
    public static String getRandomNumberKey(Integer length) {
        String[] randomValues = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuffer vcodeString = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            vcodeString.append(randomValues[random.nextInt(10)]);
        }
        return vcodeString.toString();
    }

    public static Integer getRandomNum(Integer r){
        Random random = new Random();
        return Math.abs(random.nextInt())%r;
    }

    public static void main(String[] a){
        for(int i=0;i<100;i++){
            System.out.print(getRandomNum(4));
        }

    }
}