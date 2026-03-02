package com.ninja.wangjia_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ninja.wangjia_backend.model.dto.user.UserQueryRequest;
import com.ninja.wangjia_backend.model.entity.User;
import com.ninja.wangjia_backend.model.vo.LoginUserVO;
import com.ninja.wangjia_backend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 针对表【user(用户)】的数据库操作Service
 * @author <a href="https://github.com/Ninja112358">Ninja</a>
 */

public interface UserService extends IService<User> {
    /**
     * 用户注册方法
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return long
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录方法
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request http请求
     * @return 登录后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);
    /**
     * 获取加密密码
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取当前登录用户
     * @param request http请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * 获取脱敏后的登录用户信息
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);
    /**
     * 获取脱敏后的用户信息
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息列表
     * @param userList 用户信息列表
     * @return 脱敏后的用户信息列表
     */
    List<UserVO> getUserVOList(List<User> userList);
    /**
     * 用户注销方法
     * @param request http请求
     */
    boolean userLogout(HttpServletRequest request);
    /**
     * 获取查询包装类
     * @param userQueryRequest 用户查询请求
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
