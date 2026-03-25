package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;

import net.bytebuddy.asm.Advice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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




}
