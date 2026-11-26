package com.example.ec.catalog.controller;

import com.example.ec.catalog.dto.ProductDetailDto;
import com.example.ec.catalog.dto.ProductSearchCondition;
import com.example.ec.catalog.dto.ProductSummaryDto;
import com.example.ec.catalog.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 商品照会（CAT-001）と商品検索（CAT-002）のAPI。 */
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** GET /products/{productCode} */
    @GetMapping("/products/{productCode}")
    public ProductDetailDto findDetail(@PathVariable String productCode) {
        return productService.findDetail(productCode);
    }

    /** GET /products（クエリ：keyword、categoryCode、inStockOnly） */
    @GetMapping("/products")
    public List<ProductSummaryDto> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(defaultValue = "false") boolean inStockOnly) {
        return productService.search(new ProductSearchCondition(keyword, categoryCode, inStockOnly));
    }
}
