package com.yinrong.cache.core.dis;

import com.yinrong.cache.core.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinrong on 2016/8/9.
 */
public class RedisUtil {
    public static final String IP = "ip";
    public static final String PORT = "port";
    public static final String NAME = "name";
    public static final String SCHEME = "scheme";
    public static final String INDEX = "index";
    private static RedisCache redisCache;
    private static RedisUtil redisUtil;

    private RedisUtil() {
    }

    public static RedisUtil getRedisUtil() {

        synchronized (RedisUtil.class) {
            if (null == RedisUtil.redisUtil) {
          //      Map<String, String> map=getRes();
                    Map<String,String>map=new HashMap<String, String>();
                    RedisUtil.redisCache = new JedisSentineUtil(map);
                }
                RedisUtil.redisUtil=new RedisUtil();

        }
        return RedisUtil.redisUtil;
    }
  /*  public static Map<String, String> getRes() {
        String url = "";
        if (null != ServletMain.getRegeditBean() && StringUtils.isNotBlank(ServletMain.getRegeditBean().getCacheUrl())) {
            url = ServletMain.getRegeditBean().getCacheUrl();
        } else {
            url = RegeditUtil.getCacheUrl();
        }
        if (StringUtils.isBlank(url))
            return null;
        Map<String, String> map = new HashMap<String, String>();
        String[] str = url.split(RegeditBean.APPCACHE_URL_SPLIT);
        if (str.length > 0){
            map.put(RedisUtil.INDEX, str[0]);
            setIndex(str[0]);
        }
        if (str.length > 1)
            map.put(RedisUtil.SCHEME, str[1]);
        if (str.length > 2)
            map.put(RedisUtil.IP, str[2]);
        if (str.length > 3)
            map.put(RedisUtil.PORT, str[3]);
        if (str.length > 4)
            map.put(RedisUtil.NAME, str[4]);
        return map;
    }*/
}
