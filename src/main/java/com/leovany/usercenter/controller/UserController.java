package com.leovany.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leovany.usercenter.common.ResultVO;
import com.leovany.usercenter.common.ErrorCode;
import com.leovany.usercenter.common.ResultUtils;
import com.leovany.usercenter.constant.UserConstant;
import com.leovany.usercenter.exception.BusinessException;
import com.leovany.usercenter.model.User;
import com.leovany.usercenter.model.request.UserLoginRequest;
import com.leovany.usercenter.model.request.UserRegisterRequest;
import com.leovany.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResultVO<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            // 处理异常类
            throw new BusinessException(ErrorCode.ERROR_PARAMS);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS, "参数为空");
        }

        long userId = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public ResultVO<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS);
        }
        User user = userService.doLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public ResultVO<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS);
        }
        userService.userLogout(request);
        return ResultUtils.success();
    }

    @GetMapping("/current")
    public ResultVO<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (ObjectUtils.isEmpty(currentUser)) {
            throw new BusinessException(ErrorCode.ERROR_NOT_LOGIN);
        }
        Long userId = currentUser.getId();
        // TODO: 校验用户是否合法
        User user = userService.getById(userId);
        User safeUser = userService.getSafeUser(user);
        return ResultUtils.success(safeUser);
    }


    @GetMapping("/search")
    public ResultVO<List<User>> searchUsers(String username, HttpServletRequest request) {
        // 鉴权
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.ERROR_NO_AUTH, "缺少管理员权限");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> safeUserList = userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return ResultUtils.success(safeUserList);
    }

    @PostMapping("/delete")
    public ResultVO<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        // 鉴权
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.ERROR_NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS);
        }
        boolean isOK = userService.removeById(id);
        return ResultUtils.success(isOK);
    }

    @GetMapping("/search/tags")
    public ResultVO<List<User>> searchUsersByTags(@RequestParam List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.ERROR_PARAMS);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null || user.getUserRole() != UserConstant.ROLE_ADMIN) {
            return false;
        }
        return true;
    }


}
