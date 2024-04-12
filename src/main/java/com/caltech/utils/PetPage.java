package com.caltech.utils;

import java.util.List;
import org.springframework.data.domain.Sort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.caltech.pojo.Pet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetPage implements CustomPageable<Pet> {
    private List<Pet> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    
    public PetPage(int pageNumber, int pageSize) {
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
		return Sort.by(Sort.Direction.ASC, "petType");
	}
}
