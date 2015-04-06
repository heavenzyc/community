package com.heaven.zyc.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class BlackListManager {
    private boolean[] bs;
    private Set<String>[] set;
    private int max;
    public static final int HASH_THRESHOLD = 200000;
    public static final int KEYWORD_MAX_LEN = 100;
    public static final String SINGLE_WORD = "毙睾妓枪舔";
    public String[] SINGLE_PINYIN = {"bi", "gao", "ji", "qiang", "tian"};

    public BlackListManager(boolean[] bs, Set<String>[] set, int max) {
        super();
        this.bs = bs;
        this.set = set;
        this.max = max;
    }

    protected boolean inBlackList(String keyword) {
        if (keyword != null && keyword.length() > 0) {
            int hashcode = Math.abs(keyword.hashCode()) % HASH_THRESHOLD;
            if (bs[hashcode]) {
                return set[keyword.length()].contains(keyword);
            }
        }
        return false;
    }

    public String filter(String str) {
        String result = str;
        boolean blackflag = false;
        if (str != null && str.length() > 0) {
            int len = 0;
            do {
               len = result.length();
                blackflag = false;
                for (int i = 1; i <= Math.min(max, len); i++) {
                    if(set[i]==null){
                        continue;
                    }
                    for (int j = 0; j < len - i + 1; j++) {
                        String keyword = result.substring(j, j + i);
                        if (inBlackList(keyword)) {
                            if (keyword.length() == 1) {
                                int k = SINGLE_WORD.indexOf(keyword);
                                if (k >= 0) {
                                    result = result.replace(keyword, SINGLE_PINYIN[k]);
                                } else {
                                   result =  result.replace(keyword, "*");
                                }
                            } else {
                                result = result.replace(keyword, WordAnalyzer.splitBySpace(keyword));
                            }
                            blackflag = true;
                            break;
                        }
                    }
                    if (blackflag) {
                        break;
                    }
                }
            } while (blackflag);
        }
        return result;
    }

    public static BlackListManager load(String property, String encoding) throws IOException {
        BufferedReader br = null;
        String line = null;
        boolean[] bs = new boolean[HASH_THRESHOLD];
        @SuppressWarnings("unchecked")
        Set<String>[] set = new Set[KEYWORD_MAX_LEN];
        try {
            br = new BufferedReader(new InputStreamReader(BlackListManager.class
                    .getResourceAsStream(property),
                    encoding));
            int max = 0;
            while ((line = br.readLine()) != null) {
                String str = line.trim();
                bs[Math.abs(str.hashCode()) % bs.length] = true;
                if (set[str.length()] == null) {
                    set[str.length()] = new HashSet<String>();
                }
                set[str.length()].add(str);
                if (str.length() > max) {
                    max = str.length();
                }
            }
            return new BlackListManager(bs, set, max);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }

    }


    public static void main(String[] args) throws IOException {
        BlackListManager manager = load("keyword_utf8.txt", "UTF-8");
        String keyword = "{˰/-��}����";
        System.out.println(System.currentTimeMillis());
        boolean b = manager.inBlackList(keyword);
        System.out.println(System.currentTimeMillis());
        System.out.println(b);
        for (int i = 0; i < manager.set.length; i++)
            if (manager.set[i] != null) System.out.println(i);
    }
}
