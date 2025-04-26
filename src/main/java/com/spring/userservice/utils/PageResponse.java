package com.spring.userservice.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private final List<T> content;
    private final int     pageNumber;
    private final int     pageSize;
    private final long    totalElements;
    private final int     totalPages;
}
