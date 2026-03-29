package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    //=======================================================================================
    //接下来就是六个分类管理的接口
    @PostMapping
    public Result addCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    //下面这个就是分类分页
    @GetMapping("/page")
    public Result<PageResult> page(@ModelAttribute CategoryPageQueryDTO categoryPageQueryDTO){
         PageResult pageResult=categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    public Result startOrLimit(@PathVariable Integer status, Long id){
        categoryService.startOrLimit(status,id);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(Long id){
        categoryService.delete(id);
        return Result.success();

    }
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @GetMapping("/list")
   public Result<List<Category>> list(Integer type){
        List<Category> list=categoryService.list(type);
                return Result.success(list);
    }





}
