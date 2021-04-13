package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.IProductCategoryRepository;
import com.antdigital.agency.dal.repository.IProductRepository;
import com.antdigital.agency.dal.repository.IProductSizeRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.IProductCategoryDtoMapper;
import com.antdigital.agency.mappers.IProductDtoMapper;
import com.antdigital.agency.mappers.IProductSizeDtoMapper;
import com.antdigital.agency.services.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IProductCategoryRepository productCategoryRepository;

    @Autowired
    private IProductSizeRepository productSizeRepository;

    @Override
    public List<ProductDto> findAll() {
        List<Product> productList = productRepository.findAll();
        return IProductDtoMapper.INSTANCE.toProductDtoList(productList);
    }

    @Override
    public BaseSearchDto<List<ProductDto>> findAll(BaseSearchDto<List<ProductDto>> searchDto) {
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll());
            return searchDto;
        }

        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<Product> page = productRepository.findAll(request);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IProductDtoMapper.INSTANCE.toProductDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public ProductDto getById(String id) {
        Product product = productRepository.findById(id).get();
        return IProductDtoMapper.INSTANCE.toProductDto(product);
    }

    @Override
    public ProductDto getBySlug(String nameSlug) {
        Product product = productRepository.getBySlug(nameSlug);
        return IProductDtoMapper.INSTANCE.toProductDto(product);
    }

    @Override
    public ProductDto getByName(String name) {
        Product product = productRepository.getByName(name);
        return IProductDtoMapper.INSTANCE.toProductDto(product);
    }

    @Override
    public List<ProductDto> getLikeSlugOrName(String name) {
        List<Product> productList = productRepository.getLikeSlugOrName(name);
        return IProductDtoMapper.INSTANCE.toProductDtoList(productList);
    }

    @Override
    public List<ProductDto> getLikeSlug(String nameSlug) {
        List<Product> productList = productRepository.getLikeSlug(nameSlug);
        return IProductDtoMapper.INSTANCE.toProductDtoList(productList);
    }

    @Override
    public List<ProductDto> getProducts(List<String> ids) {
        List<Product> productList = productRepository.getProductsById(ids);
        return IProductDtoMapper.INSTANCE.toProductDtoList(productList);
    }

    @Override
    @Transactional
    public ProductFullDto insert(ProductFullDto productDto) {
        try {
            Product product = IProductDtoMapper.INSTANCE.toProduct(productDto);
            product.setId(UUIDHelper.generateType4UUID().toString());
            product = productRepository.save(product);

            for(ProductCategoryDto detail : productDto.getProductCategoryList()) {
                if (detail.getCategory() == null || detail.getCategory().getId() == null
                        || detail.getCategory().getId().isEmpty()) {
                    continue;
                }

                ProductCategory tempDetail = IProductCategoryDtoMapper.INSTANCE.toProductCategory(detail);
                tempDetail.setId(UUIDHelper.generateType4UUID().toString());
                tempDetail.setProduct(product);

                productCategoryRepository.save(tempDetail);
            }
            for(ProductSizeDto detail : productDto.getProductSizeList()) {
                if (detail.getSize() == null || detail.getSize().getId() == null
                        || detail.getSize().getId().isEmpty()) {
                    continue;
                }

                ProductSize tempDetail = IProductSizeDtoMapper.INSTANCE.toProductSize(detail);
                tempDetail.setId(UUIDHelper.generateType4UUID().toString());
                tempDetail.setProduct(product);

                productSizeRepository.save(tempDetail);
            }

            productDto.setId(product.getId());
            return productDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public ProductFullDto update(ProductFullDto productDto) {
        try {
            Product old = productRepository.findById(productDto.getId()).get();
            Product product = IProductDtoMapper.INSTANCE.toProduct(productDto);

            productRepository.save(product);
            // collect detail was removed.
            List<ProductCategory> productCategoryList = productCategoryRepository.getByProductId(productDto.getId());
            List<String> detailDelete = new ArrayList<>();
            for(ProductCategory item : productCategoryList) {
                if (item.getId() == null || item.getId().isEmpty()) {
                    continue;
                }

                int index = productDto.getProductCategoryList().stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                int isExist = detailDelete.indexOf(item.getId());
                if (index == -1 && isExist == -1) {
                    detailDelete.add(item.getId());
                }
            }
            for(String id : detailDelete) {
                productCategoryRepository.deleteById(id);
            }

            // collect detail was removed.
            List<ProductSize> productSizeList = productSizeRepository.getByProductId(productDto.getId());
            List<String> productSizeDeleteList = new ArrayList<>();

            for(ProductSize item : productSizeList) {
                if (item.getId() == null || item.getId().isEmpty()) {
                    continue;
                }

                int index = productDto.getProductSizeList().stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                int isExist = productSizeDeleteList.indexOf(item.getId());
                if (index == -1 && isExist == -1) {
                    productSizeDeleteList.add(item.getId());
                }
            }
            for(String id : productSizeDeleteList) {
                productSizeRepository.deleteById(id);
            }

            for(ProductCategoryDto productCategoryDto : productDto.getProductCategoryList()) {
                if (productCategoryDto.getId() == null || productCategoryDto.getId().isEmpty()) {
                    ProductDto productDto1 = new ProductDto();
                    productDto1.setId(productDto.getId());

                    productCategoryDto.setId(UUIDHelper.generateType4UUID().toString());
                    productCategoryDto.setProduct(productDto1);
                }
                ProductCategory productCategory = IProductCategoryDtoMapper.INSTANCE.toProductCategory(productCategoryDto);
                productCategory = productCategoryRepository.save(productCategory);
                productCategoryDto = IProductCategoryDtoMapper.INSTANCE.toProductCategoryDto(productCategory);
            }

            for(ProductSizeDto productSizeDto : productDto.getProductSizeList()) {
                if (productSizeDto.getId() == null || productSizeDto.getId().isEmpty()) {
                    ProductDto productDto1 = new ProductDto();
                    productDto1.setId(productDto.getId());

                    productSizeDto.setId(UUIDHelper.generateType4UUID().toString());
                    productSizeDto.setProduct(productDto1);
                }
                ProductSize productSize = IProductSizeDtoMapper.INSTANCE.toProductSize(productSizeDto);
                productSize = productSizeRepository.save(productSize);
                productSizeDto = IProductSizeDtoMapper.INSTANCE.toProductSizeDto(productSize);
            }

            return productDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        try {
            ProductFullDto productFullDto = this.getProductFull(id);
            for(ProductCategoryDto detailDto : productFullDto.getProductCategoryList()) {
                productCategoryRepository.deleteById(detailDto.getId());
            }
            for(ProductSizeDto detailDto : productFullDto.getProductSizeList()) {
                productSizeRepository.deleteById(detailDto.getId());
            }
            productRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public ProductFullDto getProductFull(String productId) {
        try {
            Product product = productRepository.findById(productId).get();
            List<ProductCategory> details = productCategoryRepository.getByProductId(product.getId());
            List<ProductSize> productSizeList = productSizeRepository.getByProductId(product.getId());
            ProductFullDto productFullDto = IProductDtoMapper.INSTANCE.toProductFullDto(product);
            List<ProductCategoryDto> detailDto = IProductCategoryDtoMapper.INSTANCE.toProductCategoryDtoList(details);
            List<ProductSizeDto> productSizeDtoList = IProductSizeDtoMapper.INSTANCE.toProductSizeDtoList(productSizeList);
            productFullDto.setProductCategoryList(detailDto);
            productFullDto.setProductSizeList(productSizeDtoList);
            return productFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }
}
