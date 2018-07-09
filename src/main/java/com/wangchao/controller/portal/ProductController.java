package com.wangchao.controller.portal;

import com.wangchao.common.ServerResponse;
import com.wangchao.service.IProductService;
import com.wangchao.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    public ServerResponse<ProductDetailVO> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

}
