package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    //这个就是新增员工的实现类方法


    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    //这个是添加员工
    void save(EmployeeDTO employeeDTO);

    //这个是页面查询
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    //这个启用和禁用账号
    void startOrLimit(Integer status, long id);

    //这个就是编辑员工信息
    void updateEmployee(EmployeeDTO employeeDTO);

    Employee getById(long id);
    //这个就是根据id来查询员工信息，用来获取并且编辑



}
