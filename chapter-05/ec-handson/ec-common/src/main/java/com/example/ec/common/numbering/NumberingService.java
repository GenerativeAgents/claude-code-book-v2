package com.example.ec.common.numbering;

import com.example.ec.common.sql.SqlLoader;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 採番テーブルM_NUMBERINGから番号を払い出す。
 * 行ロックを取るため、呼び出し側のトランザクションの中で使う。
 */
public class NumberingService {

    private final JdbcClient jdbcClient;
    private final SqlLoader sqlLoader;

    public NumberingService(JdbcClient jdbcClient, SqlLoader sqlLoader) {
        this.jdbcClient = jdbcClient;
        this.sqlLoader = sqlLoader;
    }

    /**
     * 次の番号を払い出す。例：PREFIX「OR」、DIGITS 8、CURRENT_VALUE 3のとき「OR00000004」。
     *
     * @throws IllegalStateException 採番キーが無い場合、番号が桁数を超える場合
     * @throws org.springframework.transaction.IllegalTransactionStateException トランザクションの外で呼んだ場合
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public String next(String numberingKey) {
        Numbering numbering = jdbcClient.sql(sqlLoader.load("SEL_NUMBERING_FOR_UPDATE"))
                .param(numberingKey)
                .query((rs, rowNum) -> new Numbering(
                        rs.getString("PREFIX"), rs.getLong("CURRENT_VALUE"), rs.getInt("DIGITS")))
                .optional()
                .orElseThrow(() -> new IllegalStateException("Numbering key not found: " + numberingKey));

        long nextValue = numbering.currentValue() + 1;
        String number = Long.toString(nextValue);
        if (number.length() > numbering.digits()) {
            throw new IllegalStateException("Numbering exhausted: " + numberingKey);
        }

        jdbcClient.sql(sqlLoader.load("UPD_NUMBERING"))
                .param(nextValue)
                .param(numberingKey)
                .update();
        return numbering.prefix() + "0".repeat(numbering.digits() - number.length()) + number;
    }

    private record Numbering(String prefix, long currentValue, int digits) {
    }
}
