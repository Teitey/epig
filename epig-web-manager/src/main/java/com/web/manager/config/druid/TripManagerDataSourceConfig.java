package com.web.manager.config.druid;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.web.manager.config.jdbc.template.SecondJdbcTemplate;
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
 * 旅游资源管理应用数据库trip_manager连接池配置
 */
@Configuration
@MapperScan(basePackages= {"com.sweet.gen.mapper.tm",
		"com.web.manager.dao.tm"}, sqlSessionTemplateRef ="secondSqlSessionTemplate")
public class TripManagerDataSourceConfig {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Bean(name = "tripManagerDataSource") // 声明其为Bean实例
	@ConfigurationProperties(prefix="spring.datasource.tripmanager")
	public DataSource dataSource() {
		return DruidDataSourceBuilder.create().build();
	}

	@Bean(name = "secondSqlSessionFactory")
	public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("tripManagerDataSource") DataSource dataSource) {
		DruidConfiguration config = new DruidConfiguration();
		return config.getSqlSessionFactory(dataSource);
	}
	@Bean(name= "secondSqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("secondSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	/**
	 * 事务
	 * @param dataSource
	 * @return
	 */
	@Bean("secondTransactionManager")
	public PlatformTransactionManager SecondTransactionManager(@Qualifier("tripManagerDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	/**
	 * jdbc模板
	 * @param dataSource
	 * @return
	 */
	@Bean("secondJdbcTemplate")
	public SecondJdbcTemplate secondJdbcTemplate (@Qualifier("tripManagerDataSource") DataSource dataSource) {
		SecondJdbcTemplate sjt = new SecondJdbcTemplate(dataSource);
		return sjt;
	}

	@Bean(name = "tripManagerTxManager")
	public PlatformTransactionManager annotationDrivenTransactionManager(@Qualifier("tripManagerDataSource") DataSource dataSource)
			throws SQLException {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "tripManagerTxInterceptor")
	@Primary
	public TransactionInterceptor transactionInterceptor(@Qualifier("tripManagerTxManager") PlatformTransactionManager transactionManager)
			throws SQLException {
		DruidConfiguration config = new DruidConfiguration();
		return config.getTransactionInterceptor(transactionManager);
	}
}
