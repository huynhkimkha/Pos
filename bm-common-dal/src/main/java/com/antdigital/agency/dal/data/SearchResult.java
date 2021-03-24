package com.antdigital.agency.dal.data;

import lombok.Data;

@Data
public class SearchResult<T> {
    private T result;
    private long totalRecords;
}
