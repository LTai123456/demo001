package com.jt.common.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.common.util.IPUtils;
import com.jt.common.util.ShiroUtils;
import com.jt.sys.dao.SysLogDao;
import com.jt.sys.entity.SysLog;
/**
 * @Aspect描述此类为一个切面对象
 * @author 000
 *
 */
@Aspect
@Service
public class SysLogAspect {
	/**
	 * @Around("")环绕通知:目标方法执行之前和之后都可以执行
	 * 环绕通知内部的bean表达式的一个切入点
	 * @return pjp表示一个连接点对象（封装了一个方法）
	 */
	@Autowired
	private SysLogDao sysLogDao;

	@Around("bean(sysRoleServiceImpl)")
	public Object aroundMethod(ProceedingJoinPoint pjp) throws Throwable{
		long startTime = System.currentTimeMillis();
		//执行目标方法(result为目标方法执行的结果)
		Object result=	pjp.proceed();//执行
		long endtTime = System.currentTimeMillis();
		//获取日志信息并存储到数据库
		saveObject( pjp,endtTime-startTime);
		Signature s = pjp.getSignature();
		return result;

	}

	private void saveObject(ProceedingJoinPoint pjp, long time) {
		// 获取日志信息
		Class<?> targetCls = pjp.getTarget().getClass();
		Signature s=pjp.getSignature();
		String methodName=targetCls.getName()+"."+s.getName();
		Object[] params=pjp.getArgs();
		//封装日志信息
		SysLog log=new SysLog();
		log.setIp(IPUtils.getIpAddr());
		log.setMethod(methodName);
		log.setParams(Arrays.toString(params));
		//log.setOperation(operation);
		log.setTime(time);
		log.setUsername(ShiroUtils.getPrincipal().getUsername());
		//保存日志信息
		sysLogDao.insertObject(log);
	}


}
