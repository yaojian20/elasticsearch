package com.yao.elasticsearch.base;

import org.elasticsearch.action.bulk.BulkRequest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by yaojian on 2021/11/15 15:15
 *
 * @author
 */
public interface ElasticSearchService {

    /**
     * 创建索引库
     */
    void createIndexRequest(String index);

    /**
     * 删除索引库
     */
    void deleteIndexRequest(String index);

    /**
     * 更新索引文档
     */
    void updateRequest(String index, String id, Object object);

    /**
     * 新增索引文档
     */
    void insertRequest(String index, String id, Object object);

    /**
     * 批量新增索引(没有指定文档id)
     * @param index
     * @param list
     * @throws IOException
     */
    void bulkPutIndex(String index, List list) throws IOException;

    /**
     * 批量新增索引(指定文档id->跟实体类指定属性的值相同)
     * @param index
     * @param list
     * @throws IOException
     */

    void bulkPutIndex(String index, List list, String idParam) throws IOException;

        /**
         * 删除索引文档
         */
    void deleteRequest(String index, String id);



}
