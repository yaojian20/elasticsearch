package com.yao.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yao.elasticsearch.base.ElasticSearchService;
import com.yao.elasticsearch.bean.City;
import com.yao.elasticsearch.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.lucene.search.function.WeightFactorFunction;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yaojian on 2021/11/15 15:41
 *
 * @author
 */

@Service
@Slf4j
public class CityServiceImpl implements CityService {

    private static final String  INDEX = "city";

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private ElasticSearchService elasticSearchService;

    @Override
    public void insert(City city) {
        elasticSearchService.insertRequest(INDEX,city.getCityId(),city);
    }

    @Override
    public void batchInsert(List<City> cities) {
        try {
            elasticSearchService.bulkPutIndex(INDEX,cities);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<City> search(City city) {
        SearchRequest searchRequest=new SearchRequest();
        //??????dsl??????
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //??????????????????
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();

        boolQueryBuilder.should(QueryBuilders.termQuery("name.keyword",city.getName()));
        boolQueryBuilder.should(QueryBuilders.matchQuery("name","???"));

        searchSourceBuilder.query(boolQueryBuilder);
        log.info("dsl:"+searchSourceBuilder.toString());
        //?????????Request?????????
        searchRequest.indices(INDEX);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            Iterator<SearchHit> iterator = hits.iterator();
            while (iterator.hasNext()) {
                log.info("????????????:" + iterator.next().getSourceAsString());}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<City> searchByDIY(City city) {
        SearchRequest searchRequest=new SearchRequest();
        //??????dsl??????
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //??????????????????
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();

        boolQueryBuilder.should(QueryBuilders.termQuery("name.keyword",city.getName()));
        boolQueryBuilder.should(QueryBuilders.matchQuery("name","???"));

        searchSourceBuilder.query(boolQueryBuilder);
        log.info("dsl:"+searchSourceBuilder.toString());
        //?????????Request?????????
        searchRequest.indices(INDEX);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            Iterator<SearchHit> iterator = hits.iterator();
            while (iterator.hasNext()) {
                log.info("????????????:" + iterator.next().getSourceAsString());}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ?????????????????????
     *
     * ???????????????
     *
     * ????????????????????????: 3 hits
     * ?????????????????????: 100.0, ????????????:=====>{"id":1,"title":"Java?????????","type":3,"userId":1,"tags":["java"],"textContent":"?????????Java","status":1,"heat":80}, ??????????????????:null
     * =================================================
     * ?????????????????????: 1.0, ????????????:=====>{"id":3,"title":"Java?????????","type":1,"userId":1,"tags":["java"],"textContent":"?????????Java","status":1,"heat":100}, ??????????????????:null
     * =================================================
     * ?????????????????????: 1.0, ????????????:=====>{"id":2,"title":"Java?????????","type":2,"userId":1,"tags":["java"],"textContent":"?????????Java","status":1,"heat":99}, ??????????????????:null
     * =================================================
     *
     * @throws IOException
     */
    @Override
    public List<City> testSort(String name) throws IOException {

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("name.keyword", "????????????"), ScoreFunctionBuilders.weightFactorFunction(10)),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("name.keyword", "????????????"), ScoreFunctionBuilders.weightFactorFunction(5)),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("name.keyword", "????????????"), ScoreFunctionBuilders.weightFactorFunction(2))
        };
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("name", name));
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(boolQuery, filterFunctionBuilders);
        searchSourceBuilder.query(functionScoreQueryBuilder)
                .sort("_score", SortOrder.DESC);
        SearchRequest searchRequest = new SearchRequest(INDEX);
//        searchRequest.types(EsConstant.DEFAULT_TYPE);
        searchRequest.source(searchSourceBuilder);
        log.info("dsl:"+searchSourceBuilder.toString());
        SearchResponse searchResults = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResults.getHits();
        log.info("????????????????????????: "+hits.getTotalHits());
        List<City> cities = new ArrayList<>();
        for (SearchHit hit : hits) {
            log.info("?????????????????????: "+hit.getScore());
            log.info(", ????????????:=====>"+hit.getSourceAsString());
            City city = JSON.parseObject(hit.getSourceAsString(),City.class);
            cities.add(city);
            log.info(", ??????????????????:"+hit.getSourceAsMap().get("name"));
            log.info("=================================================");
        }

        return cities;

    }



    @Override
    public void delete(City city) {

    }

    @Override
    public void update(City city) {

    }
}
