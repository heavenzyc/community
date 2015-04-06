/**
 * @(#)JSONHandler.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.view.support;

import com.googlecode.jsonplugin.JSONException;
import com.googlecode.jsonplugin.JSONUtil;
import com.heaven.zyc.reflect.ClassUtils;
import com.heaven.zyc.support.spring.view.support.JsonConfigHandler.JsonConfig;
import com.heaven.zyc.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * description
 *
 * @author jianguo.xu
 * @version 1.0,2011-1-12
 */
public class JSONHandler {
    private static final Log LOG = LogFactory.getLog(JSONHandler.class);

    /**
     * 根据 paramKey在jsonconfig.xml中得到object对象 的json配置<br/>
     * 如果为找到对应的json配置则采用默认的方式解析object
     * @author jianguo.xu
     * @param object
     * @param paramKey
     * @return
     */
    public static final String serialize(Object object, String paramKey) {
        JsonConfig jsonConfig = JsonConfigHandler.getConfig(paramKey);
        try {
            if(jsonConfig == null) {
                return JSONUtil.serialize(object, null, getObjectDefaultIncludePropertiesList(object), true,true, false);
            }
            else {
                return JSONUtil.serialize(object, getExcludePropertiesList(jsonConfig), getIncludePropertiesList(jsonConfig),
                        jsonConfig.isIgnoreHierarchy(), jsonConfig.isEnumAsBean(), jsonConfig.isExcludeNullProperties());
            }
			/*if(jsonConfig == null) {
				jsonConfig = new JsonConfig();
			}
			return JSONUtil.serialize(object, getExcludePropertiesList(jsonConfig), getIncludePropertiesList(jsonConfig), 
					jsonConfig.isIgnoreHierarchy(), jsonConfig.isEnumAsBean(), jsonConfig.isExcludeNullProperties());*/
        } catch (JSONException e) {
            LOG.error("parser json error.");
            throw new RuntimeException(e);
        }
    }
    /**
     * please see google json-plugin JSONResult.class
     * @author jianguo.xu
     * @param jsonConfig
     * @return
     */
    private static List<Pattern> getExcludePropertiesList(JsonConfig jsonConfig) {

        List<String> excludePatterns = JSONUtil.asList(jsonConfig
                .getExcludeProperties());
        if (excludePatterns == null)
            return null;

        List<Pattern> excludeProperties = new ArrayList<Pattern>(
                excludePatterns.size());
        String pattern;
        for (Iterator<String> iterator = excludePatterns.iterator(); iterator
                .hasNext(); excludeProperties.add(Pattern.compile(pattern)))
            pattern = iterator.next();
        return excludeProperties;
    }
    /**
     * please see google json-plugin JSONResult.class
     * @author jianguo.xu
     * @param jsonConfig
     * @return
     */
    private static List<Pattern> getIncludePropertiesList(JsonConfigHandler.JsonConfig jsonConfig) {
        List<String> includePatterns = JSONUtil.asList(jsonConfig
                .getIncludeProperties());
        if (includePatterns == null)
            return null;
        List<Pattern> includeProperties = new ArrayList<Pattern>(
                includePatterns.size());
        Map<String, String> existingPatterns = new HashMap<String, String>();
        for (Iterator<String> iterator = includePatterns.iterator(); iterator
                .hasNext();) {
            String pattern = (String) iterator.next();
            String patternPieces[] = pattern.split("\\\\\\.");
            String patternExpr = "";
            String as[];
            int j = (as = patternPieces).length;
            for (int i = 0; i < j; i++) {
                String patternPiece = as[i];
                if (patternExpr.length() > 0)
                    patternExpr = (new StringBuilder(
                            String.valueOf(patternExpr))).append("\\.")
                            .toString();
                patternExpr = (new StringBuilder(String.valueOf(patternExpr)))
                        .append(patternPiece).toString();
                if (!existingPatterns.containsKey(patternExpr)) {
                    existingPatterns.put(patternExpr, patternExpr);
                    if (patternPiece.endsWith("\\]")) {
                        includeProperties
                                .add(Pattern.compile(patternExpr.substring(0,
                                        patternPiece.lastIndexOf("\\["))));
                        if (LOG.isDebugEnabled())
                            LOG.debug((new StringBuilder(
                                    "Adding include property expression:  "))
                                    .append(patternExpr.substring(0,
                                            patternPiece.lastIndexOf("\\[")))
                                    .toString());
                    }
                    includeProperties.add(Pattern.compile(patternExpr));
                    if (LOG.isDebugEnabled())
                        LOG.debug((new StringBuilder(
                                "Adding include property expression:  "))
                                .append(patternExpr).toString());
                }
            }
        }
        return includeProperties;
    }
    /**
     * 得到对象的默认IncludePropertiesList<br/>
     * 包裹对象的基本类型、基本类型的包装类型、BigDecimal,String,Date,Calendar
     * @param object
     * @return
     */
    public static List<Pattern> getObjectDefaultIncludePropertiesList(Object object) {
        if(ClassUtils.isBaseType(object)) return null;
        if(object instanceof Collection ||object instanceof Object[]) {
            return getCollectionIncludePropertiesList(null,object);
        }
        if(object instanceof Map) {
            return getMapIncludePropertiesList(object);
        }
        return getSimpleObjectIncludePropertiesList(null,object);
    }
    /**
     * 解析普通对象
     * @param key
     * @param object
     * @return
     */
    public static List<Pattern> getSimpleObjectIncludePropertiesList(String key,Object object) {
        List<Pattern> includeProperties = new ArrayList<Pattern>();
        Map<Method, String> getMethodMap = ClassUtils.paraserGet(object.getClass(),true);
        if(!StringUtils.isNullOrEmpty(key)) {
            includeProperties.add(Pattern.compile(key));
        }
        for(Method method : getMethodMap.keySet()) {
            if(StringUtils.isNullOrEmpty(key)) {
                includeProperties.add(Pattern.compile(getMethodMap.get(method)));
            }
            else {
                includeProperties.add(Pattern.compile(key+"\\."+getMethodMap.get(method)));
            }
        }
        return includeProperties;
    }
    /**
     * 解析map对象
     * @param object
     * @return
     */
    private static List<Pattern> getMapIncludePropertiesList(Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> model = (Map<String, Object>) object;
        if(model.isEmpty()) return null;
        List<Pattern> includeProperties = new ArrayList<Pattern>();
        for(String key : model.keySet()) {
            Object value = model.get(key);
            if(value == null) continue;
            if(ClassUtils.isBaseType(value)){
                includeProperties.add(Pattern.compile(key));
                continue;
            }
            if(value instanceof Collection ||value instanceof Object[]) {
                List<Pattern> patterns = getCollectionIncludePropertiesList(key,value);
                if(patterns !=null&&patterns.size()>0) {
                    includeProperties.addAll(getCollectionIncludePropertiesList(key,value));
                }
                continue;
            }
            List<Pattern> patterns = getSimpleObjectIncludePropertiesList(key,value);
            if(patterns !=null&&patterns.size()>0) {
                includeProperties.addAll(getSimpleObjectIncludePropertiesList(key,value));
            }

        }
        return includeProperties;
    }
    /**
     * 解析集合或数组对象
     * @param key
     * @param object
     * @return
     */
    private static List<Pattern> getCollectionIncludePropertiesList(String key,Object object) {
        Object member = null;
        if(object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            if(collection.isEmpty()) return null;
            member = collection.iterator().next();
        }
        if(object instanceof Object[]) {
            Object[] objs = (Object[]) object;
            if(objs.length == 0) return null;
            member = objs[0];
        }
        if(member == null || ClassUtils.isBaseType(member)) return null;
        List<Pattern> includeProperties = new ArrayList<Pattern>();
        Map<Method, String> getMethodMap = ClassUtils.paraserGet(member.getClass(),true);
        if(StringUtils.isNullOrEmpty(key))
            includeProperties.add(Pattern.compile("\\[\\d+\\]"));
        else {
            includeProperties.add(Pattern.compile(key));
            includeProperties.add(Pattern.compile(key+"\\[\\d+\\]"));
        }

        for(Method method : getMethodMap.keySet()) {
            String regex ="\\[\\d+\\]\\."+getMethodMap.get(method);
            if(!StringUtils.isNullOrEmpty(key)) {
                regex = key+regex;
            }
            includeProperties.add(Pattern.compile(regex));
        }
        return includeProperties;
    }

}