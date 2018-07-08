package com.wangchao.service;

import com.wangchao.common.ServerResponse;
import com.wangchao.pojo.User;

public interface IUserService {

    ServerResponse<User> login(String userName, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> selectQuestion(String userName);

    ServerResponse<String> selectAnswer(String userName,String question,String answer);

    ServerResponse<String> forgetResetPassword(String userName,String passwordNew,String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse<User> updateInfomation(User user);

    ServerResponse<User> getInfomation(Integer userId);

    ServerResponse checkAdminRole(User user);

}
