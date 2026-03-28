package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {


    //=========================================================================下面的是分类管理的六个接口
    @Insert("insert into category( type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values ( #{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addCategory(Category category);


    Page<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);

    void startOrLimit(Category category);


    void delete(long id);

    void update(Category category);


    List<Category> list(Integer type);
}
