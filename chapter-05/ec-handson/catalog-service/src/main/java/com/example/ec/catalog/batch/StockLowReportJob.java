package com.example.ec.catalog.batch;

import com.example.ec.catalog.dao.StockReportDao;
import com.example.ec.catalog.dto.StockLowDto;
import com.example.ec.common.batch.BatchJob;
import java.io.PrintStream;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CAT-090 在庫僅少一覧バッチ（ジョブ名stock-low）。
 * 引当可能数がしきい値未満の販売中商品を、商品コード、商品名、引当可能数のTSVで出力する。
 * 引数：しきい値（省略時10）
 */
@Component("stock-low")
public class StockLowReportJob implements BatchJob {

    private static final int DEFAULT_THRESHOLD = 10;

    private final StockReportDao stockReportDao;

    public StockLowReportJob(StockReportDao stockReportDao) {
        this.stockReportDao = stockReportDao;
    }

    @Override
    public int run(List<String> args, PrintStream out) {
        int threshold = args.isEmpty() ? DEFAULT_THRESHOLD : Integer.parseInt(args.get(0));
        for (StockLowDto row : stockReportDao.findLowStock(threshold)) {
            out.println(row.productCode() + "\t" + row.productName() + "\t" + row.availableQuantity());
        }
        return 0;
    }
}
