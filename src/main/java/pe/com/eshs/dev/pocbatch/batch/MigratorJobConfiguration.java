package pe.com.eshs.dev.pocbatch.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;
import pe.com.eshs.dev.pocbatch.batch.read.step.ItemFailedSaveStep;
import pe.com.eshs.dev.pocbatch.batch.read.step.ItemReaderStep;
import pe.com.eshs.dev.pocbatch.batch.read.step.ItemWriterStep;

@Configuration
@RequiredArgsConstructor
public class MigratorJobConfiguration {

  private final JobRepository jobRepository;

  @Bean
  public Step itemReaderStep(
      @Qualifier("originTransactionManager") JdbcTransactionManager originTransactionManager) {
    return new StepBuilder("itemReaderStep", jobRepository)
        .tasklet(readItemStep(originTransactionManager), originTransactionManager)
        .build();
  }

  @Bean
  public Step itemWriterStep(
      @Qualifier("originTransactionManager") JdbcTransactionManager originTransactionManager,
      @Qualifier("targetTransactionManager") JdbcTransactionManager targetTransactionManager) {
    return new StepBuilder("itemWriterStep", jobRepository)
        .tasklet(
            writeItemStep(originTransactionManager, targetTransactionManager),
            originTransactionManager)
        .build();
  }

  @Bean
  public Step itemFailedSave(
      @Qualifier("originTransactionManager") JdbcTransactionManager originTransactionManager) {
    return new StepBuilder("itemFailedSaveStep", jobRepository)
        .tasklet(itemFailedSaveStep(), originTransactionManager)
        .build();
  }

  @Bean
  public Job migratorJob(
      @Qualifier("originTransactionManager") JdbcTransactionManager originTransactionManager,
      @Qualifier("targetTransactionManager") JdbcTransactionManager targetTransactionManager) {
    return new JobBuilder("migratorJob", jobRepository)
        .start(itemReaderStep(originTransactionManager))
        .next(itemWriterStep(originTransactionManager, targetTransactionManager))
        .next(itemFailedSave(originTransactionManager))
        .build();
  }

  @Bean
  @JobScope
  public ItemReaderStep readItemStep(
      @Qualifier("originTransactionManager") JdbcTransactionManager originTransactionManager) {
    return new ItemReaderStep(originTransactionManager);
  }

  @Bean
  @JobScope
  public ItemWriterStep writeItemStep(
      @Qualifier("originTransactionManager") JdbcTransactionManager originTransactionManager,
      @Qualifier("targetTransactionManager") JdbcTransactionManager targetTransactionManager) {
    return new ItemWriterStep(originTransactionManager, targetTransactionManager);
  }

  @Bean
  @JobScope
  public ItemFailedSaveStep itemFailedSaveStep() {
    return new ItemFailedSaveStep();
  }
}
