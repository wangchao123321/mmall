package com.wangchao.service;

import com.wangchao.common.ServerResponse;
import com.wangchao.pojo.Product;
import com.wangchao.vo.ProductDetailVO;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSeleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVO> manageProductDetail(Integer productId);
}
