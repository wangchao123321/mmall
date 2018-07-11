package com.wangchao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wangchao.common.Const;
import com.wangchao.common.ResponseCode;
import com.wangchao.common.ServerResponse;
import com.wangchao.dao.CategoryMapper;
import com.wangchao.dao.ProductMapper;
import com.wangchao.pojo.Category;
import com.wangchao.pojo.Product;
import com.wangchao.service.ICategoryService;
import com.wangchao.service.IProductService;
import com.wangchao.util.DateTimeUtil;
import com.wangchao.util.PropertiesUtil;
import com.wangchao.vo.ProductDetailVO;
import com.wangchao.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceService implements IProductService {


    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImagesArray=product.getSubImages().split(",");
                if(subImagesArray.length>0){
                    product.setMainImage(subImagesArray[0]);
                }
            }

            // 更新
            if(product.getId()!=null){
                int rowCount=productMapper.updateByPrimaryKeySelective(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccess("更新成功");
                }
                    return ServerResponse.createByErrorMessage("更新失败");
            }else{
                //添加
                int rowCount=productMapper.insert(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccess("新增成功");
                }
                return ServerResponse.createByErrorMessage("新增失败");
            }


        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    @Override
    public ServerResponse<String> setSeleStatus(Integer productId, Integer status) {
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount=productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccess("修改销售状态成功");
        }
        return ServerResponse.createBySuccessMessage("修改销售状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVO> manageProductDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }

        ProductDetailVO productDetailVO=assembleProductDetailVO(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }

    @Override
    public ServerResponse<PageInfo> getProduct(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectList();

        List<ProductListVO> productListVOS= Lists.newArrayList();
        for (Product product : productList) {
            ProductListVO productListVO = assembleProductListVO(product);
            productListVOS.add(productListVO);
        }
        PageInfo pageInfo=new PageInfo<>(productListVOS);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);

        List<ProductListVO> productListVOS= Lists.newArrayList();
        for (Product product : productList) {
            ProductListVO productListVO = assembleProductListVO(product);
            productListVOS.add(productListVO);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVOS);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }

        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }

        ProductDetailVO productDetailVO=assembleProductDetailVO(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize,String orderBy) {
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList=new ArrayList<>();
        if(categoryId != null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> productListVOS=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVOS);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList=iCategoryService.getCategoryAndDeepChildrenCategory(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray=orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }

        List<Product> productList=productMapper.selectByNameAndCategoryIds(
                StringUtils.isBlank(keyword)? null : keyword,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVO> productListVOList=Lists.newArrayList();
        for (Product product : productList) {
            ProductListVO productListVO=assembleProductListVO(product);
            productListVOList.add(productListVO);
        }

        PageInfo pageInfo=new PageInfo<>(productList);
        pageInfo.setList(productListVOList);

        return ServerResponse.createBySuccess(pageInfo);
    }


    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO=new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setName(product.getName());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setImagesHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://images.happymmall.com"));
        return productListVO;
    }

    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO productDetailVO=new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setName(product.getName());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());

        productDetailVO.setImagesHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://images.happymmall.com"));

        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVO.setParentCategoryId(0);// 默认根节点
        }else{
            productDetailVO.setParentCategoryId(category.getParentId());
        }

        productDetailVO.setCreateTime(DateTimeUtil.datetoStr(product.getCreateTime()));
        productDetailVO.setUpdateTime(DateTimeUtil.datetoStr(product.getUpdateTime()));
        return productDetailVO;
    }
}
