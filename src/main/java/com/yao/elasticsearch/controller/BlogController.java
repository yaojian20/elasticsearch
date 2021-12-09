package com.yao.elasticsearch.controller;

import com.yao.elasticsearch.bean.Blog;
import com.yao.elasticsearch.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaojian on 2021/11/16 15:38
 *
 * @author
 */

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @RequestMapping("/batchInsert")
    public void batchInsert(){
        List<Blog> blogs = new ArrayList<>();
        Blog blog = new Blog("1001","钢铁是怎样炼成的","尼古拉·奥斯特洛夫斯基","original",1249867,182346,"normal");
        Blog blog1 = new Blog("1002","三国演义","罗贯中","original",1003245,163452,"normal");
        Blog blog2 = new Blog("1003","红楼梦","曹雪芹","original",5023487,803421,"normal");
        Blog blog3 = new Blog("1004","老人与海","海明威","original",523487,13421,"normal");
        Blog blog4 = new Blog("1005","遮天","辰东","original",623487,23421,"vip");
        Blog blog5 = new Blog("1006","神墓","辰东","original",823487,33421,"vip");
        Blog blog6 = new Blog("1007","神墓-转载","辰东","reprint",823487,33421,"vip");
        blogs.add(blog);
        blogs.add(blog1);
        blogs.add(blog2);
        blogs.add(blog3);
        blogs.add(blog4);
        blogs.add(blog5);
        blogs.add(blog6);
        blogService.batchInsert(blogs);
    }



    @RequestMapping("/search")
    public List<Blog> search(String searchContent){
        return blogService.searchBlog(searchContent);
    }



}
