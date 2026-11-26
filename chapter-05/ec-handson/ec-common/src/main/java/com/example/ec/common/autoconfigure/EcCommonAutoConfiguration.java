package com.example.ec.common.autoconfigure;

import com.example.ec.common.batch.BatchLauncher;
import com.example.ec.common.numbering.NumberingService;
import com.example.ec.common.sql.SqlLoader;
import com.example.ec.common.web.ApiExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;

/** ec-commonの共通部品をBean登録する。 */
@AutoConfiguration
public class EcCommonAutoConfiguration {

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public ApiExceptionHandler apiExceptionHandler(MessageSource messageSource) {
        return new ApiExceptionHandler(messageSource);
    }

    @Bean
    public SqlLoader sqlLoader() {
        return new SqlLoader();
    }

    @Bean
    public NumberingService numberingService(JdbcClient jdbcClient, SqlLoader sqlLoader) {
        return new NumberingService(jdbcClient, sqlLoader);
    }

    @Bean
    @Profile("batch")
    public BatchLauncher batchLauncher(ApplicationContext context) {
        return new BatchLauncher(context);
    }
}
