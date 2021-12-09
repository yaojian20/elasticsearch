package com.yao.elasticsearch.service;

import com.yao.elasticsearch.bean.City;

import java.io.IOException;
import java.util.List;

/**
 * Created by yaojian on 2021/11/15 15:38
 *
 * @author
 */
public interface CityService {

    void insert(City city);

    void batchInsert(List<City> cities);

    List<City> search(City city);

    void delete(City city);

    void update(City city);

    List<City> testSort(String name) throws IOException;


}
