package com.sky.aspect;



//刚才在注解包里面我们创建好接口，里面是自动填充公共字段的方法，下面这个就是
//切面类AutoFillAspect
//=================================================这个关于公共字段的自动填充知识点就是Aop
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect//切面注解
@Component//这个也是固定的
@Slf4j
public class AutoFillAspect {
    //切面类里面写切点方法

    //===============================================================================
    //首先第一步，我们的目的是自动填充公共的实体类里面的对象，所以我们先创建一个包，名字取值为annotation
    //就是注解的意思，然后因为这是一个自动填充的方法，所以我们创建一个接口，然后名字就是autofill,自动填充
    //然后我们就要想自动填充的这些对象是在哪儿，发现是在方法中的，所以注解@(Target),然后里面就是方法elementtype.method
    //然后下面还有一个就是@Retention(RetentionPolicy.RUNTIME),固定的，然后我们还要想，公共字段不可能只在简单方法
    //中，想还在就是insert,update 因为只有这两个方法中有公共字段，这两个方法在哪儿，就是在枚举里面 OperationType value();
    //里面就是数据库的两个方法insert,update ，然后这个方法定义完，不就可以在mapper里面的insert,update,
    //上面把注解自动填充写上去，必须是相对应的类型insert,update
    //第二步就是切面，和切入点，
    //定义一个类表示切面，然后固定格式@Aspect @Component ,然后就是在切面里面来定义方法，来定义切点
    //然后写完切点上面写注解 @Pointcut,切点在哪儿，然后两个条件，扫描mapper里面的所有方法，同时满足
    //方法里面包含insert,和update ,其实就是注解annotation包里面的auyofill方法。因为这个方法包含
    //insert,update 这两个值，
    //第三步就是通知，因为是在执行sql语句前先给公共变量赋值，所以再下面的一个注解@before 后面写切点的方法
    //意思就是在切点前先通知赋值，然后这个通知也是定义了一个方法，注意里面还有一个值就是jointpoint
    //这个可以理解为，我更深确定切点是谁，上面那个切点的的方法就是大概确定切点是在insert,update这
    //然后我jointpoint 确定切点为这两个中的某一个，然后，调用ethodSignature signature =
    // (MethodSignature) joinPoint.getSignature();这个就是调用标签信息，就是这个切点更深的信息
    //然后我用这个对象.getMethod().getAnnotation(AutoFill.class);，就是获得这个切点所在的
    //具体注解就是定位到autofill这个注解这儿，然后我就可以获得里面的值，是insert 还是update
    //先调用方法，然后定位是在哪儿一个注解getannotation,是在哪儿一个注解，把这个注解的字节码文件放进去
    //是这个autofill 然后就可以拿这个里面枚举里面的方法，其实我定义切点 jointpoint就是为了
    //确定这个insert,update 的起源地在哪儿，让我们可以拿到他，为了后面设置里面的值做准备
    //然后我假设我现在这个value==OperationType.INSERT.这个不就是获取注解里面的值value
    //和枚举里面做比较，然后是的话，是不是就要开始赋值了，我们在mapper里面都是有方法的
    //然后方法里面都有实体类，就是Object[] args = joinPoint.getArgs();
    //        if(args==null || args.length==0)
    //        {
    //            return;
    //        }
    //        Object entity=args[0];
    //我们获取到实体类对象了，然后就可以赋值了，我们先把赋值内容写下来
    //LocalDateTime now = LocalDateTime.now();
    //Long currentId = BaseContext.getCurrentId();
    //然后不就是准备赋值，我们调用实体类.getclass调用类的字节码对象，可以用里面所有的方法
    //然后设置setcreateTime 啥的Method setCreateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
    //           Method setCreateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,long.class);
    //           Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
    //           Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,long.class);
    //           setCreateTime.invoke(entity,now);
    //           setCreateUser.invoke(entity,currentId);
    //           setUpdateTime.invoke(entity,now);
    //           setUpdateUser.invoke(entity,currentId);



    //=================================================================================


    //然后类不管，在里面创建方法，表示我的切入点是啥，其实你找切入点，不就是上面那个接口里面定义的
    //自动填充的是在枚举里面的两个数据库语句吗
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    //这个就是扫描mapper下的所有类和所有方法，同时还要满足，只有是insert，update也就是自动填充方法里面的枚举里面的两个值
    public void autoFillAspectPoint(){
    }
    //==========================================上面这个就是定义切点
    //然后下面就是通知,这里就是前置通知，因为我是先给这些公共字段赋值之后，才执行数据库语句


    //切入点准备好了，就是定义的方法，下面写通知的时候，要先报道切入点在哪儿，就是调用方法
    @Before("autoFillAspectPoint()")
    //注意就是我现在确定了通知是在那个切入点前面的，也就是在执行inset,update方法前，先把公共变量赋值好
    //下面通知方法里面也要写切点，把切点写进去
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      //那么现在就是我成功进入切面点了，面对的就是insert,update两个数据库方法，接下来我是不是就是
        //准备拿到这两个方法，然后来赋值公共变量

        MethodSignature signature =(MethodSignature) joinPoint.getSignature();
        //这个就是我joinpoint不是确定了切入点就是insert,update,那么我就是要获得究竟是获得那儿一个
        //就是getSignature,其实就是获取切入点的签名信息，然后强转为MethodSignature，这个可以理解为就是
        //加强版的signature,为了让应对下面的getmethod
        AutoFill autofill = signature.getMethod().getAnnotation(AutoFill.class);
        //!!!!!!!!!!!!!!!!!!,然后关键来了，我现在不就是获取到了切入点就是，insert,update的签名信息
        //其实也就是更加完整的信息，然后我调用这个getmethod，就是获取假如说是insert,就是调用这个
        //inser这个方法，其实就是理解为定位到这个方法，我现在定位到这个切点的这个方法，然后我获取这个方法的
        //对应注解，刚才在后面就是insert上面有注解，就是值是枚举里面的方法，getannotation，获取
        //这个注解，后面加上他的字节码对象，就是调用这个注解里面所有的的方法,其实就是为了获取注解@Autofill
        //然后是为了获取里面的值
        OperationType value = autofill.value();

        //这个是获取到了包含insert,update方法的对象，以便于下面的选择，然后就是要获取
        //准备要设置公共变量的这些公共变量，就是获取insert或者update右边括号里面的那个总的实体类对象，employee
        Object[] args = joinPoint.getArgs();
        if(args==null || args.length==0)
        {
            return;
        }
        Object entity=args[0];
        //这个就是拿那个括号里面的第一个实体类对象，其实就是实体类

        //然后准备要赋值的公共变量
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if(value==OperationType.INSERT)
        {
           Method setCreateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
           Method setCreateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,long.class);
           Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
           Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,long.class);
           setCreateTime.invoke(entity,now);
           setCreateUser.invoke(entity,currentId);
           setUpdateTime.invoke(entity,now);
           setUpdateUser.invoke(entity,currentId);
        }
        else if(value==OperationType.UPDATE)
        {

            Method setUpdateTime=entity.getClass().getDeclaredMethod("setUpdateTime",LocalDateTime.class);
            Method setUpdateUser=entity.getClass().getDeclaredMethod("setUpdateUser",long.class);

            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentId);
        }

    }







}