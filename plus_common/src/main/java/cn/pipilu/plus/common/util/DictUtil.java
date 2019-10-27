package cn.pipilu.plus.common.util;

import cn.pipilu.plus.common.config.SpringContextHolder;
import cn.pipilu.plus.common.service.CacheManagerService;
import cn.pipilu.plus.common.vo.DictEnumItemResp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class DictUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictUtil.class);
    private static CacheManagerService cacheManagerService = SpringContextHolder.getBean(CacheManagerService.class);
    public static String getDictLabel(String key,String dictType,String defaultLabel){
        if (StringUtils.isBlank(key) || StringUtils.isBlank(dictType))
            return defaultLabel;
        List <DictEnumItemResp> list = cacheManagerService.getCache(dictType);
        LOGGER.info("{},key:{},查询缓存，{}",dictType,key,list);
        if (CollectionUtils.isEmpty(list)){
           return defaultLabel;
        }
        for (DictEnumItemResp item : list) {
            if (Objects.equals(key,item.getDictItemKey())){
                return item.getDictItemValue();
            }
        }
        return defaultLabel;
    }

    public static String getDictValue(String label,String dictType,String defaultValue){
        if (StringUtils.isBlank(label) || StringUtils.isBlank(dictType))
            return defaultValue;
        List <DictEnumItemResp> list = cacheManagerService.getCache(dictType);
        LOGGER.info("{},key:{},查询缓存，{}",dictType,label,list);
        if (CollectionUtils.isEmpty(list)){
            return defaultValue;
        }
        for (DictEnumItemResp item : list) {
            LOGGER.info("查询的标签：{}, 缓存数据的key-value:{},{}",label,item.getDictItemKey(),item.getDictItemValue());
            if (Objects.equals(label,item.getDictItemValue())){
                return item.getDictItemKey();
            }
        }
        return defaultValue;
    }

    public static boolean containsKey(String key){
        if (StringUtils.isBlank(key))
            return false;

        return cacheManagerService.containsKey(key);
    }
}
