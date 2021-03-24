package com.antdigital.agency.dtos.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class BaseSearchDto<T> {
    private int currentPage;
    private int recordOfPage;
    private long totalRecords;
    private boolean sortAsc = true;
    private String sortBy;
    private String createdDateSort;
    private int pagingRange;
    private T result;
}
