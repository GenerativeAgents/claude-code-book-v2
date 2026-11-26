package com.example.ec.catalog.service;

import com.example.ec.catalog.dao.ReservationDao;
import com.example.ec.catalog.dao.StockDao;
import com.example.ec.catalog.dto.ReleaseRequestDto;
import com.example.ec.catalog.dto.ReserveLineDto;
import com.example.ec.catalog.dto.ReserveRequestDto;
import com.example.ec.catalog.dto.StockDto;
import com.example.ec.common.BusinessException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 在庫引当（CAT-020）と在庫引当解除（CAT-021）。 */
@Service
public class StockService {

    private final StockDao stockDao;
    private final ReservationDao reservationDao;

    public StockService(StockDao stockDao, ReservationDao reservationDao) {
        this.stockDao = stockDao;
        this.reservationDao = reservationDao;
    }

    /**
     * 全明細を1トランザクションで引き当てる。1明細でも引き当てられなければ、すべて取り消す。
     *
     * @throws BusinessException 明細が無い、または数量が1未満の場合（E0203）、商品の在庫が無い場合（E0201）、
     *                           引当可能数が足りない場合（E0202）
     */
    @Transactional
    public void reserve(ReserveRequestDto request) {
        if (request.lines() == null || request.lines().isEmpty()) {
            throw new BusinessException("E0203");
        }
        for (ReserveLineDto line : request.lines()) {
            if (line.quantity() < 1) {
                throw new BusinessException("E0203");
            }
        }

        for (ReserveLineDto line : request.lines()) {
            StockDto stock = stockDao.findForUpdate(line.productCode())
                    .orElseThrow(() -> BusinessException.notFound("E0201", line.productCode()));
            if (stock.stockQuantity() - stock.reservedQuantity() < line.quantity()) {
                throw BusinessException.conflict("E0202", line.productCode());
            }
            stockDao.updateReserved(StockDao.ACTION_RESERVE, line.productCode(), line.quantity());
            reservationDao.insert(request.orderNo(), line);
        }
    }

    /** 受注番号の引当をすべて解除する。引当中の明細が無ければ何もしない。 */
    @Transactional
    public void release(ReleaseRequestDto request) {
        List<ReserveLineDto> reservations = reservationDao.findActiveByOrderNoForUpdate(request.orderNo());
        for (ReserveLineDto reservation : reservations) {
            stockDao.updateReserved(StockDao.ACTION_RELEASE, reservation.productCode(), reservation.quantity());
        }
        reservationDao.markReleased(request.orderNo());
    }
}
