package com.yinrong.cache.core.dis;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * 分布式缓存
 *
 * @author liurong
 *
 */
public interface DisCache {
    public void set(String key, String value);

    public void setJson(String key, Object value);

    public void setJsonVer(String key, Object value);

    public void append(String key, String value);

    public String get(String key);

    @SuppressWarnings( "rawtypes" )
    public List getListJson(String key, Class clazz);

    @SuppressWarnings( "rawtypes" )
    public List getListByMapJson(String key, Class clazz);

    @SuppressWarnings("rawtypes")
    public Object getObjectJson(String key, Class clazz) ;

    @SuppressWarnings("rawtypes")
    public Map getMapJson(String key, Class keyclazz, Class valueclazz);

    @SuppressWarnings("rawtypes")
    public Map getConcMapJson(String key, Class keyclazz, Class valueclazz);

    @SuppressWarnings("rawtypes")
    public Map getMapByListJson(String key, Class keyclazz, Class valueclazz);

    @SuppressWarnings("rawtypes")
    public Map getMapByMapJson(String key, Class keyclazz, Class valueclazz);

    public <T> Object getObject(String key, TypeReference<T> typeReference);

    public void del(String key) ;

    public void setVer(String key, String value) ;

    public void appendVer(String key, String value) ;

    public void delVer(String key) ;

    //
    public void setList(String key, String... strings);

    public void setListVer(String key, String... strings);
    public List<String> blpop(String key);
    public List<String> brpop(String key);
    public List<String> getList(String key, long start, long end);

    public String lpop(String key) ;

    public String rpop(String key);

    public void setMap(String key, String field, String value);

    public String getMap(String key, String field) ;
    public Map<String, String> getMapAll(String key);
    public List<String> getMap(String key, String... fields);

    public void delMap(String key, String... fields) ;

    public void setMap(String key, Map<String, String> hash);

    public void setMapVer(String key, String field, String value) ;

    public void delMapVer(String key, String... fields) ;

    public void setMapVer(String key, Map<String, String> hash) ;

}

