package com.yinrong.cache.core.dis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yinrong.cache.core.util.StringUtils;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Slowlog;


public class JedisSentineUtil implements RedisCache {
    public static final String SYS_CODE = "VOP.CACHE.JedisSentineUtil";
  //  private OpenLogUtil logger = new OpenLogUtil(JedisSentineUtil.class);
    // private static JedisPool jedisPool;//非切片连接池
    // private static ShardedJedisPool shardedJedisPool;//切片连接池
    private static JedisSentinelPool jedisSentinelPool;
    private Map<String, String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public JedisSentineUtil(Map<String, String> map) {
        setMap(map);
        // initialPool();
        // initialShardedPool();
        iniJedisSentinelPool();
        // shardedJedis = shardedJedisPool.getResource();
        // jedis = jedisPool.getResource();
    }

    private void iniJedisSentinelPool() {
        if (null == map)
            map = new HashMap<String, String>();
        String portss = map.get(RedisUtil.PORT);
        String ips = map.get(RedisUtil.IP);
        String names = map.get(RedisUtil.NAME);
        if (StringUtils.isBlank(portss)) {
            portss = "26379";
        }
        if (StringUtils.isBlank(ips)) {
            ips = "127.0.0.1";
        }
        if (StringUtils.isBlank(names)) {
            names = "mymaster";
        }
        // slave链接
        Set<String> sentinels = new HashSet<String>();
        String[] ipsstr = ips.split(";");
        String[] portsstr = portss.split(";");
        for (int i = 0; i < ipsstr.length; i++) {
            sentinels.add(ipsstr[i] + ":" + Integer.valueOf(portsstr[i]));
        }

        // 构造池
        jedisSentinelPool = new JedisSentinelPool(names, sentinels);
    }

    // /**
    // * 初始化非切片池
    // */
    // private void initialPool()
    // {
    // // 池基本配置
    // Map<String,String> map=getRes();
    // if(null==map)map=new HashMap<String,String>();
    // String ports = map.get(PORT);
    // String ip = map.get(IP);
    // if(StringUtils.isBlank(ports)){
    // ports="6379";
    // }
    // if(StringUtils.isBlank(ip)){
    // ip="127.0.0.1";
    // }
    // //增加从注册信息中获取
    // ip=RegeditUtil.replaceCacheUrl(ip);
    // JedisPoolConfig config = getConfig();
    // jedisPool = new JedisPool(config,ip,Integer.valueOf(ports),5000);
    // }
    //
    // private JedisPoolConfig getConfig() {
    // JedisPoolConfig config = new JedisPoolConfig();
    // config.setMaxIdle(10);
    // config.setMaxTotal(50);
    // config.setMaxWaitMillis(10000);
    // config.setTestOnBorrow(true);
    // config.setTestOnReturn(true);
    // return config;
    // }
    //
    // /**
    // * 初始化切片池
    // */
    // private void initialShardedPool()
    // {
    // // 池基本配置
    // Map<String,String> map=getRes();
    // if(null==map)map=new HashMap<String,String>();
    // String portss = map.get(PORT);
    // String ips = map.get(IP);
    // String names = map.get(NAME);
    // if(StringUtils.isBlank(portss)){
    // portss="6379";
    // }
    // if(StringUtils.isBlank(ips)){
    // ips="127.0.0.1";
    // }
    // if(StringUtils.isBlank(names)){
    // names="master";
    // }
    // // slave链接
    // JedisPoolConfig config = getConfig();
    // List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
    // String[] ipsstr=ips.split(";");
    // String[] portsstr=portss.split(";");
    // String[] namesstr=names.split(";");
    // for (int i = 0; i < ipsstr.length; i++) {
    // shards.add(new
    // JedisShardInfo(ipsstr[i],Integer.valueOf(portsstr[i]),5000,namesstr[i]));
    // }
    // // 构造池
    // shardedJedisPool = new ShardedJedisPool(config, shards);
    // }
    /**
     * 获取一个jedis 对象
     *
     * @return
     */
    private Jedis getJedis() {
        return jedisSentinelPool.getResource();
    }

    /**
     * 归还一个连接
     *
     * @param jedis
     */
    private void returnRes(Jedis resource) {
        jedisSentinelPool.returnResource(resource);
    }

    public synchronized void addVersion(String key) {
        String value = get(key);
        if (StringUtils.isBlank(value))
            value = "0";
        value = String.valueOf(Integer.valueOf(value).intValue() + 1);
        Jedis resource = getJedis();
        try {
            resource.set(key, value);
        } finally {
            returnRes(resource);
        }
    }

    public boolean checkVersion(String key, String value) {
        String oldValue = get(key);
        if (StringUtils.isBlank(oldValue))
            oldValue = "0";
        if (StringUtils.isBlank(value))
            value = "0";
        if (Integer.valueOf(oldValue) - Integer.valueOf(value) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void set(String key, String value) {
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.set(key, value);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void append(String key, String value) {
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.append(key, value);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public String get(String key) {
        Jedis shardedJedis = getJedis();
        try {
            String res = shardedJedis.get(key);
            return res;
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void del(String key) {
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.del(key);
        } finally {
            returnRes(shardedJedis);
        }
    }

    //
    public void setVer(String key, String value) {
        addVersion(key + "-ver");
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.set(key, value);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void appendVer(String key, String value) {
        addVersion(key + "-ver");
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.append(key, value);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void delVer(String key) {
        addVersion(key + "-ver");
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.del(key);
        } finally {
            returnRes(shardedJedis);
        }
    }

    //
    public void setList(String key, String... strings) {
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.rpush(key, strings);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public List<String> getList(String key, long start, long end) {
        Jedis shardedJedis = getJedis();
        try {
            List<String> res = shardedJedis.lrange(key, start, end);
            return res;
        } finally {
            returnRes(shardedJedis);
        }
    }

    public String lpop(String key) {
        Jedis shardedJedis = getJedis();
        try {
            String res = shardedJedis.lpop(key);
            return res;
        } finally {
            returnRes(shardedJedis);
        }
    }

    public String rpop(String key) {
        Jedis shardedJedis = getJedis();
        try {
            String res = shardedJedis.rpop(key);
            return res;
        } finally {
            returnRes(shardedJedis);
        }
    }

    public List<String> blpop(String key) {
        Jedis shardedJedis = getJedis();
        try {
            List<String> res = shardedJedis.blpop(key);
            return res;
        } finally {
            returnRes(shardedJedis);
        }
    }

    public List<String> brpop(String key) {
        Jedis shardedJedis = getJedis();
        try {
            List<String> res = shardedJedis.brpop(key);
            return res;
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void setListVer(String key, String... strings) {
        addVersion(key + "-ver");
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.rpush(key, strings);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void setMap(String key, String field, String value) {
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.hset(key, field, value);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public String getMap(String key, String field) {
        Jedis shardedJedis = getJedis();
        try {
            String res = shardedJedis.hget(key, field);
            return res;
        } finally {
            returnRes(shardedJedis);
        }
    }

    public Map<String, String> getMapAll(String key) {
        Jedis shardedJedis = getJedis();
        try {
            Map<String, String> res = shardedJedis.hgetAll(key);
            return res;
        } finally {
            returnRes(shardedJedis);
        }
    }

    public List<String> getMap(String key, String... fields) {
        Jedis shardedJedis = getJedis();
        try {
            List<String> res = shardedJedis.hmget(key, fields);
            return res;
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void delMap(String key, String... fields) {
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.hdel(key, fields);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void setMap(String key, Map<String, String> hash) {
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.hmset(key, hash);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void setMapVer(String key, String field, String value) {
        addVersion(key + "-ver");
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.hset(key, field, value);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void delMapVer(String key, String... fields) {
        addVersion(key + "-ver");
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.hdel(key, fields);
        } finally {
            returnRes(shardedJedis);
        }
    }

    public void setMapVer(String key, Map<String, String> hash) {
        addVersion(key + "-ver");
        Jedis shardedJedis = getJedis();
        try {
            shardedJedis.hmset(key, hash);
        } finally {
            returnRes(shardedJedis);
        }
    }

    /**
     * 获取redis连接信息
     *
     * @return
     */
    public String getJedisConfig(String key) {
        Client client = null;
        Jedis jedis = getJedis();
        try {
            client = jedis.getClient();
            if (null == client) {
                return null;
            }
            client.info();
            return client.getBulkReply();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
            returnRes(jedis);
        }
        return null;
    }

    /**
     * 获取慢查询日志
     *
     * @return
     */
    public List<Slowlog> getShowLog(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.slowlogGet();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnRes(jedis);
        }
        return null;
    }

    /**
     * 获取key数量
     *
     * @param key
     * @return
     */
    public long getDbSize(String key) {
        Client client = null;
        Jedis jedis = getJedis();
        try {
            client = jedis.getClient();
            if (null == client) {
                return 0;
            }
            client.dbSize();
            return client.getIntegerReply();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnRes(jedis);
        }
        return 0;
    }

    public Set<String> getAllKeys(String key) {
        Set<String> s = null;
        Jedis jedis = getJedis();
        try {
            s = jedis.keys(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnRes(jedis);
        }
        return s;
    }

}

