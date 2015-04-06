package com.heaven.zyc.config;

import java.util.ArrayList;
import java.util.List;

/**
 *  分词
 */
public final class WordAnalyzer {
    private WordAnalyzer() {
    }

    public static String[] cut(String str, int length) {
        List<String> result = new ArrayList<String>();
        if (str != null && str.length() > 0) {
            int k = str.length() - length;
            if (k < 0 || length <= 0) {
                result.add(str);
            } else {
                for (int i = 0; i <= k; i++) {
                    result.add(str.substring(i, i + length));
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public static String splitBySpace(String str) {
        StringBuilder result = new StringBuilder();
        if (str != null && str.length() > 1) {
            for (int i = 0; i < str.length(); i++) {
                result.append(str.charAt(i));
                if (i < str.length() - 1) {
                    result.append(" ");
                }
            }
        }
        return result.toString();
    }
}
