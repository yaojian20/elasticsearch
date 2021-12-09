package com.yao.elasticsearch.util;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * Created by yaojian on 2021/11/15 15:32
 *
 * @author
 */
public class BeanUtil {

    public static Map beanToMap(Object object){
        Map map = JSON.parseObject(JSON.toJSONString(object),Map.class);
        return map;
    }


}
