package com.leovany.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leovany.usercenter.common.ErrorCode;
import com.leovany.usercenter.constant.UserConstant;
import com.leovany.usercenter.exception.BusinessException;
import com.leovany.usercenter.mapper.UserMapper;
import com.leovany.usercenter.model.User;
import com.leovany.usercenter.model.request.UserRegisterRequest;
import com.leovany.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leovany
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-09-03 17:53:32
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值
     */
    final private static String SALT = "Leovany";

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1. 校验
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "用户账号过段");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "两次输入密码不一致");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "星球编号过短");
        }

        // 特殊字符的正则表达式
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "账号存在特殊字符");
        }

        // 2. 重复
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "账号已存在");
        }
        // 3. 加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 4. 保存
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPasspword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "保存失败");
        }
        return user.getId();
    }

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "密码过短");
        }

        // 特殊字符的正则表达式
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "账号存在特殊字符");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 2. 账号不存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        queryWrapper.eq(User::getUserPasspword, encryptPassword);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.ERROR_NULL, "账号不存在");
        }
        // 3. 用户脱敏
        User safeUser = getSafeUser(user);

        // 4. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safeUser);

        return safeUser;
    }

    @Override
    public User getSafeUser(User user) {
        User safeUser = new User();
        if (user == null) {
            return null;
        }
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setPlanetCode(user.getPlanetCode());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setTags(user.getTags());
        return safeUser;
    }

    @Override
    public void userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        tagNameList.forEach(tagName -> {
            queryWrapper.like(User::getTags, tagName);
        });
        List<User> userList = list(queryWrapper);

        return userList;
    }
}




