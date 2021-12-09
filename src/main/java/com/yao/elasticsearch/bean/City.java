package com.yao.elasticsearch.bean;


import java.io.Serializable;

/**
 * Created by yaojian on 2021/11/8 16:00
 *
 * @author
 */

public class City implements Serializable {

    public City(String cityId, String name, String description) {
        this.cityId = cityId;
        this.name = name;
        this.description = description;
    }

    public City() {
        this.cityId = cityId;
        this.name = name;
        this.description = description;
    }

    private String cityId;

    private String name;

    private String description;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
