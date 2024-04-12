package com.caltech.utils;

import java.util.List;
import org.springframework.data.domain.Sort;

public interface CustomPageable<T> {
    List<T> getContent();
    int getPageNumber();
    int getPageSize();
    long getTotalElements();
    int getTotalPages();
    boolean hasNext();
    boolean hasPrevious();

    // Default values for page number and page size
    int DEFAULT_PAGE_NUMBER = 0;
    int DEFAULT_PAGE_SIZE = 5;
    
    // Default sorting order
    default Sort getDefaultSort() {
        return Sort.by(Sort.Direction.ASC, "username");
    }
}

