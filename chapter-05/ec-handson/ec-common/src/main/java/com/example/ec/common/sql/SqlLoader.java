package com.example.ec.common.sql;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.io.ClassPathResource;

/**
 * SQL-IDに対応するSQLファイル（クラスパスのsql/&lt;SQL-ID&gt;.sql）を読み込む。
 * 先頭のコメント行は残し、末尾の空白と;だけを除く。
 */
public class SqlLoader {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    /**
     * SQLを読み込む。一度読んだSQLはキャッシュする。
     *
     * @throws IllegalStateException SQLファイルが無い場合
     */
    public String load(String sqlId) {
        return cache.computeIfAbsent(sqlId, this::read);
    }

    private String read(String sqlId) {
        ClassPathResource resource = new ClassPathResource("sql/" + sqlId + ".sql");
        if (!resource.exists()) {
            throw new IllegalStateException("SQL not found: " + sqlId);
        }
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8).replaceFirst("[\\s;]+$", "");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
