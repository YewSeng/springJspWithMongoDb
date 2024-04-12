package com.caltech.utils;

import java.util.List;
import org.springframework.data.domain.Sort;
import com.caltech.pojo.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorPage implements CustomPageable<Doctor>  {
    private List<Doctor> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    
    public DoctorPage(int pageNumber, int pageSize) {
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
    
	@Override
	public Sort getDefaultSort() {
		return Sort.by(Sort.Direction.ASC, "status");
	}
}
