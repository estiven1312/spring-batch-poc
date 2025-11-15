package pe.com.eshs.dev.pocbatch.batch.read.step;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.JdbcTransactionManager;
import pe.com.eshs.dev.pocbatch.model.Invoice;

@Slf4j
public class ItemWriterStep implements Tasklet {

  private static final String INSERT_INVOICE_QUERY =
      """
        INSERT INTO invoice (
            id,
            national_invoice_code,
            internal_system_invoice_code,
            company_code,
            processing_date,
            invoice_url,
            invoice_datetime
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
    """;
  private final JdbcTransactionManager originTransactionManager;
  private final JdbcTransactionManager targetTransactionManager;

  public ItemWriterStep(
      @Qualifier("originTransactionManager") JdbcTransactionManager originTransactionManager,
      @Qualifier("targetTransactionManager") JdbcTransactionManager targetTransactionManager) {
    this.originTransactionManager = originTransactionManager;
    this.targetTransactionManager = targetTransactionManager;
  }

  private static final String UPDATE_STATUS_QUERY =
      """
        UPDATE invoice
        SET
            estado_procesamiento = ?,
            mensaje_error = ?
        WHERE
            id = ?
    """;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    log.info(">>>>> Step: Reading items from the database");
    try (var dsOrigin = originTransactionManager.getDataSource().getConnection();
        var dsTarget = targetTransactionManager.getDataSource().getConnection(); ) {
      List<Invoice> invoicesToProcess =
          (List<Invoice>)
              chunkContext
                  .getStepContext()
                  .getStepExecution()
                  .getJobExecution()
                  .getExecutionContext()
                  .get("invoicesToProcess");
      List<Invoice> invoices = new ArrayList<>();
      List<Invoice> failedInvoices = new ArrayList<>();
      log.info("Processing {}", invoicesToProcess);
      for (Invoice invoice : invoicesToProcess) {

        try (PreparedStatement psInsert = dsTarget.prepareStatement(INSERT_INVOICE_QUERY)) {
          psInsert.setObject(1, invoice.getId());
          psInsert.setString(2, invoice.getNationalInvoiceCode());
          psInsert.setString(3, invoice.getInternalSystemInvoiceCode());
          psInsert.setString(4, invoice.getCompanyCode());
          psInsert.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
          psInsert.setString(6, invoice.getInvoiceUrl());
          psInsert.setTimestamp(7, Timestamp.valueOf(invoice.getInvoiceDateTime()));
          psInsert.executeUpdate();
          log.info("Inserted invoice with id {}", invoice.getId());
          invoices.add(invoice);
          try{
            try (PreparedStatement psUpdate = dsOrigin.prepareStatement(UPDATE_STATUS_QUERY)) {
              psUpdate.setString(1, "PROCESADO");
              psUpdate.setString(2, null);
              psUpdate.setString(3, invoice.getId().toString());
              psUpdate.executeUpdate();
            }
          } catch (Exception e) {
            log.error("Error updating status for invoice id {}: {}", invoice.getId(), e.getMessage(), e);
          }
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          try (PreparedStatement psUpdate = dsOrigin.prepareStatement(UPDATE_STATUS_QUERY)) {
            psUpdate.setString(1, "FALLIDO");
            psUpdate.setString(
                2,
                e.getLocalizedMessage()
                    .substring(0, Math.min(e.getLocalizedMessage().length(), 250)));
            psUpdate.setString(3, invoice.getId().toString());
            psUpdate.executeUpdate();
          }
          failedInvoices.add(invoice);
        }
      }
      chunkContext
          .getStepContext()
          .getStepExecution()
          .getJobExecution()
          .getExecutionContext()
          .put("failedProcessed", failedInvoices);
      log.info("Successfully inserted {} invoices", invoices.size());
    } catch (Exception e) {
      log.error("Error reading items from the database", e);
      throw e;
    }

    return RepeatStatus.FINISHED;
  }
}
