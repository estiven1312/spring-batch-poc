package pe.com.eshs.dev.pocbatch.batch.read.step;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.JdbcTransactionManager;
import pe.com.eshs.dev.pocbatch.model.Invoice;

@Slf4j
public class ItemFailedSaveStep implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    log.info(">>>>> Step: Saving failed items to the database");
    List<Invoice> failedInvoices =
        (List<Invoice>)
            chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("failedInvoices");

    if (failedInvoices == null || failedInvoices.isEmpty()) {
      log.info("No failed invoices to save.");
      return RepeatStatus.FINISHED;
    }
    for( Invoice invoice : failedInvoices) {
      log.info("Failed Invoice ID: {}, Error Message: {}", invoice.getId(), invoice.getErrorMessage());
    }

    return RepeatStatus.FINISHED;
  }
}
