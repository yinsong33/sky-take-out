package com.sky.annotation;

import com.sky.enumeration.OperationType;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//这个annotation就是注解的意思，然后就是，我们在写前面两个地方的分别接口的时候
//不是在service层的方法里面都用到createtime,updatedatime,createUser,updateUser,然后这些都是
//规定格式，像什么直接LocationTime.now,还有，update更新时间，跟随id的变化而变化，是那个
//User 设置值就是BaseContext.getid,这个是在拦截器里面，先在token里面给他把id解析出来，然后设置用来拿取
//然后这个注解,这个接口里面的方法就是用来自动给公共的，向上面这两种自动给它赋值的

@Target(ElementType.METHOD)//首先先是定义在方法中，只有方法中的才能自动填充
@Retention(RetentionPolicy.RUNTIME)//这个也是固定的
public @interface AutoFill {
     //然后这里规定哪儿些的数据库操作类型会用到上面方法中的公共字段
    //然后这个就是在sky-commom里面的enumeration.Operation Type里面，这个是枚举，他枚举了数据库的两个类型insert,和update
    //因为只有这两个会出现公共字段,其实也就是先是在方法里面，然后就是方法中的哪儿个小方法里面，发现就是枚举里面的
    //两个数据库类型
    OperationType value();






}

