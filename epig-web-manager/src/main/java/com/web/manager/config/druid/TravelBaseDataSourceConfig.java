package com.web.manager.config.druid;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

import com.web.manager.config.jdbc.template.PrimaryJdbcTemplate;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 旅游资源基础数据库travel_base连接池配置
 */
@Configuration
@MapperScan(basePackages= {"com.sweet.gen.mapper.tb",
		"com.web.manager.dao.tm"},sqlSessionTemplateRef ="sqlSessionTemplate")
@Primary
public class TravelBaseDataSourceConfig {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Bean(name = "travelBaseDataSource") // 声明其为Bean实例
	@ConfigurationProperties(prefix = "spring.datasource.travelbase")
	@Primary // 在同样的DataSource中，首先使用被标注的DataSource
	public DataSource dataSource() {
		return DruidDataSourceBuilder.create().build();
	}

    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("travelBaseDataSource") DataSource dataSource) {
        DruidConfiguration config = new DruidConfiguration();
        return config.getSqlSessionFactory(dataSource);
    }

    @Bean(name= "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    /**
     * jdbc模板
     * @param dataSource
     * @return
     */
    @Bean("primaryJdbcTemplate")
	public PrimaryJdbcTemplate privateJdbcTemplate (@Qualifier("travelBaseDataSource") DataSource dataSource) {
		PrimaryJdbcTemplate pjt = new PrimaryJdbcTemplate(dataSource);
		return pjt;
	}

    @Bean(name = "travelBaseTxManager")
    public PlatformTransactionManager annotationDrivenTransactionManager(@Qualifier("travelBaseDataSource") DataSource dataSource)
            throws SQLException {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "travelBaseTxInterceptor")
    @Primary
    public TransactionInterceptor transactionInterceptor(@Qualifier("travelBaseTxManager") PlatformTransactionManager transactionManager)
            throws SQLException {
        DruidConfiguration config = new DruidConfiguration();
        return config.getTransactionInterceptor(transactionManager);
    }
}
