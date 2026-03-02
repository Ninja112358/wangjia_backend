package com.ninja.wangjia_backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 * @author <a href="https://github.com/Ninja112358">Ninja</a>
 */
@Data
public class UserUpdateRequest implements Serializable {
    private Long id;
    private String userAccount;
    private String userName;
    private String userAvatar;
    private String userProfile;
    private String userRole;
    private static final long serialVersionUID = 1L;
}
