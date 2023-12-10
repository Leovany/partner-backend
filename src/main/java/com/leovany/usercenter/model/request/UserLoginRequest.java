package com.leovany.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    String userAccount;

    String userPassword;

}
