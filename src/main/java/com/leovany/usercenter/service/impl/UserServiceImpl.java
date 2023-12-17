package com.leovany.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.leovany.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author leovany
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-09-03 17:53:32
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

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
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);

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
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS);
        }
        // 1. 先查询所有用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        List<User> userList = baseMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2. 在内存中判断是否包含要求的标签
        List<User> newUserList = userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafeUser).collect(Collectors.toList());

//        if (CollectionUtils.isEmpty(tagNameList)) {
//            throw new BusinessException(ErrorCode.ERROR_PARAMS);
//        }
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        tagNameList.forEach(tagName -> {
//            queryWrapper.like(User::getTags, tagName);
//        });
//        List<User> userList = list(queryWrapper);

        return newUserList;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.ERROR_NO_AUTH);
        }
        return (User) userObj;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS);
        }
        // todo 补充校验，如果用户没有传任何要更新的值，就直接报错，不用执行 update 语句
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.ERROR_NO_AUTH);
        }
        User oldUser = getById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.ERROR_NULL);
        }
        return userMapper.updateById(user);
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null || user.getUserRole() != UserConstant.ROLE_ADMIN) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == UserConstant.ROLE_ADMIN;
    }

}




