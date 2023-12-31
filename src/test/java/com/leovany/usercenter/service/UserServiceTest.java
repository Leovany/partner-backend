package com.leovany.usercenter.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.leovany.usercenter.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() {
        System.out.println("==");
        User user = new User();

        user.setUsername("Leovany");
        user.setUserAccount("123");
        user.setUserPasspword("456");
        user.setGender(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        boolean result = userService.save(user);
        System.out.println(user.getId());
        System.out.println(result);
        Assertions.assertTrue(result);
    }

    @Test
    void searchUsersByTags() {
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assert.assertNotEquals(userList.size(), 0);

    }
}