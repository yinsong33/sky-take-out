package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        System.out.println("前端传来的原始 username = [" + username + "]");
        System.out.println("前端传来的原始 password = [" + password + "]");
        System.out.println("前端原始 password 长度 = " + password.length());

        Employee employee = employeeMapper.getByUsername(username);

        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        password = DigestUtils.md5DigestAsHex(password.getBytes());

        System.out.println("前端密码加密后 = [" + password + "]");
        System.out.println("前端加密后长度 = " + password.length());

        System.out.println("数据库中的 password = [" + employee.getPassword() + "]");
        System.out.println("数据库 password 长度 = " + employee.getPassword().length());

        System.out.println("password.equals(employee.getPassword()) = " + password.equals(employee.getPassword()));

        for (int i = 0; i < Math.min(password.length(), employee.getPassword().length()); i++) {
            if (password.charAt(i) != employee.getPassword().charAt(i)) {
                System.out.println("第一个不同字符位置: " + i);
                System.out.println("前端字符 = " + password.charAt(i) + "，编码 = " + (int) password.charAt(i));
                System.out.println("数据库字符 = " + employee.getPassword().charAt(i) + "，编码 = " + (int) employee.getPassword().charAt(i));
                break;
            }
        }

        if (!password.equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return employee;
    }


    //这个不就是重新写的service接口里面的方法
    //就是获得了从controller传过来的数据
     public  void save(EmployeeDTO employeeDTO){
          //就是service层得到了controller传过来的DTO数据，然后service肯定是要用到
         //mapper层的,接下来想mapper里面是干啥的，是不是将实体类传到数据库里面，那么实体类和DTO不是一个东西
         //先把DTO的东西赋值给总的实体类empolyee,然后把里面剩余的值自己赋值

         //先获得实体类
         Employee employee=new Employee();
         BeanUtils.copyProperties(employeeDTO,employee);
         //然后设置是实体类里面dto没有的，把值赋给上


         employee.setStatus(StatusConstant.ENABLE);//这个就是设置状态的默认值为1，启动的
         employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));//这个是加密之后的密码
         //而且这个是定义了一个常量，在sky-common里面的passwordconstatnt里面的常量,其实还是就是123456

         employee.setCreateTime(LocalDateTime.now());
         employee.setUpdateTime(LocalDateTime.now());


         employee.setCreateUser(BaseContext.getCurrentId());
         employee.setUpdateUser(BaseContext.getCurrentId());
         //这个是建议不能设定成固定值，这边应该是动态获取在这里的id，然后setupdateuser里面的值最好与id动态变化
         //其实这里的意思就是，我登陆的时候以id，username登陆就会生成token,然后通过的时候就会被拦截器阻拦
         //用来校验，然后我就是想这个时候来解析，然后获取到id,然后再service层里面把updatauser与id值一样，
         //一一对应,该怎么让解析出来的id与service里面的undatauser对应

         //那么就要使用ThreadLocal,他是thread的一个局部变量，然后他为每一个线程都提供了一份存储空间
         //只有线程才能获取对应的值，其实就相当于每一个小线程提供一个local,当地的庇护所，然后想获得就进去，获取值
         //!!!!!!!!!!!!客户端每次发送的请求，都是一个单独的线程
         //那其实就是很简单了，想什么登陆啊，拦截器啊，服务层都是用一个线程，那我用Threadlocal给他们包起来，
         //提供存储空间，然后从登录页面拦截器那里，把id解析下来，然后传给service层，设置updataUser为id，不就行了
         //而且这个id还是你每一次不一样，他就不一样，但是给updateUser是实时的，每次线程不一样，但是里面三个
         //调用的是同一个
         //注意在令牌拦截那里，就已经解析好id了，直接把他放在threadlocal提供的空间里面，让service get就行了

         //我现在把东西都赋值好了，然后准备给到mapper，让他去写sql语句
         employeeMapper.insert(employee);
    }


    //===================================================================
    //下面这个就是员工分页查询的代码
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //现在这里就是这里有name ,page,pagesize数据，然后要返回的是total,records的数据
        //分别不就是模糊查询nam  e,页码，每一页一共几条数据，返回的是总的条数,还有点击每一条里面的数据
        //本来应该是select * from employee limit 0,10从第一页开始，查询十条，然后有一个插件可以自动分页
        //就是pageHelper
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //这个就是往里面设定两个值，第一个就是实体类里面的page页数，第二个不就是里面的每一页有多少条
        //那么实体类里面的 name还有怎么显示employee的信息，这个就是在插件pagehelper的返回值里面
        //太厉害了 ,！！！！！注意name在这里就是一个模糊查询.
        //下一步就是在mapper里面写查询语句
        Page<Employee> page=employeeMapper.pageQuery(employeePageQueryDTO);
        //我刚才的插件不就是在里面设置了两个值，一个是页数，一个是每一页多少行
        //然后还有模糊查询的name，还有employee信息就是放在插件的返回对象page<Employee>里面
        //直接写sql语句就行，然后employeeMapper里面还要把含有页数和每页多少条数据，name的实体类
        //传进去，左边就是含有员工信息的实体类，那么不就是都在里面

        //我现在就是这个方法返回的是pageresult,那么我现在就是想如何将page转换为pageresult,然后返回出来
        //我这个page里面就有两个方法可以获得pageresult里面的两个参数，直接调用，注意
        //获取records是获得你实体类的所有数据，所以不是简单调用getrecords，而是getresult获得是集合里面就是employee的数据
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        return new PageResult(total,records);
    }


}
