package com.ninja.wangjia_backend.common;

import lombok.Data;
/**
 * 分页请求
 * @author <a href="https://github.com/Ninja112358">Ninja</a>
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}
