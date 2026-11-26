package com.example.ec.common.batch;

import java.io.PrintStream;
import java.util.List;

/**
 * BatchLauncherから起動するバッチ処理。
 * 実装クラスは、ジョブ名をBean名にしてBean登録する（例：{@code @Component("stock-low")}）。
 */
@FunctionalInterface
public interface BatchJob {

    /**
     * バッチを実行し、終了コードを返す。
     *
     * @param args ジョブへの引数（オプションでないコマンドライン引数）
     * @param out  結果の出力先
     */
    int run(List<String> args, PrintStream out) throws Exception;
}
