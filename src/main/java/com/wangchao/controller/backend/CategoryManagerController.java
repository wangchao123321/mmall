package com.wangchao.controller.backend;

import com.wangchao.common.Const;
import com.wangchao.common.ResponseCode;
import com.wangchao.common.ServerResponse;
import com.wangchao.pojo.User;
import com.wangchao.service.ICategoryService;
import com.wangchao.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/category")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("addCategory.do")
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
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
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
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
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
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
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
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
