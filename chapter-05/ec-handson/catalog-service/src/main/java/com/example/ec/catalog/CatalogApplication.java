package com.example.ec.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 商品・在庫サービスの起動クラス。
 * プロファイルbatchで起動した場合は、バッチの終了コードでプロセスを終える。
 */
@SpringBootApplication
public class CatalogApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CatalogApplication.class, args);
        if (context.getEnvironment().matchesProfiles("batch")) {
            System.exit(SpringApplication.exit(context));
        }
    }
}
