package com.wangchao.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.wangchao.common.Const;
import com.wangchao.common.ResponseCode;
import com.wangchao.common.ServerResponse;
import com.wangchao.dao.CartMapper;
import com.wangchao.dao.ProductMapper;
import com.wangchao.pojo.Cart;
import com.wangchao.pojo.Product;
import com.wangchao.service.ICartService;
import com.wangchao.util.BigDecimalUtil;
import com.wangchao.util.PropertiesUtil;
import com.wangchao.vo.CartProductVO;
import com.wangchao.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService{

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;


    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart == null){
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setChecked(Const.Cart.CHEKED);
            cartMapper.insert(cartItem);
        }else{
            count= cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart =cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo=getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer checked,Integer productId) {
        cartMapper.checkedtOrUnCheckedProduct(userId,checked,productId);
        return list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo=new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVO> cartProductVOList= Lists.newArrayList();

        BigDecimal carTotalPrice=new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for (Cart cart : cartList) {
                CartProductVO cartProductVO=new CartProductVO();
                cartProductVO.setId(cart.getId());
                cartProductVO.setUserId(userId);
                cartProductVO.setProductId(cart.getProductId());

                Product product=productMapper.selectByPrimaryKey(cart.getProductId());
                if(product!=null){
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount=0;
                    if(product.getStock() >= cart.getQuantity()){
                        buyLimitCount=cart.getQuantity();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount=product.getStock();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        // 购物车中更新有效库存
                        Cart cartForQuantity=new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVO.setQuantity(buyLimitCount);
                    // 计算总价
                    cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVO.getQuantity()));
                    cartProductVO.setProductChecked(cart.getChecked());
                }


                if(cart.getChecked() == Const.Cart.CHEKED){
                    // 如果已经勾选,增加到整个的购物车总价中
                    carTotalPrice=BigDecimalUtil.add(carTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }

                cartProductVOList.add(cartProductVO);
            }
        }

        cartVo.setCartTotalPrice(carTotalPrice);
        cartVo.setCartProductVoList(cartProductVOList);
        cartVo.setAllChecked(getAllchekedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean getAllchekedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
