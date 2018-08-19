package com.wangchao.controller.common;

import com.wangchao.common.Const;
import com.wangchao.common.RedisPool;
import com.wangchao.pojo.User;
import com.wangchao.util.CookieUtil;
import com.wangchao.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;

        String loginToken= CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr=RedisPool.getJedis().get(loginToken);
            User user= JsonUtil.string2Obj(userJsonStr,User.class);
            if(user!=null){
                RedisPool.getJedis().expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
