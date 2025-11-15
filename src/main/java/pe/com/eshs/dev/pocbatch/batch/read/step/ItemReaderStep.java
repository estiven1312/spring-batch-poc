package pe.com.eshs.dev.pocbatch.batch.read.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Service;
import pe.com.eshs.dev.pocbatch.model.Invoice;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ItemReaderStep implements Tasklet {

  private static final String QUERY_READ_ITEMS =
      """
        SELECT
            id,
            codigo_nacional_factura,
            codigo_sistema_interno_factura,
            direccion_empresa,
            codigo_empresa,
            fecha_hora_factura,
            monto_total_factura,
            direccion_factura,
            url_factura,
            tipo_archivo_factura,
            estado_procesamiento,
            mensaje_error
        FROM
            invoice
        WHERE
            estado_procesamiento = 'PENDIENTE'
    """;

  private final JdbcTransactionManager originTransactionManager;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    log.info(">>>>> Step: Reading items from the database");
    try (var ds = originTransactionManager.getDataSource().getConnection();
        PreparedStatement ps = ds.prepareStatement(QUERY_READ_ITEMS)) {
      List<Invoice> items = new ArrayList<>();
      var rs = ps.executeQuery();
      while (rs.next()) {
        log.info("Read item with ID: {}", rs.getObject("id"));
        log.info("National Invoice Code: {}", rs.getString("codigo_nacional_factura"));
        log.info(
            "Internal System Invoice Code: {}", rs.getString("codigo_sistema_interno_factura"));
        log.info("Company Code: {}", rs.getString("codigo_empresa"));
        log.info("Company Address: {}", rs.getString("direccion_empresa"));
        log.info("Invoice DateTime: {}", rs.getObject("fecha_hora_factura"));
        log.info("Total Invoice Amount: {}", rs.getObject("monto_total_factura"));
        log.info("Invoice Address: {}", rs.getString("direccion_factura"));
        log.info("Invoice URL: {}", rs.getString("url_factura"));
        log.info("File Type: {}", rs.getString("tipo_archivo_factura"));
        log.info("Processing Status: {}", rs.getString("estado_procesamiento"));
        log.info("Error Message: {}", rs.getString("mensaje_error"));
        Invoice item = new Invoice();
        item.setId(rs.getObject("id", java.util.UUID.class));
        item.setNationalInvoiceCode(rs.getString("codigo_nacional_factura"));
        item.setInternalSystemInvoiceCode(rs.getString("codigo_sistema_interno_factura"));
        item.setCompanyCode(rs.getString("codigo_empresa"));
        item.setCompanyAddress(rs.getString("direccion_empresa"));
        item.setInvoiceDateTime(rs.getObject("fecha_hora_factura", java.time.LocalDateTime.class));
        item.setTotalInvoiceAmount(rs.getObject("monto_total_factura", java.math.BigDecimal.class));
        item.setInvoiceAddress(rs.getString("direccion_factura"));
        item.setInvoiceUrl(rs.getString("url_factura"));
        item.setFileType(rs.getString("tipo_archivo_factura"));
        item.setProcessingStatus(rs.getString("estado_procesamiento"));
        item.setErrorMessage(rs.getString("mensaje_error"));
        items.add(item);
      }
      chunkContext
          .getStepContext()
          .getStepExecution()
          .getJobExecution()
          .getExecutionContext()
          .put("invoicesToProcess", items);
    } catch (Exception e) {
      log.error("Error reading items from the database", e);
      throw e;
    }

    return RepeatStatus.FINISHED;
  }
}
