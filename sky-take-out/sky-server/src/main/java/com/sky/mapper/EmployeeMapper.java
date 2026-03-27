package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);


    @Insert("insert into employee(name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Employee employee);
    //注意因为我这里就是一个简单的插入数据，insert，不是一个动态语句，没有那么麻烦，直接用注解表示我就是一个添加
    //不用再对应的xml里面写了，直接在注解里面写


    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
    //这个就是分页查询的代码,注意这里不就是要写sql语句，但是注意这里有一个条件查询，就是动态查询，那么你就要
    //在这个接口对应的映射文件里写就行，


    void startOrLimit(Employee employee);
    //这个就是员工启动和禁用





}
