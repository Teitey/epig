package com.web.manager.config.jdbc;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * JavaBean实现的分页器. 用来封装分页信息。
 * <p/>
 * 
 * @author <a href="mailto:dingbq@etuo.cn">Holin Ding</a>
 * @version @param <T> Date: 2016年1月29日 下午9:10:42
 * @serial 1.0
 * @since 2016年1月29日 下午9:10:42
 */
public class Pagination<T> {

    public static final int DEFAULT_PAGESIZE = 10;
    private int pageNo = 1;
    private int pageSize = DEFAULT_PAGESIZE;
    private int totalCount;
    private List<T> elements;

    public Pagination() {
    }

    public Pagination(int page) {
        this.pageNo = page;
    }

    public Pagination(int page, int size) {
        this.pageNo = page;
        this.pageSize = size;
    }

    /**
     * 页码. 当前第几页.
     *
     * @return 页码
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页码.
     *
     * @param pageNo 页码
     */
    public void setPageNo(int pageNo) {
        if (pageNo > 1) {
            this.pageNo = pageNo;
        }
    }

    /**
     * 分页大小. 一页包含多少数据.
     *
     * @return 分页大小
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置分页大小.
     *
     * @param pageSize 一页包含多少数据
     */
    public void setPageSize(int pageSize) {
        if (pageSize > 1) {
            this.pageSize = pageSize;
        }
    }

    /**
     * 获取不分页时的总数据量.
     *
     * @return 总数据量
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 设置不分页时的总数据量.
     *
     * @param totalCount 总数据量
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 获取不分页时的总页数.
     *
     * @return 总页数
     */
    public int getTotalPages() {
        return (int) Math.ceil(1.0 * totalCount / pageSize);
    }

    /**
     * 当前页是否超出总页数范围.
     *
     * @return true - 已超出页码范围. false - 未超出范围.
     */
    @JsonIgnore
    public boolean isOverflowed() {
        return getIndex() >= totalCount;
    }

    /**
     * 获取当前数据索引.
     *
     * @return 索引
     */
    @JsonIgnore
    public int getIndex() {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 获取当前页数据列表.
     *
     * @return 当前页数据列表
     */
    public List<T> getElements() {
        return elements;
    }

    /**
     * 设置当前页数据列表.
     *
     * @param elements 当前页数据列表
     */
    public void setElements(List<T> elements) {
        this.elements = elements;
    }

    public String toJson() {
        return "{\"page\":" + pageNo + ",\"size\":" + pageSize + ",\"total\":" + totalCount + "}";
    }

    /**
     * {@inheritDoc }. 与{@link #toJson() }等价
     *
     * @return JSON字符串
     */
    @Override
    public String toString() {
        return toJson();
    }
}
