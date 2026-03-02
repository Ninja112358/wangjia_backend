package com.ninja.wangjia_backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 * @author <a href="https://github.com/Ninja112358">Ninja</a>
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 3497388033290455683L;
    private String userAccount;
    private String userPassword;
}
