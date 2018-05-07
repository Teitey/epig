package com.web.manager.config.jdbc;

/**
 * 表主键
 * @author suxl
 *
 * @param <T>
 */
public interface Identifiable<T> {
	public T getId();
}
