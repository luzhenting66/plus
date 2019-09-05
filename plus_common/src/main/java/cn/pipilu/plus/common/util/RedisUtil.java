package cn.pipilu.plus.common.util;

import cn.pipilu.plus.common.seriallizer.RedisObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Component
public class RedisUtil<T> {
    private final Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisObjectSerializer redisObjectSerializer;
    private String                        redisCode = "utf-8";

    /**
     * 刷新缓存
     * @return
     */
    public String flush() {
        return redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
    }

    /**
     * @param key
     *            k
     * @param value
     *            v
     * @param liveTime
     *            缓存强制要求限制超时时间，单位为秒
     * @since 1.0
     */
    public void set(final byte[] key, final byte[] value, final long liveTime) {
        redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    connection.set(key, value);
                    if (liveTime > 0) {
                        connection.expire(key, liveTime);
                    }
                    return 1L;
                } catch (Exception e) {
                    logger.error("redis set error", e);
                }
                return null;
            }
        });
    }

    /**
     * @param key
     *            k
     * @param value
     *            v
     * @param liveTime
     *            缓存强制要求限制超时时间，单位为秒
     * @since 1.0
     */
    public void set(String key, String value, long liveTime) {
        try {
            this.set(key.getBytes(redisCode), value.getBytes(redisCode), liveTime);
        } catch (Exception e) {
            logger.error("redisSet异常", e);
        }
    }

    /**
     * 删除keys
     * @param keys
     *            k
     * @return l
     */
    public long del(final String... keys) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (int i = 0; i < keys.length; i++) {
                    try {
                        result += connection.del(keys[i].getBytes(redisCode));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        logger.error("redisDel异常", e);
                    }
                }
                return result;
            }
        });
    }

    /**
     * 根据key获取value
     * @param key
     *            l
     * @return s
     */
    public String get(final String key) {
        return redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    byte[] data = connection.get(key.getBytes(redisCode));
                    if (data != null) {
                        return new String(data, redisCode);
                    }
                } catch (Exception e) {
                    logger.error("redisGet异常", e);
                }
                return null;
            }
        });
    }

    /**
     * 判断key是否存在
     * @param key
     *            k
     * @return b
     */
    public boolean exists(final String key) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) {
                try {
                    return connection.exists(key.getBytes(redisCode));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.error("redisExists异常", e);
                }
                return false;
            }
        });
    }

    /**
     * 存HSET的field 字节
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hset(final byte[] key, final byte[] field, final byte[] value) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                try {
                    return connection.hSet(key, field, value);
                } catch (Exception e) {
                    logger.error("redis hset error", e);
                }
                return null;
            }
        });
    }

    /**
     * 存HSET的field 字符
     * @param key
     * @param field
     * @param value
     * @return
     * @throws Exception
     */
    public boolean hset(String key, String field, String value) throws Exception {
        try {
            return this.hset(key.getBytes(redisCode), field.getBytes(redisCode), value.getBytes(redisCode));
        } catch (Exception e) {
            logger.error("redis boolean hset error", e);
        }
        return false;
    }

    /**
     * 获取HSET的field值 传入字节
     * @param key
     * @param field
     * @return
     */
    public String hget(final byte[] key, final byte[] field) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) {
                byte[] value = connection.hGet(key, field);
                if (value != null) {
                    try {
                        return new String(value, redisCode);
                    } catch (Exception e) {
                        logger.error("redisHget异常", e);
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }
        });
    }

    /**
     * 获取HSET的field值 传入字符
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) throws Exception {
        try {
            return this.hget(key.getBytes(redisCode), field.getBytes(redisCode));
        } catch (Exception e) {
            logger.error("redis string hget error", e);
        }
        return null;
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key
     *            键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hget(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            logger.error("redis map hget error", e);
        }
        return null;
    }

    /**
     * 幂等性校验
     * @param key
     * @param expTime
     * @return
     */
    public boolean setNx(final String key, final String value, long expTime) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) {
                try {
                    boolean result = connection.setNX(key.getBytes(redisCode), value.getBytes(redisCode));
                    if (result) {
                        connection.expire(key.getBytes(redisCode), expTime);
                    }
                    return result;
                } catch (Exception e) {
                    logger.error("redisSetNx异常", e);
                }
                return false;
            }
        });
    }

    /**
     * 获取key在单位时间内出现的次数
     * @param key
     * @param expTime
     *            单位为秒
     * @return
     */
    public Long incr(String key, long expTime) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) {
                try {
                    long count = connection.incr(key.getBytes(redisCode));
                    if (count == 1) {
                        connection.expire(key.getBytes(redisCode), expTime);
                    }
                    return count;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.error("redisIncr异常", e);
                }
                return null;
            }
        });
    }

    /**
     * 检验key是否过期
     * @param key
     * @return
     */
    public Long ttl(String key) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) {
                try {
                    long count = connection.ttl(key.getBytes(redisCode));
                    return count;
                } catch (Exception e) {
                    logger.error("redisttl异常", e);
                }
                return null;
            }

        });
    }

    /**
     * 设置超时时间，单位秒
     * @param key
     * @param timeout
     */
    public void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * (单位秒) <=0 不过期 缓存地理位置信息
     */
    public boolean geoAdd(String key, double x, double y, String member, long time) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            geoOps.add(key, new Point(x, y), member);
            if (time > 0)
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } catch (Throwable t) {
            logger.error("缓存[" + key + "]" + "失败, point[" + x + "," + y + "], member[" + member + "]" + ", error[" + t + "]");
        }
        return true;
    }

    /**
     * 缓存地理位置信息(单位秒) <=0 不过期
     */
    public boolean geoAdd(String k, Iterable<RedisGeoCommands.GeoLocation<String>> locations, long time) {
        try {
            for (RedisGeoCommands.GeoLocation<String> location : locations) {
                geoAdd(k, location.getPoint().getX(), location.getPoint().getY(), location.getName(), time);
            }
        } catch (Throwable t) {
            logger.error("缓存[" + k + "]" + "失败" + ", error[" + t + "]");
        }
        return true;
    }

    /**
     * 移除地理位置信息
     */
    public boolean removeGeo(String key, String... members) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            geoOps.remove(key, members);
        } catch (Throwable t) {
            logger.error("移除[" + key + "]" + "失败" + ", error[" + t + "]");
        }
        return true;
    }

    /**
     * 根据两个成员计算两个成员之间距离
     */
    public Distance geoDist(String key, String member1, String member2) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            return geoOps.distance(key, member1, member2);
        } catch (Throwable t) {
            logger.error("计算距离[" + key + "]" + "失败, member[" + member1 + "," + member2 + "], error[" + t + "]");
        }
        return null;
    }

    /**
     * 根据key和member获取这些member的坐标信息
     */
    public List<Point> getGeo(String key, String... members) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            return geoOps.position(key, members);
        } catch (Throwable t) {
            logger.error("获取坐标[" + key + "]" + "失败]" + ", error[" + t + "]");
        }
        return null;
    }

    /**
     * 通过给定的坐标和距离(km)获取范围类其它的坐标信息
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius(String key, double x, double y, double distance, Sort.Direction direction, long limit) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            // 设置geo查询参数
            RedisGeoCommands.GeoRadiusCommandArgs geoRadiusArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
            geoRadiusArgs = geoRadiusArgs.includeCoordinates().includeDistance();// 查询返回结果包括距离和坐标
            if (Sort.Direction.ASC.equals(direction)) {// 按查询出的坐标距离中心坐标的距离进行排序
                geoRadiusArgs.sortAscending();
            } else if (Sort.Direction.DESC.equals(direction)) {
                geoRadiusArgs.sortDescending();
            }
            geoRadiusArgs.limit(limit);// 限制查询数量
            GeoResults<RedisGeoCommands.GeoLocation<String>> radiusGeo = geoOps.radius(key, new Circle(new Point(x, y), new Distance(distance, RedisGeoCommands.DistanceUnit.KILOMETERS)), geoRadiusArgs);
            return radiusGeo;
        } catch (Throwable t) {
            logger.error("通过坐标[" + x + "," + y + "]获取范围[" + distance + "km的其它坐标失败]" + ", error[" + t + "]");
        }
        return null;
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key
     *            键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        try {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            return map;
        } catch (Exception e) {
            logger.error("获取hash表数据异常", e);
        }
        return null;
    }

    /**
     * HashSet
     * @param key
     *            键
     * @param map
     *            对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, T> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            logger.error("redis boolean hmget error", e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key
     *            键
     * @param map
     *            对应多个键值
     * @param time
     *            时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, T> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("redishmset异常", e);
            return false;
        }
    }

    /**
     * HashGet
     * @param key
     *            键 不能为null
     * @param item
     *            项 不能为null
     * @return 值
     */
    public Object hgetObject(String key, String item) {
        try {
            Object object = redisTemplate.opsForHash().get(key, item);
            return object;
        } catch (Exception e) {
            logger.error("获取hash表异常===》", e);
        }
        return null;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public String getSet(final byte[] key, final byte[] value) {
        return redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) {
                try {
                    byte[] data = connection.getSet(key, value);
                    if (data != null) {
                        return new String(data, redisCode);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.error("getSet异常", e);
                }
                return null;
            }
        });
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public String getSet(final String key, final String value) {
        try {
            return this.getSet(key.getBytes(redisCode), value.getBytes(redisCode));
        } catch (Exception e) {
            logger.error("redis String getSet error", e);
        }
        return null;
    }

    /**
     * 获取list缓存的内容
     * @param key
     *            键
     * @param start
     *            开始
     * @param end
     *            结束 0 到 -1代表所有值
     * @return
     */
    public List getList(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logger.error("redis getList error", e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key
     *            键
     * @return
     */
    public long getListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            logger.error("redis getListSize error", e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key
     *            键
     * @param index
     *            索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public String getIListByIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logger.error("redis getIListByIndex error", e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     * @param key
     *            键
     * @param value
     *            值
     * @return
     */
    public boolean setList(String key, String value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            logger.error("redis setList error", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key
     *            键
     * @param value
     *            值
     * @param time
     *            时间(秒)
     * @return
     */
    public boolean setList(String key, String value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("redis boolean setList error", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key
     *            键
     * @param value
     *            值
     * @return
     */
    public boolean setAllList(String key, List<String> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            logger.error("redis setAllList error", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key
     *            键
     * @param value
     *            值
     * @param time
     *            时间(秒)
     * @return
     */
    public boolean setAllList(String key, List<String> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("redis boolean setAllList error", e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key
     *            键
     * @param index
     *            索引
     * @param value
     *            值
     * @return
     */
    public boolean updateListByIndex(String key, long index, String value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            logger.error("redis updateListByIndex error", e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key
     *            键
     * @param count
     *            移除多少个
     * @param value
     *            值
     * @return 移除的个数
     */
    public long removeListByValue(String key, long count, T value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            logger.error("redis removeListByValue error", e);
            return 0;
        }
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<?, ?> template = new RedisTemplate<>();
        RedisSerializer<String> stringSerializer = template.getStringSerializer();
        template.setConnectionFactory(factory);
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(redisObjectSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(redisObjectSerializer);
        return template;
    }

    public String eval(String luaStr, List<String> keys, Object... args) {
        try {
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
            redisScript.setResultType(String.class);
            redisScript.setScriptText(luaStr);
            String res = redisTemplate.execute(redisScript, new StringRedisSerializer(), new StringRedisSerializer(), keys, args);
            return res;
        } catch (Exception e) {
            logger.error("redis eval error", e);
            return "";
        }
    }
}
