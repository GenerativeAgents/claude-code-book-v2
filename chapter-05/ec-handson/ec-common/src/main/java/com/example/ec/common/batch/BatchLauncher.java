package com.example.ec.common.batch;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.ApplicationContext;

/**
 * オプション{@code --job=<ジョブ名>}で指定したBean名のBatchJobを実行する。
 * オプションでない引数をジョブへ渡し、結果を標準出力へ出す。
 * 終了コードは、ジョブの戻り値、ジョブ名の指定が無いか未登録なら2、ジョブが例外を投げたら1。
 */
public class BatchLauncher implements ApplicationRunner, ExitCodeGenerator {

    private static final Logger log = LoggerFactory.getLogger(BatchLauncher.class);

    private final ApplicationContext context;
    private int exitCode;

    public BatchLauncher(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> jobNames = args.getOptionValues("job");
        if (jobNames == null || jobNames.size() != 1) {
            log.error("Specify one job: --job=<name>");
            exitCode = 2;
            return;
        }

        String jobName = jobNames.get(0);
        BatchJob job;
        try {
            job = context.getBean(jobName, BatchJob.class);
        } catch (NoSuchBeanDefinitionException | BeanNotOfRequiredTypeException e) {
            log.error("Job not found: {}", jobName);
            exitCode = 2;
            return;
        }

        try {
            exitCode = job.run(args.getNonOptionArgs(), System.out);
        } catch (Exception e) {
            log.error("Job failed: {}", jobName, e);
            exitCode = 1;
        } finally {
            System.out.flush();
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
