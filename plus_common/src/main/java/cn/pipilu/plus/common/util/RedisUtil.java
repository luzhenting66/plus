package cn.pipilu.plus.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@Component
public class RedisUtil {
    private final Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间（秒）
     * @return boolean
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                return redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            throw new IllegalArgumentException("输入的时间不合法,请检查过期时间");
        } catch (Exception e) {
            logger.error("redis 设置发生错误", e);
            return false;
        }
    }

    /**
     * 获取键的过期时间
     *
     * @param key 键
     * @return long
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return boolean
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 批量删除键
     *
     * @param key 键
     */
    public void del(String... key) {
        if (ArrayUtils.isEmpty(key)) {
            return;
        }
        redisTemplate.delete(CollectionUtils.arrayToList(key));
    }
    //================ String ============================

    /**
     * 获取键值
     *
     * @param key
     * @return Object
     */
    public Object get(String key) {
        return StringUtils.isBlank(key) ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     * @param time  过期时间，单位：秒
     * @return boolean
     */
    public boolean set(String key, Object value, long time) {
        if (time < 0) {
            throw new IllegalArgumentException("输入的时间不合法,请检查过期时间");
        }
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key 键
     * @return long
     */
    public long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 按制定步长递增
     *
     * @param key 键
     * @param by  步长
     * @return long
     */
    public long incrBy(String key, int by) {
        if (by < 0) {
            throw new IllegalArgumentException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, by);
    }

    /**
     * 递减
     *
     * @param key 键
     * @return long
     */
    public long decr(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 递减
     *
     * @param key 键
     * @param by  步长
     * @return long
     */
    public long decrBy(String key, int by) {
        if (by <= 0) {
            throw new IllegalArgumentException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, by);
    }

    // =============================== hash 类型 ==============================

    /**
     * 获取键的字段值
     *
     * @param key   键
     * @param field 字段
     * @return Object
     */
    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获得键的值
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置键
     *
     * @param key 键
     * @param map 值
     * @return boolean
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 设置键
     *
     * @param key  键
     * @param map  值
     * @param time 过期时间
     * @return boolean
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {

        if (time < 0) {
            throw new IllegalArgumentException("输入的时间不合法,请检查过期时间");
        }
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 向 key 指定字段添加值，如果不存在，将创建
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     * @return boolean
     */
    public boolean hset(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 删除键中的字段
     *
     * @param key   键
     * @param field 字段
     */
    public void hdel(String key, Object... field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 判断 键中是否包含某个字段
     *
     * @param key   键
     * @param field 字段
     * @return boolean
     */
    public boolean hHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 向 hash表，某个字段递增
     *
     * @param key   键
     * @param field 字段
     * @param by    步长
     * @return
     */
    public double hincr(String key, String field, double by) {
        return redisTemplate.opsForHash().increment(key, field, by);
    }

    /**
     * hash 表某个字段递减
     *
     * @param key   键
     * @param field 字段
     * @param by    要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String field, double by) {
        return redisTemplate.opsForHash().increment(key, field, -by);
    }

    // ==================== Set ==================

    /**
     * 获取key
     *
     * @param key 键
     * @return Set
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return null;
        }
    }

    /**
     * 键中是否存在 某一个值
     *
     * @param key   键
     * @param value 值
     * @return boolean
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 向 key 中添加值
     *
     * @param key
     * @param values
     * @return
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return 0;
        }
    }

    /**
     * 向 key 中添加值
     *
     * @param key
     * @param time
     * @param values
     * @return
     */
    public long sSetAndTime(String key, long time, Object... values) {
        if (time < 0) {
            throw new IllegalArgumentException("输入的时间不合法,请检查过期时间");
        }

        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) expire(key, time);
            return count;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object ...values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return 0;
        }
    }
//===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public long lGetListSize(String key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key,long index){
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        if (time < 0) {
            throw new IllegalArgumentException("输入的时间不合法,请检查过期时间");
        }
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index,Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key,long count,Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            logger.error("redis 发生错误", e);
            return 0;
        }
    }

}
