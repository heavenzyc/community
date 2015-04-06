package com.heaven.zyc.config;

import java.math.BigInteger;
import java.util.UUID;

/**
 * 随机生成UUID
 * Copyright (c) 2012 by PreTang
 * All rights reserved.
 *
 * @author WangQiao
 *         创建时间：2012-7-18 上午11:34:45
 */
public class UUIDGenerator {
    public UUIDGenerator() {
    }

    public final static String uuid() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        return temp;
    }

    // 获得指定数量的UUID
    public final static String[] uuids(int number) {
        if (number < 1) {
            return null;
        }
        String[] ss = new String[number];
        for (int i = 0; i < number; i++) {
            ss[i] = uuid();
        }
        return ss;
    }


    public final static String sn() {
        String uuid = uuid();
        String longSn = new BigInteger(uuid, 16).toString();
        String sn = longSn.substring(0, 12);
        return sn;
    }
}
