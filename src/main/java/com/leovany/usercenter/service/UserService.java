package com.leovany.usercenter.service;

import com.leovany.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leovany.usercenter.model.request.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     *
     * @param user
     * @return
     */
    User getSafeUser(User user);

    void userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 更新用户信息
     *
     * @param user
     * @param loginUser
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);
}
