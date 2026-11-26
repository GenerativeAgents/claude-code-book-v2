package com.example.ec.order.client;

import com.example.ec.common.BusinessException;
import java.util.List;

/** 商品・在庫サービス（catalog-service）の在庫APIを呼び出す。 */
public interface CatalogClient {

    /**
     * 受注の明細について在庫を引き当てる（CAT-020）。
     *
     * @throws BusinessException 在庫が不足している場合（E0108）
     */
    void reserve(String orderNo, List<ReserveLine> lines);

    /** 受注の在庫引当を解除する（CAT-021）。引当中の明細が無くても正常に終わる。 */
    void release(String orderNo);
}
