/**
 * 
 */
package com.web.manager.config.jdbc.dao;

import com.alibaba.fastjson.JSONObject;


import com.sweet.util.support.StringUtil;
import com.web.manager.config.jdbc.Pagination;
import com.web.manager.config.jdbc.template.SecondJdbcTemplate;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 使用spring-jdbc二次封装的数据库层抽象类，所有dao层均需继承该抽象类<br>
 * 
 * 配置中需要注入dbHelper
 * <p/>
 */
@Repository
public class SecondJdbcTemplateDao {
	protected static Logger LOGGER = LoggerFactory.getLogger(SecondJdbcTemplateDao.class);
	
	@Autowired
	@Qualifier("secondJdbcTemplate")
	protected SecondJdbcTemplate secondJdbcTemplate;
	
	/**
	 * 查询列表 - 不带参数
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList(String sql){
		LOGGER.info(sql);
		return secondJdbcTemplate.queryForList(sql);
	}
	
	/**
	 * 查询列表 - 带参数
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList(String sql, Object... args){
		LOGGER.info(sql);
		return secondJdbcTemplate.queryForList(sql, args);
	}
	
	/**
	 * 插入记录
	 * @param sql
	 * @param args - Object数组（Object[]）
	 * @return
	 */
	public Number insert(String sql, Object... args){
		LOGGER.info(sql + "==params:==" + JSONObject.toJSONString(args));
		return secondJdbcTemplate.insert(sql, args);
	}
	
	/**
	 * 更新操作
	 * @param sql
	 * @param args
	 * @return
	 */
	public int update(String sql, Object... args){
		LOGGER.info(sql + "==params:==" + JSONObject.toJSONString(args));
		return secondJdbcTemplate.update(sql, args);
	}
	
	/**
	 * 执行SQL
	 * @param sql
	 */
	public void execute(String sql){
		LOGGER.info(sql);
		secondJdbcTemplate.execute(sql);
	}
	/**
	 * 分页查询. <strong>不支持union语句</strong>
	 * 
	 * @param sql
	 *            查询语句. <strong>不支持union语句</strong>
	 * @param rowMapper
	 *            数据转换器
	 * @param page
	 *            分页参数 - 页码
	 * @param size
	 *            分页参数 - 大小
	 * @param args
	 *            查询语句其他参数
	 * @return 分页结果
	 */
	@SuppressWarnings("unchecked")
	public <T> Pagination<T> paginate(String sql, RowMapper<T> rowMapper, int page, int size, Object... args) {
		Pagination<T> pagination = new Pagination<T>(page, size);
		int total = secondJdbcTemplate.queryForObject(sqlToCount(sql), Integer.class, args);
		pagination.setTotalCount(total);
		if (pagination.isOverflowed()) {
			pagination.setElements(Collections.EMPTY_LIST);
			return pagination;
		}

		List<T> elements = secondJdbcTemplate.query(sqlAppendLimit(sql), rowMapper,
		        ArrayUtils.addAll(args, new Object[] { pagination.getIndex(), pagination.getPageSize() }));
		pagination.setElements(elements);
		return pagination;
	}

	private String sqlToCount(String sql) {
		int i = StringUtil.lowerCase(sql).indexOf("from");
		if (i < 0) {
			throw new RuntimeException("分页查询语句解析失败：" + sql);
		}
		int j = StringUtil.lowerCase(sql).indexOf("limit");
		StringBuilder builder = new StringBuilder().append("select count(1) ");
		if (j > 0) {
			builder.append(sql.substring(i, j));
		} else {
			builder.append(sql.substring(i));
		}
		return builder.toString();
	}

	private String sqlAppendLimit(String sql) {
		if (sql.endsWith(";")) {
			sql = sql.substring(0, sql.length() - 1);
		}
		return sql + " limit ?,?";
	}
}

