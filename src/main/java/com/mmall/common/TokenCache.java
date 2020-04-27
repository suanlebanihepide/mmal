/*
 * @Author: shenzheng
 * @Date: 2020/4/28 1:07
 */

package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    public static final String TOKEN_PREFIX = "token_";
    private static LoadingCache<String, String> loadCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(100000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //                默认数据加载实现 当调用的Key不存在，就调用该方法匿名实现
                @Override
                public String load(String o) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key, String value) {

        loadCache.put(key, value);

    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = loadCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
        } catch (Exception e) {
            logger.error("localCache get error", e);
        }
        return null;
    }
}
