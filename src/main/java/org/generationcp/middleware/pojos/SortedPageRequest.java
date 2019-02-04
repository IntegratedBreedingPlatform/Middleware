package org.generationcp.middleware.pojos;


public class SortedPageRequest {
	
	private Integer pageNumber;
	private Integer pageSize;
	private String sortBy; 
	private String sortOrder;
	
	
	public SortedPageRequest(final Integer pageNumber, final Integer pageSize, final String sortBy, final String sortOrder) {
		super();
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.sortBy = sortBy;
		this.sortOrder = sortOrder;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public String getSortBy() {
		return sortBy;
	}
	
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	
	public String getSortOrder() {
		return sortOrder;
	}
	
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

}
