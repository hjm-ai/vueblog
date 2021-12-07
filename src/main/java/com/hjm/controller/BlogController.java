package com.hjm.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjm.common.lang.Result;
import com.hjm.entity.Blog;
import com.hjm.service.BlogService;
import com.hjm.utils.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2021-11-25
 */
@RestController
public class BlogController {

    @Autowired
    BlogService blogService;

    @GetMapping("/blogs")
    public Result blogs(@RequestParam(defaultValue = "1") Integer currentPage){
        Page page=new Page(currentPage,2);
        IPage pageDate =blogService.page(page,new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.succ(pageDate);
    }

    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable(name = "id") Long id){
        Blog blog = blogService.getById(id);
        Assert.notNull(blog,"该博客已被删除");
        return Result.succ(blog);

    }

    @RequiresAuthentication
    @PostMapping("blog/edit")
    public Result edit(@Validated @RequestBody Blog blog){
        Blog temp=null;
        if (blog.getId()!=null){
            temp=blogService.getById(blog.getId());
            //只能编辑自己的文章
            Assert.isTrue(temp.getUserId().longValue()== ShiroUtil.getProfile().getId(),"没有权限编辑");
        }else {
            temp=new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);
        }
        BeanUtil.copyProperties(blog,temp,"id","userId","created","status");
        blogService.saveOrUpdate(temp);
        return Result.succ(null);
    }

}
