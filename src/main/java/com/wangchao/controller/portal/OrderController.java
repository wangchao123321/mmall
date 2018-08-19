package com.wangchao.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.wangchao.common.Const;
import com.wangchao.common.RedisPool;
import com.wangchao.common.ResponseCode;
import com.wangchao.common.ServerResponse;
import com.wangchao.pojo.User;
import com.wangchao.service.IOrderService;
import com.wangchao.util.CookieUtil;
import com.wangchao.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;



    @RequestMapping("create.do")
    public ServerResponse create(HttpServletRequest request, Integer shippingId){
        String loginToken= CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr= RedisPool.getJedis().get(loginToken);
        User user= JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("cancle.do")
    public ServerResponse cancle(HttpServletRequest request, Long orderNo){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancle(user.getId(),orderNo);
    }


    @RequestMapping("getOrderCartProduct.do")
    public ServerResponse getOrderCartProduct(HttpServletRequest request){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("detail.do")
    public ServerResponse detail(HttpServletRequest request,Long orderNo){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @RequestMapping("list.do")
    public ServerResponse list(HttpServletRequest request,
                               @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }


    @RequestMapping("pay.do")
    public ServerResponse pay(HttpServletRequest request, Long orderNo){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String pash=request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),pash);
    }

    @RequestMapping("alipayCallback.do")
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params= Maps.newHashMap();

        Map requestParams=request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
            String name= (String) iter.next();
            String[] values= (String[]) requestParams.get(name);
            String valueStr="";
            for (int i = 0; i < values.length; i++) {
                valueStr=(i==values.length-1)?valueStr+values[i]:valueStr+values[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("回调参数: {}",params.toString());

        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2= AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if(!alipayRSACheckedV2){
                return ServerResponse.createBySuccess("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
            e.printStackTrace();
        }
        // todo 验证各种数据


        ServerResponse serverResponse=iOrderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }

        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    @RequestMapping("queryOrderPayStatus.do")
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest request, Long orderNo){
        String loginToken=CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userJsonStr=RedisPool.getJedis().get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }else {
            return ServerResponse.createBySuccess(false);
        }

    }
}
