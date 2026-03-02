package com.ninja.wangjia_backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户添加请求
 * @author <a href="https://github.com/Ninja112358">Ninja</a>
 */
@Data
public class UserAddRequest implements Serializable {
    private String userName;
    private String userAccount;
    private String userAvatar;
    private String userProfile;
    private String userRole;
    private static final long serialVersionUID = 1L;
}
