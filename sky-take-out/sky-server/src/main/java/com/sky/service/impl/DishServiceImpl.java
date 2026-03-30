package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    //有个问题就是我这里的dto里面还有一个口味的dto，这边可能会用到两张表，所以下面会有一个注解就是@Transaction
    //这个就是假设你同时操作的是两张表，然后就是保持着事务的一致性，要么全部成功，要么都是失败
   @Transactional
    public void add(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.add(dish);
       Long id = dish.getId();



       //然后这里就是对口味进行处理,下面这个flavors就是一个集合，然后里面放的就是dishflavor里面的四个参数
       List<DishFlavor> flavors = dishDTO.getFlavors();
       //然后这个口味可以是没有的，添加菜品可以选择口味，也可以不选择口味，所以可以加一个if 判断
       if(flavors != null && flavors.size()>0){
           for(DishFlavor dishFlavor : flavors){
               dishFlavor.setDishId(id);
           }

           dishFlavorMapper.insertBatch(flavors);

           //因为我dishflavor里面那四个对象数据被放在集合中了，原本可以是遍历集合，拿到里面的每一个对象
           //但是这里可以直接传过去集合，让他自己批量添加
       }



   }
}
