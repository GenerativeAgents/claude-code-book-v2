package com.example.ec.catalog.service;

import com.example.ec.catalog.dao.ProductDao;
import com.example.ec.catalog.dao.ProductSearchDao;
import com.example.ec.catalog.dto.ProductDetailDto;
import com.example.ec.catalog.dto.ProductSearchCondition;
import com.example.ec.catalog.dto.ProductSummaryDto;
import com.example.ec.common.BusinessException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 商品照会（CAT-001）と商品検索（CAT-002）。 */
@Service
public class ProductService {

    private final ProductDao productDao;
    private final ProductSearchDao productSearchDao;

    public ProductService(ProductDao productDao, ProductSearchDao productSearchDao) {
        this.productDao = productDao;
        this.productSearchDao = productSearchDao;
    }

    /**
     * 商品を照会する。
     *
     * @throws BusinessException 商品が存在しない、または削除済みの場合（E0201）
     */
    @Transactional
    public ProductDetailDto findDetail(String productCode) {
        return productDao.findDetail(productCode)
                .orElseThrow(() -> BusinessException.notFound("E0201", productCode));
    }

    /** 販売中の商品を検索する。 */
    @Transactional
    public List<ProductSummaryDto> search(ProductSearchCondition condition) {
        return productSearchDao.search(condition);
    }
}
