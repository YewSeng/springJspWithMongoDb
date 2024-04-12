package com.caltech.utils;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.caltech.pojo.Admin;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPage implements CustomPageable<Admin> {
    private List<Admin> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    
    public AdminPage(int pageNumber, int pageSize) {
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
