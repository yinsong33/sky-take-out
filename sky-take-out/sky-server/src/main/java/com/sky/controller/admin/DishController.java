package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public Result add(@RequestBody DishDTO dishDTO) {
        dishService.add(dishDTO);
        return Result.success();
    }


    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

     //他这里有三个删除，就是可以单独删除菜品，或者是批量删除，还有就是删除菜品，也会把对应的口味删除
     @DeleteMapping
     public Result delete(@RequestParam("ids") List<Long> ids){
        //！！！！！这个注解就是可以自动识别结合里面的内容，一次识别
        //这里有个细节就是，他传过来的参数是字符串类型的，然后里面就是菜品的id，然后
         //我们可以用string来包装，也可以用集合，然后在集合里面放的就是菜品id，然后
         //加一个注解表示后面会依次遍历，拿到里面的字符串id
         dishService.delete(ids);
         return Result.success();

     }

    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);
        return Result.success();
    }

    //接下来就是修改，注意一下你修改菜品，要先获得菜品的全部信息，也就是先查询对应id的全部信息，然后在进行修改
    //而且你菜品的查询带来的还有对应的口味，也算在里面，
    //因为就是你看接口文档里面的参数就是dishvo，然后就是里面还有一个就是口味的相关数据，然后你在页面也是一样
    //修改不是要查询吗，查询的不仅是菜品数据，然后i口味也要，也是可以修改的，然后就查询分两次一次查数据
    //一次查口味
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
       DishVO dishVO=dishService.getByIdWithFlavor(id);
       return Result.success(dishVO);
    }

    //查询到接下来就是修改
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }






}
