package com.ninja.wangjia_backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 * @author <a href="https://github.com/Ninja112358">Ninja</a>
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
