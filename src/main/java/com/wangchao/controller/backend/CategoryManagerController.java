package com.wangchao.controller.backend;

import com.wangchao.common.Const;
import com.wangchao.common.RedisPool;
import com.wangchao.common.ResponseCode;
import com.wangchao.common.ServerResponse;
import com.wangchao.pojo.User;
import com.wangchao.service.ICategoryService;
import com.wangchao.service.IUserService;
import com.wangchao.util.CookieUtil;
import com.wangchao.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/category")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("addCategory.do")
    public ServerResponse addCategory(HttpServletRequest request, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") int parentId){
        String loginToken= CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr= RedisPool.getJedis().get(loginToken);
        User user= JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByError();
        }
    }

    @RequestMapping("setCategoryName.do")
    public ServerResponse setCategoryName(HttpServletRequest request,Integer categoryId,String categoryName){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByError();
        }
    }

    @RequestMapping("getChildrenParallelCategory.do")
    public ServerResponse getChildrenParallelCategory(HttpServletRequest request,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByError();
        }
    }

    @RequestMapping("getCategoryAndDeepChildrenCategory.do")
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest request,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){
            return iCategoryService.getCategoryAndDeepChildrenCategory(categoryId);
        }else{
            return ServerResponse.createByError();
        }
    }

}
