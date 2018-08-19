package com.wangchao.controller.portal;

import com.wangchao.common.Const;
import com.wangchao.common.RedisPool;
import com.wangchao.common.ResponseCode;
import com.wangchao.common.ServerResponse;
import com.wangchao.pojo.User;
import com.wangchao.service.ICartService;
import com.wangchao.util.CookieUtil;
import com.wangchao.util.JsonUtil;
import com.wangchao.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("list.do")
    public ServerResponse<CartVo> list(HttpServletRequest request, Integer count, Integer productId){
        String loginToken= CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr= RedisPool.getJedis().get(loginToken);
        User user= JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("add.do")
    public ServerResponse<CartVo> add(HttpServletRequest request,Integer count,Integer productId){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }


    @RequestMapping("update.do")
    public ServerResponse<CartVo> update(HttpServletRequest request,Integer count,Integer productId){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("deleteProduct.do")
    public ServerResponse deleteProduct(HttpServletRequest request,String productIds){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }


    //全选
    @RequestMapping("selectAll.do")
    public ServerResponse<CartVo> selectAll(HttpServletRequest request){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectAll(user.getId(),Const.Cart.CHEKED,null);
    }

    //全反选
    @RequestMapping("unselectAll.do")
    public ServerResponse<CartVo> unselectAll(HttpServletRequest request){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectAll(user.getId(),Const.Cart.UNCHEKED,null);
    }

    //单独选
    @RequestMapping("select.do")
    public ServerResponse<CartVo> select(HttpServletRequest request,Integer productId){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectAll(user.getId(),Const.Cart.CHEKED,productId);
    }
    //单独反选
    @RequestMapping("unselect.do")
    public ServerResponse<CartVo> unselect(HttpServletRequest request,Integer productId){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectAll(user.getId(),Const.Cart.UNCHEKED,productId);
    }

    //查询当前用户的购物车里面的产品数量,如果一个产品有10个，数量就是10
    @RequestMapping("getCartProductCount.do")
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest request){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }

}
