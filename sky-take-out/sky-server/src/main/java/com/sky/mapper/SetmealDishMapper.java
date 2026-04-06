package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


//其实现在总结一下就是我们一开始传过来的是菜品id 没错，然后注意这个是ids，是放在集合里面的，然后现在就是看删除的第二个条件
//就是这个套餐和菜品已经关联的id是已经有了的，就是setmealdish里面的,setmeal_id,然后这个就是根据后面的那个dish_id
//这个集合里面的菜品id选出来的，然后我在mapper里面的写的就是根据菜品id来找出来setmeal_id这个其实就是根据数据库语句写出来的
//所以数据库语句很重要，然后你看我还没写，不就是在service获得了这个setmeal_id，就是关联的才能判断是否为空，然后
//就是抛出异常不能删，其实这个mapper这个接口定义的方法不仅和service关联，同时也是满足xml里面的语句，然后你看在xml
//里面不就是详细写了，我要获得是setmeal_id,然后where这个dish_id 这个是菜品id 不就是遍历那个集合获得的吗，
//其实意思就是关联id就是要根据菜品id,然后这个菜品id是在结合里面的


@Mapper
public interface SetmealDishMapper {

 //这个sql语句就是要写一个什么意思呢，就是这个套餐id必须要与菜品id对应上，才知道这个被套餐关联了，是不能删除的
 //其实sql里面写的就会是什么呢，就是你看现在这个setmealdish这张表，setmealid就是菜品id和套餐关联后的id
 //就是在service判断这个不是null的这个，就是菜品被关联了，不能删，然后菜品既然能被关联，就是setmealdish里面的
 //dish_id和菜品里面的是对应的，根据这个写sql语句
 //注意一下这个setmealdish里面的的dish_id不是单独的一个，一个，而是一个集合，然后里面有好多菜品的id
 //这个虽然没有告诉你，但是这个就是要先了解表的参数，这个dish_id就是代表着一个集合里面都是菜品的id
 //因为一开始传过来的就是放在集合里面的菜品id
 List<Long> getSetmealIdsByDishIds(@Param("dishIds") List<Long> dishIds);
}
