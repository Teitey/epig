package com.sweet.gen.pagination;

public class Pagination {

	public static final int DEFAULT_PAGE_SIZE = 15;

	private int pageSize;
	private int currentPage;
	private int prePage;
	private int nextPage;
	private int totalPage;
	private int totalCount;
	private int page;// jquery-easyui分页时当前页变量名为page
	private int rows;// jquery-easyui分页时每页记录数变量名为rows

	public Pagination() {
		this.currentPage = 1;
		this.page = 1;
		this.pageSize = DEFAULT_PAGE_SIZE;
		this.rows = DEFAULT_PAGE_SIZE;
		this.nextPage = currentPage + 1;
		this.prePage = currentPage - 1;
	}

	/**
	 * 
	 * @param currentPage
	 * @param pageSize
	 */
	public Pagination(int currentPage, int pageSize) {
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.nextPage = currentPage + 1;
		this.prePage = currentPage - 1;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		this.nextPage = currentPage + 1;
		this.prePage = currentPage - 1;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPrePage() {
		return prePage;
	}

	public void setPrePage(int prePage) {
		this.prePage = prePage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

}
