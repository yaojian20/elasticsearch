package com.yao.elasticsearch.base;

import com.yao.elasticsearch.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by yaojian on 2021/11/15 15:17
 *
 * @author
 */

@Service
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {

    protected static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // 默认缓冲限制为100MB，此处修改为30MB。
        builder.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Resource
    private RestHighLevelClient restHighLevelClient;


    @Override
    public void createIndexRequest(String index) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index)
                .settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 0));
        try {
            CreateIndexResponse response = restHighLevelClient.indices().create(createIndexRequest, COMMON_OPTIONS);
            log.info(" 所有节点确认响应 : {}", response.isAcknowledged());
            log.info(" 所有分片的复制未超时 :{}", response.isShardsAcknowledged());
        } catch (IOException e) {
            log.error("创建索引库【{}】失败", index, e);
        }
    }

    @Override
    public void deleteIndexRequest(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse response = restHighLevelClient.indices().delete(request, COMMON_OPTIONS);
            System.out.println(response.isAcknowledged());
        } catch (IOException e) {
            log.error("删除索引库【{}】失败", index, e);
        }
    }

    @Override
    public void updateRequest(String index, String id, Object object) {
        UpdateRequest updateRequest = new UpdateRequest(index, id);
        updateRequest.doc(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            restHighLevelClient.update(updateRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("更新索引文档 {" + index + "} 数据 {" + object + "} 失败", e);
        }
    }

    @Override
    public void insertRequest(String index, String id, Object object) {
        IndexRequest indexRequest = new IndexRequest(index).id(id).source(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("创建索引文档 {" + index + "} 数据 {" + object + "} 失败", e);
        }

    }

    @Override
    public void deleteRequest(String index, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        try {
            restHighLevelClient.delete(deleteRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("删除索引文档 {" + index + "} 数据id {" + id + "} 失败", e);
        }
    }


    @Override
    public void bulkPutIndex(String index,List list) throws IOException {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        int size = list.size();
        BulkRequest request = new BulkRequest();
        for (int i = 0; i < size; i++) {
            Object object = list.get(i);
            //这里必须每次都使用new IndexRequest(index,type),不然只会插入最后一条记录（这样插入不会覆盖已经存在的Id，也就是不能更新）
            //request.add(new IndexRequest(index,type).opType("create").id(map.remove("id").toString()).source(map));
            IndexRequest indexRequest = new IndexRequest(index).source(BeanUtil.beanToMap(object), XContentType.JSON);
            request.add(indexRequest);
        }
        restHighLevelClient.bulk(request, COMMON_OPTIONS);
    }

    @Override
    public void bulkPutIndex(String index, List list, String idParam) throws IOException {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        int size = list.size();
        BulkRequest request = new BulkRequest();
        for (int i = 0; i < size; i++) {
            Object object = list.get(i);
            //这里必须每次都使用new IndexRequest(index,type),不然只会插入最后一条记录（这样插入不会覆盖已经存在的Id，也就是不能更新）
            //request.add(new IndexRequest(index,type).opType("create").id(map.remove("id").toString()).source(map));
            Map map = BeanUtil.beanToMap(object);
            String id = map.get(idParam).toString();
            if (id == null || id.length() == 0){
                IndexRequest indexRequest = new IndexRequest(index).source(map, XContentType.JSON);
                request.add(indexRequest);
            } else {
                IndexRequest indexRequest = new IndexRequest(index).id(id).source(map, XContentType.JSON);
                request.add(indexRequest);
            }

        }
        restHighLevelClient.bulk(request, COMMON_OPTIONS);
    }

}
