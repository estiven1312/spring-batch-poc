package pe.com.eshs.dev.pocbatch.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ErrorProcessingInvoice {
  private UUID id;
  private String nationalInvoiceCode;
  private String internalSystemInvoiceCode;
  private String companyCode;
  private String errorReason;
  private LocalDateTime registrationDate;
}
