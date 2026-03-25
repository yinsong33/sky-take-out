package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
//其实上面这个就是一个注释
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工推出")
    public Result<String> logout() {
        return Result.success();
    }



     //其实这个EmployeeDTO就是把前端传过来的json格式的数据封装成他的格式
    //然后vo就是返回给前端

     // 1 我现在前端前端传过来的数据是json格式的，但是我要把把他封装成EmployeeDTO的对象类，注意你要封装成一个类对象
    //那么你就要用一个注解就是@requestbody,就是把json转换成对象，
    // 2 然后就是处理类里面不是有那个什么requestmapping吗后面加那个路径，这个是get的方式，要写指定路径
    //但是这里你看apifox接口文档里面就是post 那么用post就要用一个注解就是postmapping

    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){
       //然后control是获取请求，那么操作的是不是就是service层，所以里面要写services的类
        //其实就是你要把获得的请求给service来服务，就是要一个连接，所以两者要有关系，其实就是
        //把战线拉到service里面了,注意把参数，也就是对象传进去,!!!!!!!!!!!!!
        //注意啊我这里不就是想调用service里面的那个类吗@Autowired
        //    private EmployeeService employeeService;
        //注意这个是employService 不是EmpolyeeService,这个接口的方法，注意一下

        employeeService.save(employeeDTO);

        return Result.success();//这个意思就是返回成功,后端返回给前端

     }



}
