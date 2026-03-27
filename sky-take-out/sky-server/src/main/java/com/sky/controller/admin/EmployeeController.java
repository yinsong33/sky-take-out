package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
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


         //============================================================
    //下面就是员工分页查询的代码，我们在写代码的时候，要求都在apidox里面，我们根据里面的要求
    //什么get还是post请求方式啊，前端给你返回的是什么类型啊，就是参数都是已经准备好在pojo里面了
    //都是实体类，然后你要判断前端给你的是什么类型就行，如果是json你就转换为dto,用requestbody，
    //如果是query参数，你就在实体类里面找employeepagequerydto就行了，就不用加requestbody
    //然后还有一点，看右边实例你就能确定是什么格式了code message,data这个不就是result格式
    //然后data里面还有total,records，这个不就是返回类型是pageresult，然后就是result<pageresult>
    //两个就联系在一起，然后records里面不就是employee实体类参数，其实也就是员工分页查询的信息

     @GetMapping("page")//这个看要求，上面已经有了，requestmapping后面有一个路径，然后
     //你在看接口文档，后面跟了一个page，那么这个请求方式就把page带着.
    public Result<PageResult> page (EmployeePageQueryDTO employeePageQueryDTO){
        PageResult pageResult=employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);

    }


    //=============================================================================
    //接下来就是写员工禁用与启用的方法，这里我们还是跟上面一样，但是有一个细节就是，他这个有一个路径参数
    //就是你@postmapping的时候后面不是写一个status吗，后面就会跟一个参数(status),这个里面就是1是启动
    //0是禁用

    //然后这个里面就是写方法，里面传入两个参数，一个是路径参数，一个就是用户的id
    //然后路径参数要用一个注解@pathvariable，然后这个就是传路径的意思，然后右边把参数写上去，同时第二个参数就是id

    @PostMapping("/status/{status}")
    public Result startOrLimit(@PathVariable Integer status,long id){
          //因为返回值就是一个简单的返回信息，所以直接result.success
        employeeService.startOrLimit(status,id);
        return  Result.success();

    }

    //=========================================================================================
    //注意在编辑信息的前面还有一个先获得员工的id，你才能获得，然后要求看接口文档
     //那你根据id查信息，那么你就要先获得id，这个id是在你的get请求路径中
    @GetMapping("/{id}")
    //你要想我获取id的目的不就是，为了获取员工的信息，这个就是service需要返回的
    public Result<Employee> getById(@PathVariable long id){
        Employee employee=employeeService.getById(id);
        //我现在看我想返回的是data里面员工的信息，那我就在前面加一个employee对象的返回值，意思我service返回的就是
        //employee对象
        return Result.success(employee);
    }


    //=======================================================================
    //下面就是员工的编辑信息
    @PutMapping
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO){
         employeeService.updateEmployee(employeeDTO);
         return Result.success();

    }
    //=====================================================================================
    //！！！！！！！！！！！注意你要想修改员工的数据，那么你就要先获得员工的id，根据员工的Id来进行编辑




}
