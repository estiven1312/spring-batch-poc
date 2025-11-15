package pe.com.eshs.dev.pocbatch.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProcessedInvoice {
  private UUID id;
  private String nationalInvoiceCode;
  private String internalSystemInvoiceCode;
  private String companyCode;
  private LocalDateTime invoiceDateTime;
  private LocalDateTime processingDate;
  private String invoiceUrl;
}
