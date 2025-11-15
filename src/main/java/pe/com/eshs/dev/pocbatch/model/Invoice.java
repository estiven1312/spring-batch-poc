package pe.com.eshs.dev.pocbatch.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Invoice implements Serializable {
  private UUID id;
  private String nationalInvoiceCode;
  private String internalSystemInvoiceCode;
  private String companyAddress;
  private String companyCode;
  private LocalDateTime invoiceDateTime;
  private BigDecimal totalInvoiceAmount;
  private String invoiceAddress;
  private String invoiceUrl;
  private String fileType;
  private String processingStatus;
  private String errorMessage;
}
