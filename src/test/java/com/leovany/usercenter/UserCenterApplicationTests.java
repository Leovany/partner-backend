package com.leovany.usercenter;

import com.leovany.usercenter.mapper.UserMapper;
import com.leovany.usercenter.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;


import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class UserCenterApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Test
    void testUserSize() {
        List<User> users = userMapper.selectList(null);
        Assertions.assertEquals(5, users.size());
        users.forEach(System.out::println);
    }

}
