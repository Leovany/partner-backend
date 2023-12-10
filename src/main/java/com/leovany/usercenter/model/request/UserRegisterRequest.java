package com.leovany.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    String userAccount;

    String userPassword;

    String checkPassword;

    String planetCode;
}
