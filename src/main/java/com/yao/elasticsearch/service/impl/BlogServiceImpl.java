package com.yao.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.yao.elasticsearch.base.ElasticSearchService;
import com.yao.elasticsearch.bean.Blog;
import com.yao.elasticsearch.bean.City;
import com.yao.elasticsearch.service.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaojian on 2021/11/16 15:31
 *
 * @author
 */
@Service
@Slf4j
public class BlogServiceImpl implements BlogService {

    private static final String INDEX_NAME = "blog";

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void insert(Blog blog) {

        elasticSearchService.insertRequest(INDEX_NAME,blog.getId(),blog);

    }

    @Override
    public void batchInsert(List<Blog> blogs) {
        try {
            elasticSearchService.bulkPutIndex(INDEX_NAME,blogs,"id");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test(){
        String searchContent = "IPhone";
        String index = "10000";
        SearchRequestBuilder searchBuilder = null;
        //??????
        searchBuilder.setFrom(0).setSize(10);
        //explain???true???????????????????????????????????????????????????????????????????????????
        searchBuilder.setExplain(true);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // ?????? title????????????IPhone?????????
        queryBuilder.must(QueryBuilders.matchQuery("title", searchContent));

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[3];

        //????????????1???????????????????????????????????? -- ????????????Weight
        ScoreFunctionBuilder<WeightBuilder> scoreFunctionBuilder = new WeightBuilder();
        scoreFunctionBuilder.setWeight(2);
        QueryBuilder termQuery = QueryBuilders.termQuery("categoryName", "????????????");
        FunctionScoreQueryBuilder.FilterFunctionBuilder category = new FunctionScoreQueryBuilder.FilterFunctionBuilder(termQuery, scoreFunctionBuilder);
        filterFunctionBuilders[0] = category;

        // ????????????2???????????????????????? --???????????? FieldValueFactor
        ScoreFunctionBuilder<FieldValueFactorFunctionBuilder> fieldValueScoreFunction = new FieldValueFactorFunctionBuilder("salesVolume");
        ((FieldValueFactorFunctionBuilder) fieldValueScoreFunction).factor(1.2f);
        FunctionScoreQueryBuilder.FilterFunctionBuilder sales = new FunctionScoreQueryBuilder.FilterFunctionBuilder(fieldValueScoreFunction);
        filterFunctionBuilders[1] = sales;

        // ?????????????????????????????????  --random_score
        ScoreFunctionBuilder<RandomScoreFunctionBuilder> randomScoreFilter = new RandomScoreFunctionBuilder();
        ((RandomScoreFunctionBuilder) randomScoreFilter).seed(2);
        FunctionScoreQueryBuilder.FilterFunctionBuilder random = new FunctionScoreQueryBuilder.FilterFunctionBuilder(randomScoreFilter);
        filterFunctionBuilders[2] = random;

        // ??????????????? FunctionScore
        FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery(queryBuilder, filterFunctionBuilders)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM).boostMode(CombineFunction.SUM);
        searchBuilder.setQuery(query);

        SearchResponse response = searchBuilder.execute().actionGet();
        SearchHits hits = response.getHits();
        String searchSource;
        for (SearchHit hit : hits)
        {
            searchSource = hit.getSourceAsString();
            System.out.println(searchSource);
        }
        //        long took = response.getTook().getMillis();
        long total = hits.getTotalHits().value;
        System.out.println(total);

    }

    @Override
    public List<Blog> searchBlog(String searchContent) {
        //????????????
        //????????????(ln(1+(0.1*?????????))+vip??????+????????????)*????????????
        //vip??????????????????10???,??????????????????2???
        ScoreFunctionBuilder<?> scoreFunctionBuilder = ScoreFunctionBuilders.fieldValueFactorFunction("viewCount").modifier(FieldValueFactorFunction.Modifier.LN1P).factor(0.1f);
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("userType.keyword", "vip"), ScoreFunctionBuilders.weightFactorFunction(1)),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("type.keyword", "original"), ScoreFunctionBuilders.weightFactorFunction(1)),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(scoreFunctionBuilder)
        };
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchAllQuery());
        //??????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // ??????????????? FunctionScore
        //MULTIPLY??????
        FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery(boolQuery, filterFunctionBuilders)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM).boostMode(CombineFunction.MULTIPLY);
        //FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery(boolQuery);
        searchSourceBuilder.query(query)
                .sort("_score", SortOrder.DESC);
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.source(searchSourceBuilder);
        log.info("dsl:"+searchSourceBuilder.toString());
        SearchResponse searchResults = null;
        try {
            searchResults = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResults.getHits();
            List<Blog> blogs = new ArrayList<>();
            for (SearchHit hit : hits) {
                System.out.print("?????????????????????: "+hit.getScore());
                System.out.print(", ????????????:=====>"+hit.getSourceAsString());
                Blog blog = JSON.parseObject(hit.getSourceAsString(),Blog.class);
                blogs.add(blog);
                System.out.println(", ??????????????????:"+hit.getSourceAsMap().get("title"));
                System.out.println("=================================================");
            }
            return  blogs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateBlog(Blog blog) {

    }

    @Override
    public void deleteBlog(Blog blog) {

    }
}
