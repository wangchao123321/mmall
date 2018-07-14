package com.wangchao.service;

import com.github.pagehelper.PageInfo;
import com.wangchao.common.ServerResponse;
import com.wangchao.pojo.Shipping;

public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse<String > delete(Integer userId, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize);
}
