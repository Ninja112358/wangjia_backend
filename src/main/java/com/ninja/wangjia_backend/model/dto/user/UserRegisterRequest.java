package com.ninja.wangjia_backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * @author <a href="https://github.com/Ninja112358">Ninja</a>
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 5874567786720835491L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
