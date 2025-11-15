package pe.com.eshs.dev.pocbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pe.com.eshs.dev.pocbatch.config.PropertiesDataSourceConfig;

import java.util.UUID;

@SpringBootApplication(
    exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
@RequiredArgsConstructor
@Slf4j
public class PocBatchApplication implements CommandLineRunner {

  @Qualifier("migratorJob")
  private final Job job;

  private final JobLauncher jobLauncher;

  public static void main(String[] args) {
    SpringApplication.run(PocBatchApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    jobLauncher.run(
        job,
        new JobParametersBuilder()
            .addDate("run.date", new java.util.Date())
            .addString("id", UUID.randomUUID().toString())
            .toJobParameters());
  }
}
