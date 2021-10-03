package com.codingandshare.tracking.dtos;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Define json page response
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
@Getter
public class PageDTO<T> {

  private final List<T> content;

  private final String sort;

  private final Long total;

  private final Integer totalPage;

  private final Integer page;

  private final Integer pageSize;

  public PageDTO(Page<T> page) {
    this.content = page.getContent();
    this.sort = page.getSort().toString();
    this.total = page.getTotalElements();
    this.pageSize = page.getPageable().getPageSize();
    this.page = page.getPageable().getPageNumber();
    this.totalPage = page.getTotalPages();
  }
}
