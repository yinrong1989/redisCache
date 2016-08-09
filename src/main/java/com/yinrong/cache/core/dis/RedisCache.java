package com.yinrong.cache.core.dis;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yinrong on 2016/8/9.
 */
public interface RedisCache {
    public void addVersion(String key) ;
    public boolean checkVersion(String key, String value);
    public void set(String key, String value);
    public void append(String key, String value);
    public String get(String key) ;
    public void del(String key) ;
    public void setVer(String key, String value);
    public void appendVer(String key, String value);
    public void delVer(String key);
    public void setList(String key, String... strings);
    public List<String> getList(String key, long start, long end) ;
    public String lpop(String key);
    public String rpop(String key) ;
    public List<String> blpop(String key);
    public List<String> brpop(String key);
    public void setListVer(String key, String... strings);
    public void setMap(String key, String field, String value);
    public String getMap(String key, String field);
    public Map<String, String> getMapAll(String key);
    public List<String> getMap(String key, String... fields);
    public void delMap(String key, String... fields) ;
    public void setMap(String key, Map<String, String> hash);
    public void setMapVer(String key, String field, String value);
    public void delMapVer(String key, String... fields);
    public void setMapVer(String key, Map<String, String> hash);
    public String getJedisConfig(String key);
    //public List<Slowlog> getShowLog(String key);
    public long getDbSize(String key);
    public Set<String> getAllKeys(String key);
}
