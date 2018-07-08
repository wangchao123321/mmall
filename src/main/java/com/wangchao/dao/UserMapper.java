package com.wangchao.dao;

import com.wangchao.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String userName);

    int checkUerEmail(String email);

    User selectLogin(@Param("userName") String userName,@Param("password") String password);

    String selectQuestionByUserName(String userName);

    int checkAnwser(@Param("userName")String userName,@Param("question") String question,@Param("answer") String answer);

    int updatePasswordByUserName(@Param("userName")String userName,@Param("password")String password);

    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);

    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);
}