package com.yao.elasticsearch.service;

import com.yao.elasticsearch.bean.Blog;

import java.util.List;

/**
 * Created by yaojian on 2021/11/16 15:24
 *
 * @author
 */
public interface BlogService {

    void insert(Blog blog);

    void batchInsert(List<Blog> blogs);

    List<Blog> searchBlog(String searchContent);

    void updateBlog(Blog blog);

    void deleteBlog(Blog blog);

}
