package com.web.manager.config.druid;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.sweet.gen.pagination.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据库连接池Druid配置
 */
@Configuration
@EnableTransactionManagement
public class DruidConfiguration {
    /**
     * 注册ServletRegistrationBean
     * @return
     */
    @Bean
    public ServletRegistrationBean registrationBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(),
                "/druid/*");
        /** 初始化参数配置，initParams**/
        //白名单
       // bean.addInitParameter("allow", "127.0.0.1");
        //IP黑名单 (存在共同时，deny优先于allow)
        //bean.addInitParameter("deny", "192.168.1.73");
        //登录查看信息的账号密码.
        bean.addInitParameter("loginUsername", "tbdruid");
        bean.addInitParameter("loginPassword", "Druid@travelbase");
        //是否能够重置数据.
        bean.addInitParameter("resetEnable", "false");
        return bean;
    }
    /**
     * 注册FilterRegistrationBean
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则.
        bean.addUrlPatterns("/*");
        bean.addInitParameter("profileEnable", "true");
        //添加不需要忽略的格式信息.
        bean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return bean;
    }

    /**
     * SqlSessionFactory配置
     * @param dataSource
     * @return
     */
    public SqlSessionFactory getSqlSessionFactory(DataSource dataSource){
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        PageInterceptor pageHelper = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("dialect", "mysql");
        properties.setProperty("pageSqlId", ".*Page$");
        pageHelper.setProperties(properties);
        Interceptor[] plugins =  new Interceptor[]{ pageHelper };
        bean.setPlugins(plugins);
        try {
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * druid + spring监控：拦截器
     * @return
     */
    @Bean(value = "druid-stat-interceptor")
    public DruidStatInterceptor druidStatInterceptor() {
        DruidStatInterceptor druidStatInterceptor = new DruidStatInterceptor();
        return druidStatInterceptor;
    }

    /**
     * druid + spring监控：切入点
     * @return
     */
    @Bean
    public JdkRegexpMethodPointcut druidStatPointcut() {
        JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
        druidStatPointcut.setPatterns("com.web.manager.controller.*",
                "com.web.manager.service.*", "com.web.manager.dao.*","com.sweet.gen.mapper",
                "com.sweet.gen.mapper.*");
        return druidStatPointcut;
    }

    /**
     * druid + spring监控：通知
     * @return
     */
    @Bean
    public Advisor druidStatAdvisor() {
        return new DefaultPointcutAdvisor(druidStatPointcut(), druidStatInterceptor());
    }

    /**
     * 指定拦截器，对特定Bean自动创建代理拦截：目前仅用于事务管理
     * @return
     */
    @Bean
    public BeanNameAutoProxyCreator transProxy() {
        BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
        creator.setProxyTargetClass(true);
        creator.setBeanNames("*Impl");
        creator.setInterceptorNames("travelBaseTxInterceptor", "tripManagerTxInterceptor");
        return creator;
    }

    /**
     * 指定事务管理拦截的方法
     * @param transactionManager
     * @return
     */
    public TransactionInterceptor getTransactionInterceptor(PlatformTransactionManager transactionManager){
        Properties props = new Properties();
        props.setProperty("insert*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("update*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("del*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("save*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("submit*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("cancel*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("register*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("clear*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("put*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("add*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("modify*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("edit*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("config*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("remove*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("revoke*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("push*", "PROPAGATION_REQUIRED,-java.lang.Exception");
        props.setProperty("handle*", "PROPAGATION_REQUIRED,-java.lang.Exception");

//		props.setProperty("*", "PROPAGATION_REQUIRED");

        TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager, props);

        AspectJExpressionPointcutAdvisor pointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        pointcutAdvisor.setAdvice(txAdvice);
        pointcutAdvisor.setExpression("execution (* com.web.manager.service.impl.*.*(..))");

        return txAdvice;
    }
}
