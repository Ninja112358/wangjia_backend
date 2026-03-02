package com.ninja.wangjia_backend.constant;

/**
 * 用户常量
 * @author <a href="https://github.com/Ninja112358">Ninja</a>
 */
public interface UserConstant {
    /**
     * 用户登录状态
     */
    String USER_LOGIN_STATE = "user_login";
    String ENCRYPTION_SALT = "ninja";
    //region 权限
    /**
     * 默认角色（普通用户）
     */
    String DEFAULT_ROLE = "user";
    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";
    //endregion

}
