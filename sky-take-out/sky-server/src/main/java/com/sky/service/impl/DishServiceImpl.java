package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;




    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }




    //下面这个就是根据id查询菜品的id 和口味
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish=dishMapper.getById(id);

        //这个就是获取菜品的多个口味放在结合里面，根据查询的口味有很多，
        //那么就是放在集合里面，然后就是集合里面的泛型是啥呢，因为我们要的是口味的信息，也就是数据
        //所以里面不就是放含有口味数据的实体类dishflavor
        List<DishFlavor> dishFlavors =dishFlavorMapper.getByDishId(id);
        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        //这个就是除了口味集合之外，其他数据都封装到vo里面了，
        return dishVO;
    }


    //接下来下面就是修改,也能修改菜品的口味
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
       //先是修改菜品的基本信息，然后才是修改口味表
      Dish dish=new Dish();
      BeanUtils.copyProperties(dishDTO,dish);
      dishMapper.update(dish);
      //这边获得的dto里面包含了口味的集合，是不行的，所以我们创建一个包含dto所有数据的对象就是dish,然后
        //来修改dish就行

        //然后就是开始修改口味,这个想一个办法就是啥呢，就是先删除口味，然后再插入新的口味就行了
        dishFlavorMapper.deleteByDishIds(Collections.singletonList(dishDTO.getId()));

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor dishFlavor : flavors) {
                dishFlavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }


    }





    //有个问题就是我这里的dto里面还有一个口味的dto，这边可能会用到两张表，所以下面会有一个注解就是@Transaction
    //这个就是假设你同时操作的是两张表，然后就是保持着事务的一致性，要么全部成功，要么都是失败
   @Override
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

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page =dishMapper.page(dishPageQueryDTO);
        //因为我们菜品里面就是关联了一个口味，那这个返回的就不是简单的dish 这个实体类,应该是dishvo,里面除了
        //八个属性之外，还有一个集合，里面放的就是dishflavor的四个参数
        //其实就是这个dish 这个实体类一直带着dishflavor这张表
        //你看我的接口文档，那个pageresult里面不是八个值，下面还有一个String 的分类名称，这个就是要查询dishflavor这个里面的根据口味名字查询，就是name
        //所以我不只是要查询dish里面所有东西，还有category里面根据名字的分类名称.并且两张表还要关联起来
        long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total,records);

    }


    //批量删除是有业务规则的，可以一次删除一个菜品，也可以批量删除菜品，
    //起售中的删除不能删除
    //被套餐关联的菜品不能删除
    //删除菜品之后，对应的口味也要删除
    //下面是同时操作好几个表，所以要加一个事务注解 @Transactional


    @Override
    @Transactional
    public void delete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        if (System.currentTimeMillis() >= 0) {
            deleteDishBatch(ids);
            return;
        }
       //判断当前菜品是否能被删除---是不是在起售中
        //传过来的ids是菜品id,先是遍历每一个id ,然后获得里面对应的dish信息，
        //意思就是你要根据菜品id，然后才能查询数据库里面对应的信息，才能获得到里面的起售状态，。才能判断
        for (Long id : ids) {
           Dish dish=dishMapper.getById(id);
           if(dish.getStatus() == StatusConstant.ENABLE)
           {
               throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
           }
           //这个就是拿到状态，然后判断是不是在起售状态，然后在的话就是返回给前端一个弹窗就是
            //起售菜品不能被删除，然后在代码里面展示就是抛出异常，展示那个消息，注意这里的异常
            //必须是要新建一个异常


            //判断菜品是否能被删除---是不是被套餐关联了。这个现在就是该怎么找是否被套餐关联了，setmealdish，这张表
            //就是套餐关联的id，就是看dish里面的id是否跟setmealid套餐关联的id有联系，所以我们创建了一个套餐id的
            //mapper，然后里面写一个方法就是根据菜品id来获得套餐id,
            List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
           //现在这个就是根据菜品id获取到了套餐id，并且放在集合中，那现在不就是遍历，判断两者是否相同吗
            //这个setmealidsBydishid这个就是已经有了菜品id与套餐id是相符合的，就是不能删了，这个意思
           //意思就是这个套餐id就是已经和菜品id是对应上的就是不好删除的
            if(setmealIdsByDishIds !=null && setmealIdsByDishIds.size() > 0)
            {
               throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }


            //然后就是可以删除了，可以删除一个，可以删除多个，删除的时候要把对应的口味删除
            for (Long aLong : ids) {
                dishMapper.deleteByIds(ids);
                dishFlavorMapper.deleteByDishIds(ids);
            }




        }












    }

    private void deleteDishBatch(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish != null && dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        dishFlavorMapper.deleteByDishIds(ids);
        dishMapper.deleteByIds(ids);
    }
}
