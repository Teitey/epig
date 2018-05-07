package com.web.manager.config.jdbc.template;



import com.web.manager.config.jdbc.Identifiable;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

import javax.persistence.Id;
import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 扩展Spring JdbcTemplate.
 * <p/>
 * <ul>
 * <li>增加了{@code querySingleObject}相关方法，该类方法不抛出
 * {@link EmptyResultDataAccessException}.
 * <p>
 * 注意：{@code queryForObject}与{@code querySingleObject}的不同点：
 * {@code queryForObject}表示一定要拿到一个对象， 当查询语句返回的数据条数不等于1时抛出异常；而
 * {@code querySingleObject}突出单个对象，当查询语句未能查询到数据时返回{@code null}， 若查询到了多条记录还是会抛出
 * {@link IncorrectResultSizeDataAccessException}.
 * </p>
 * </li>
 * <li>
 * 使用jdbc方式时,需要配置中引入如下配置:<br/>
 * {@code <bean id="dbHelper" class="com.syiti.cm.common.dao.jdbc.DefaultJdbcTemplate">}
 * {@code <property name = "dataSource" ref="dataSource"/>}
 * {@code </bean>}
 * </li>
 * </ul>
 * <p/>
 */
	public class PrimaryJdbcTemplate extends JdbcTemplate {
	
		/**
		 * 构造器
		 * @param dataSource
		 */
	public PrimaryJdbcTemplate(DataSource dataSource) {
		super(dataSource);
    }

	/**
	 * 查询单条记录并返回对应数据. 查不到数据时返回{@code null}.
	 * 
	 * @param sql
	 *            查询语句
	 * @param rowMapper
	 *            数据转换器
	 * @param args
	 *            查询语句中的参数列表
	 * @return 对应的数据或{@code null}
	 * @throws IncorrectResultSizeDataAccessException
	 *             返回的数据包含多条记录时抛出此异常
	 * @throws DataAccessException
	 *             其他数据库操作异常
	 */
	public <T> T querySingleObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
		List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
		return DataAccessUtils.singleResult(results);
	}

	/**
	 * 查询单条记录并返回对应数据. 查不到数据时返回{@code null}.
	 * 
	 * @param sql
	 *            查询语句
	 * @param args
	 *            查询语句中的参数列表
	 * @param rowMapper
	 *            数据转换器
	 * @return 对应的数据或{@code null}
	 * @throws IncorrectResultSizeDataAccessException
	 *             返回的数据包含多条记录时抛出此异常
	 * @throws DataAccessException
	 *             其他数据库操作异常
	 */
	public <T> T querySingleObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
		return DataAccessUtils.singleResult(results);
	}

	/**
	 * 查询单条记录并返回对应数据. 查不到数据时返回{@code null}.
	 * 
	 * @param sql
	 *            查询语句
	 * @param args
	 *            查询语句中的参数列表
	 * @param argTypes
	 *            参数类型列表（来自于 java.sql.Types）
	 * @param rowMapper
	 *            数据转换器
	 * @return 对应的数据或{@code null}
	 * @throws IncorrectResultSizeDataAccessException
	 *             返回的数据包含多条记录时抛出此异常
	 * @throws DataAccessException
	 *             其他数据库操作异常
	 */
	public <T> T querySingleObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)
	        throws DataAccessException {
		List<T> results = query(sql, args, argTypes, new RowMapperResultSetExtractor<T>(rowMapper, 1));
		return DataAccessUtils.singleResult(results);
	}

	/**
	 * 查询单条记录并返回对应数据. 查不到数据时返回{@code null}.
	 * 
	 * @param sql
	 *            查询语句
	 * @param requiredType
	 *            返回值类型
	 * @return 对应的数据或{@code null}
	 * @throws IncorrectResultSizeDataAccessException
	 *             返回的数据包含多条记录时抛出此异常
	 * @throws DataAccessException
	 *             其他数据库操作异常
	 */
	public <T> T querySingleObject(String sql, Class<T> requiredType) throws DataAccessException {
		return querySingleObject(sql, getSingleColumnRowMapper(requiredType));
	}

	/**
	 * 查询单条记录并返回对应数据. 查不到数据时返回{@code null}.
	 * 
	 * @param sql
	 *            查询语句
	 * @param rowMapper
	 *            数据转换器
	 * @return 对应的数据或{@code null}
	 * @throws IncorrectResultSizeDataAccessException
	 *             返回的数据包含多条记录时抛出此异常
	 * @throws DataAccessException
	 *             其他数据库操作异常
	 */
	public <T> T querySingleObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
		List<T> results = query(sql, rowMapper);
		return DataAccessUtils.singleResult(results);
	}

	/**
	 * 查询单条记录并返回对应数据. 查不到数据时返回{@code null}.
	 * 
	 * @param sql
	 *            查询语句
	 * @param requiredType
	 *            返回数据类型
	 * @param args
	 *            查询语句中的参数列表
	 * @return 对应的数据或{@code null}
	 * @throws IncorrectResultSizeDataAccessException
	 *             返回的数据包含多条记录时抛出此异常
	 * @throws DataAccessException
	 *             其他数据库操作异常
	 */
	public <T> T querySingleObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
		return querySingleObject(sql, args, getSingleColumnRowMapper(requiredType)); // To
		                                                                             // change
		                                                                             // body
		                                                                             // of
		                                                                             // generated
		                                                                             // methods,
		                                                                             // choose
		                                                                             // Tools
		                                                                             // |
		                                                                             // Templates.
	}

	/**
	 * 查询单条记录并返回对应数据. 查不到数据时返回{@code null}.
	 * 
	 * @param sql
	 *            查询语句
	 * @param args
	 *            查询语句中的参数列表
	 * @param requiredType
	 *            返回数据类型
	 * @return 对应的数据或{@code null}
	 * @throws IncorrectResultSizeDataAccessException
	 *             返回的数据包含多条记录时抛出此异常
	 * @throws DataAccessException
	 *             其他数据库操作异常
	 */
	public <T> T querySingleObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
		return querySingleObject(sql, args, getSingleColumnRowMapper(requiredType)); // To
		                                                                             // change
		                                                                             // body
		                                                                             // of
		                                                                             // generated
		                                                                             // methods,
		                                                                             // choose
		                                                                             // Tools
		                                                                             // |
		                                                                             // Templates.
	}

	/**
	 * 查询单条记录并返回对应数据. 查不到数据时返回{@code null}.
	 * 
	 * @param sql
	 *            查询语句
	 * @param args
	 *            查询语句中的参数列表
	 * @param argTypes
	 *            参数类型列表（来自于 java.sql.Types）
	 * @param requiredType
	 *            返回数据类型
	 * @return 对应的数据或{@code null}
	 * @throws IncorrectResultSizeDataAccessException
	 *             返回的数据包含多条记录时抛出此异常
	 * @throws DataAccessException
	 *             其他数据库操作异常
	 */
	public <T> T querySingleObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType)
	        throws DataAccessException {
		return querySingleObject(sql, args, argTypes, getSingleColumnRowMapper(requiredType)); // To
		                                                                                       // change
		                                                                                       // body
		                                                                                       // of
		                                                                                       // generated
		                                                                                       // methods,
		                                                                                       // choose
		                                                                                       // Tools
		                                                                                       // |
		                                                                                       // Templates.
	}

	/**
	 * 查询记录并返回对应数据映射集合. 使用返回值中{@link Id @Id}标记的字段作为{@code key}，数据对象作为
	 * {@code value}.
	 * 
	 * @param <V>
	 *            返回值数据类型. 需要包含{@link Id @Id}标记的字段或{@code getId()}方法.
	 * @param sql
	 *            查询语句
	 * @param rowMapper
	 *            数据转换器
	 * @param args
	 *            查询语句中的参数列表
	 * @return 数据映射集合
	 * @throws DataAccessException
	 *             数据库操作异常
	 * @throws IllegalStateException
	 *             无法确定映射集合的KEY值时抛出此异常
	 */
	public <K, V> Map<K, V> queryMap(String sql, final RowMapper<V> rowMapper, Object... args)
	        throws IllegalStateException {
		final HashMap<K, V> map = new HashMap<K, V>();
		query(sql, newArgPreparedStatementSetter(args), new RowCallbackHandler() {

			@SuppressWarnings("unchecked")
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				int rowNum = 0;
				while (rs.next()) {
					V v = rowMapper.mapRow(rs, rowNum++);
					K id = (K) ReflectPrimaryKeyReference.getPrimaryValue(v);
					map.put(id, v);
				}

			}
		});

		return map;
	}

	/**
	 * 插入单条记录.
	 * 
	 * @param sql
	 *            insert SQL
	 * @param args
	 *            SQL 参数列表
	 * @return 插入的记录主键.
	 * @throws DataAccessException
	 *             数据库操作异常
	 */
	public Number insert(String sql, Object... args) throws DataAccessException {
		KeyHolderPreparedStatementCreator psc = new KeyHolderPreparedStatementCreator(sql, args);
		update(psc, psc.keyHolder);
		return psc.keyHolder.getKey();
	}

	/**
	 * 插入单条记录.
	 * 
	 * @param sql
	 *            insert SQL
	 * @param args
	 *            SQL 参数列表
	 * @param argTypes
	 *            SQL 参数类型列表
	 * @return 插入的记录主键.
	 * @throws DataAccessException
	 *             数据库操作异常
	 */
	public Number insert(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		KeyHolderPreparedStatementCreator psc = new KeyHolderPreparedStatementCreator(sql, args);
		update(psc, psc.keyHolder);
		return psc.keyHolder.getKey();
	}

	/**
	 * 插入单条记录.
	 * 
	 * @param sql
	 *            insert SQL
	 * @return 插入的记录主键.
	 * @throws DataAccessException
	 *             数据库操作异常
	 */
	public Number insert(String sql) throws DataAccessException {
		return insert(sql, (Object[]) null);
	}

	private static class KeyHolderPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

		private final String sql;
		private final Object[] args;
		private final KeyHolder keyHolder = new GeneratedKeyHolder();

		public KeyHolderPreparedStatementCreator(String sql, Object[] args) {
			Assert.notNull(sql, "SQL must not be null");
			this.sql = sql;
			this.args = args;
		}

		@Override
		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			PreparedStatement prepareStatement = con.prepareStatement(this.sql, Statement.RETURN_GENERATED_KEYS);
			new ArgumentPreparedStatementSetter(args).setValues(prepareStatement);
			return prepareStatement;
		}

		@Override
		public String getSql() {
			return this.sql;
		}
	}
}

class ReflectPrimaryKeyReference {

	private static final String UNKNOWN_PRIMARY_KEY = "unknown primary key - The entity bean returned by DefaultJdbcTemplate#queryMap should meet : "
	        + "\n    1. present id property or getId() method with no arguments, or"
	        + "\n    2. @javax.​persistence.Id annotated property or method with no arguments, or"
	        + "\n    3. implement cn.etuo.kjm.framework.common.Identifiable interface.";

	@SuppressWarnings("rawtypes")
	private static final Map<Class, AccessibleObject> cache = new HashMap<Class, AccessibleObject>();

	@SuppressWarnings("unchecked")
	public static <T> T getPrimaryValue(Object obj) {
		if (obj instanceof Identifiable) {
			return ((Identifiable<T>) obj).getId();
		}
		AccessibleObject accessibleObject = ReflectPrimaryKeyReference.getPrimaryKey(obj);
		if (accessibleObject instanceof Method) {
			try {
				return (T) ((Method) accessibleObject).invoke(obj);
			} catch (Exception ex) {
				throw new IllegalStateException("无法获取主键", ex);
			}
		} else {
			Field field = (Field) accessibleObject;
			Object value = null;
			try {
				value = field.get(obj);
			} catch (Exception ex) {
			}
			if (value == null) {
				PropertyDescriptor propertyDescriptor = BeanUtils
				        .getPropertyDescriptor(obj.getClass(), field.getName());
				try {
					Method method = propertyDescriptor.getReadMethod();
					if (method == null) {
						throw new IllegalStateException("无法获取主键：字段【" + field.getName() + "】值无法访问");
					}
					value = method.invoke(obj);
				} catch (Exception ex) {
					throw new IllegalStateException("无法获取主键", ex);
				}
			}
			return (T) value;
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public static AccessibleObject getPrimaryKey(Object obj) {
		Class type = obj.getClass();
		AccessibleObject accessibleObject = cache.get(type);
		if (accessibleObject == null) {
			for (Field field : type.getFields()) {
				Id id = AnnotationUtils.getAnnotation(field, Id.class);
				if (id != null) {
					accessibleObject = field;
					break;
				}
			}
			for (Field field : type.getDeclaredFields()) {
				Id id = AnnotationUtils.getAnnotation(field, Id.class);
				if (id != null) {
					accessibleObject = field;
					break;
				}
			}
			if (accessibleObject == null) {
				try {
					for (Method method : type.getMethods()) {
						Id id = AnnotationUtils.getAnnotation(method, Id.class);
						if (id != null) {
							accessibleObject = method;
							break;
						}
					}
				} catch (Exception ex) {
				}
			}
			try {
				if (accessibleObject == null) {
					accessibleObject = type.getMethod("getId");
				}
			} catch (SecurityException ex) {
			} catch (NoSuchMethodException ex) {
			}
			try {
				if (accessibleObject == null) {
					accessibleObject = type.getField("id");
				}
			} catch (NoSuchFieldException ex) {
			} catch (SecurityException ex) {
			}
			try {
				if (accessibleObject == null) {
					accessibleObject = type.getMethod("getID");
				}
			} catch (SecurityException ex) {
			} catch (NoSuchMethodException ex) {
			}
			try {
				if (accessibleObject == null) {
					accessibleObject = type.getField("ID");
				}
			} catch (NoSuchFieldException ex) {
			} catch (SecurityException ex) {
			}
			if (accessibleObject == null) {
				throw new IllegalStateException(UNKNOWN_PRIMARY_KEY);
			}
			cache.put(type, accessibleObject);
		}
		return accessibleObject;
	}
}


