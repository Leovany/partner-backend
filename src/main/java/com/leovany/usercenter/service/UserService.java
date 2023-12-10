package com.leovany.usercenter.service;

import com.leovany.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leovany.usercenter.model.request.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author leovany
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2023-09-03 17:53:32
 */
public interface UserService extends IService<User> {


    long userRegister(UserRegisterRequest userRegisterRequest);

    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户数据脱敏
     * @param user
     * @return
     */
    User getSafeUser(User user);

    void userLogout(HttpServletRequest request);
}
