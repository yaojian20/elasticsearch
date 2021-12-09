package com.yao.elasticsearch.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yaojian on 2021/11/16 14:44
 *
 * @author
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog {

    /**
     * 文章id
     */
    private String id;


    /**
     * 文章标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 文章类型,原创,转载
     */
    private String type;

    /**
     *浏览量
     */
    private long viewCount;

    /**
     * 点赞量
     */
    private long likesCount;

    /**
     * 用户类型
     */
    private String userType;


}
