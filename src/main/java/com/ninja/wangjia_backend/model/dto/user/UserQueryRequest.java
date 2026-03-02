package com.ninja.wangjia_backend.model.dto.user;

import com.ninja.wangjia_backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 * @author <a href="https://github.com/Ninja112358">Ninja</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private String userName;
    private String userAccount;
    private String userProfile;
    private String userRole;
    private static final long serialVersionUID = 1L;
}
