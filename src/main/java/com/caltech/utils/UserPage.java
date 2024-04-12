package com.caltech.utils;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.caltech.pojo.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPage implements CustomPageable<User> {
    private List<User> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public UserPage(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
    
    @Override
    public boolean hasNext() {
        return pageNumber < totalPages - 1;
    }

    @Override
    public boolean hasPrevious() {
        return pageNumber > 0;
    }
}
