package pe.com.eshs.dev.pocbatch.batch.read.config;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import pe.com.eshs.dev.pocbatch.model.Invoice;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class ItemMapper implements RowMapper<Invoice> {
  public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
    Invoice invoice = new Invoice();
    invoice.setId(rs.getObject("id", java.util.UUID.class));
    invoice.setNationalInvoiceCode(rs.getString("codigo_nacional_factura"));
    invoice.setInternalSystemInvoiceCode(rs.getString("codigo_sistema_interno_factura"));
    invoice.setCompanyCode(rs.getString("codigo_empresa"));
    invoice.setCompanyAddress(rs.getString("direccion_empresa"));
    invoice.setInvoiceDateTime(rs.getObject("fecha_hora_factura", LocalDateTime.class));
    invoice.setTotalInvoiceAmount(rs.getObject("processing_date", BigDecimal.class));
    invoice.setInvoiceAddress(rs.getString("direccion_factura"));
    invoice.setInvoiceUrl(rs.getString("invoice_url"));
    invoice.setFileType(rs.getString("tipo_archivo"));
    invoice.setProcessingStatus(rs.getString("estado_procesamiento"));
    invoice.setErrorMessage(rs.getString("mensaje_error"));
    return invoice;
  }
}
