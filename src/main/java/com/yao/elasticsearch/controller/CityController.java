package com.yao.elasticsearch.controller;

import com.yao.elasticsearch.bean.City;
import com.yao.elasticsearch.service.CityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaojian on 2021/11/15 15:44
 *
 * @author
 */

@RestController
@RequestMapping("/city")
public class CityController {

    @Resource
    private CityService cityService;

    @RequestMapping("/test")
    public void test(){

        List<City> cities = new ArrayList<>();
        City city = new City("1","江苏苏州","非常好的地方");
        City city1 = new City("2","江苏常州","非常好的地方");
        City city2 = new City("3","江苏南京","非常好的地方");
        City city3 = new City("4","江苏泰州","非常好的地方");
        City city4 = new City("5","江苏张家港","非常好的地方");
        City city5 = new City("6","江苏连云港","非常好的地方");
        City city6 = new City("7","安徽合肥","非常好的地方");
        City city7 = new City("8","安徽安庆","非常好的地方");
        City city8 = new City("9","安徽芜湖","非常好的地方");
        City city9 = new City("10","安徽马鞍山","非常好的地方");
        City city10 = new City("11","安徽铜陵","非常好的地方");
        City city11 = new City("12","浙江杭州","非常好的地方");
        City city12 = new City("13","浙江湖州","非常好的地方");
        cities.add(city);
        cities.add(city1);
        cities.add(city2);
        cities.add(city3);
        cities.add(city4);
        cities.add(city5);
        cities.add(city6);
        cities.add(city7);
        cities.add(city8);
        cities.add(city9);
        cities.add(city10);
        cities.add(city11);
        cities.add(city12);

        cityService.batchInsert(cities);


    }


    @RequestMapping("/search")
    public List<City> search(@RequestParam("id")String id,@RequestParam("name")String name){
        City city = new City();
        city.setCityId(id);
        city.setName(name);
        return cityService.search(city);
    }

    @RequestMapping("/sort")
    public List<City> search(@RequestParam("name")String name) throws IOException {
        return cityService.testSort(name);
    }




}
