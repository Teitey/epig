package com.sweet.gen.pagination;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.PropertyException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class}) })
public class PageInterceptor implements Interceptor {

	private final Logger logger = LoggerFactory.getLogger(PageInterceptor.class);

	private static String dialect = "mysql"; // 数据库方言
	private static String pageSqlId = ".*Page$"; // mapper.xml中需要拦截的方法(正则匹配)，以Page结尾的都拦截

	public Object intercept(Invocation invocation) throws Throwable {
		if (!(invocation.getTarget() instanceof RoutingStatementHandler)) {
			return invocation.proceed();
		}
		RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
		BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler,
				"delegate");
		MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate,
				"mappedStatement");

		if (!mappedStatement.getId().matches(pageSqlId)) {
			// 拦截需要分页的SQL
			return invocation.proceed();
		}
		// 查询条数
		int count = 0;
		BoundSql boundSql = delegate.getBoundSql();
		// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
		Object parameterObject = boundSql.getParameterObject();
		if (parameterObject == null) {
			throw new NullPointerException("parameterObject尚未实例化！");
		} else {
			Pagination page = null;
			if (parameterObject instanceof Pagination) { // 参数就是Pagination实体
				page = (Pagination) parameterObject;
			} else if (parameterObject instanceof Map) {
				// 如果参数是Map，map中必须有key=pagination，value=Pagination实体
				page = (Pagination) ((Map<?, ?>) parameterObject).get("pagination");
			} else {
				// 参数为某个实体，该实体拥有Pagination属性
				Field pageField = ReflectHelper.getFieldByFieldName(parameterObject, "pagination");
				if (pageField != null) {
					page = (Pagination) ReflectHelper.getValueByFieldName(parameterObject, "pagination");
					if (page == null) {
						return invocation.proceed();
					}
				} else {
					// 不存在page属性
					return invocation.proceed();
				}
			}
			Connection connection = (Connection) invocation.getArgs()[0];
			String sql = boundSql.getSql();
			String countSql = "select count(0) from (" + sql + ") tmp_count"; // 记录统计
			PreparedStatement countStmt = connection.prepareStatement(countSql);
			logger.info(countSql);
			ReflectHelper.setValueByFieldName(boundSql, "sql", countSql);
			DefaultParameterHandler ParameterHandler = new DefaultParameterHandler(mappedStatement,
					parameterObject, boundSql);
			ParameterHandler.setParameters(countStmt);
			ResultSet rs = countStmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
			countStmt.close();
			page.setTotalCount(count);
			page.setCurrentPage(page.getPage());// jquery-easyui分页时当前页变量名为page
			page.setPageSize(page.getRows());// jquery-easyui分页时每页记录数变量名为rows
			int totalPage = count / page.getPageSize() + ((count % page.getPageSize() == 0) ? 0 : 1);
			page.setTotalPage(totalPage);
			String pageSql = generatePagesSql(sql, page);
			logger.info(pageSql);
			ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql); // 将分页sql语句反射回BoundSql.
		}
		return invocation.proceed();
	}

	/**
	 * 根据数据库方言，生成特定的分页sql
	 * 
	 * @param sql
	 * @param page
	 * @return
	 */
	private String generatePagesSql(String sql, Pagination page) {
		if (page != null && StringUtils.isNotEmpty(dialect)) {
			StringBuffer pageSql = new StringBuffer();
			if ("mysql".equals(dialect)) {
				String beginrow = String.valueOf((page.getCurrentPage() - 1) * page.getPageSize());
				pageSql.append(sql);
				pageSql.append(" limit " + beginrow + "," + page.getPageSize());
			} else if ("oracle".equals(dialect)) {
				String beginrow = String.valueOf((page.getCurrentPage() - 1) * page.getPageSize());
				String endrow = String.valueOf(page.getCurrentPage() * page.getPageSize());
				pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
				pageSql.append(sql);
				pageSql.append(") tmp_tb where ROWNUM<=");
				pageSql.append(endrow);
				pageSql.append(") where row_id>");
				pageSql.append(beginrow);
			}
			return pageSql.toString();
		} else {
			return sql;
		}
	}

	public Object plugin(Object arg0) {
		return Plugin.wrap(arg0, this);
	}

	public void setProperties(Properties p) {
		dialect = p.getProperty("dialect");
		if (StringUtils.isEmpty(dialect)) {
			try {
				throw new PropertyException("dialect property is not found!");
			} catch (PropertyException e) {
				logger.info("PageInterceptor setProperties dialect: ", e.getMessage());
			}
		}
		pageSqlId = p.getProperty("pageSqlId");
		if (StringUtils.isEmpty(pageSqlId)) {
			try {
				throw new PropertyException("pageSqlId property is not found!");
			} catch (PropertyException e) {
				logger.info("PageInterceptor setProperties pageSqlId: ", e.getMessage());
			}
		}
	}

}
