package com.billdesk.banks.sbi.impl;

import com.billdesk.banks.sbi.config.SBIConfig;
import com.billdesk.banks.sbi.eligibility.CardDetailsBlock;
import com.billdesk.banks.sbi.eligibility.CustomerBlockApiResponse;
import com.billdesk.banks.sbi.eligibility.CustomerBlockRequest;
import com.billdesk.banks.sbi.eligibility.CustomerBlockResponse;
import com.billdesk.banks.sbi.eligibility.ObjectFactory;
import com.billdesk.banks.sbi.translator.BDToSBIBookLoanRequestTranslator;
import com.billdesk.banks.service.BookLoanResponseDao;
import com.billdesk.banks.support.FlexExceptionFactory;
import com.billdesk.core.enums.API;
import com.billdesk.core.enums.FlexErrorCode;
import com.billdesk.core.exception.FlexException;
import com.billdesk.core.model.BookLoanRequest;
import com.billdesk.core.model.BookLoanResponse;
import com.billdesk.core.service.BookLoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import java.util.List;

import static com.billdesk.banks.sbi.constants.ErrorCodeConstants.BANK_SUCCESS_CODE;

@Log4j2
@Service
public class SBIBookLoanService implements BookLoanService {

  private static final ObjectFactory objectFactory = new ObjectFactory();
  @Autowired
  private SBIConfig sbiConfig;
  @Autowired
  private BDToSBIBookLoanRequestTranslator translator;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private SBIEncryptionService sbiEncryptionService;
  @Autowired
  private SBISoapClient sbiSoapClient;
  @Autowired
  private BookLoanResponseDao persister;
  @Autowired
  private SBIBookLoanResponseTransformService transformService;

  @Override
  public BookLoanResponse bookLoan(BookLoanRequest bookLoanRequest) {

    try {
      log.info("Book loan request - URL [{}]", sbiConfig.getBookLoanUrl());
      final CustomerBlockRequest customerBlockRequest = translator.translate(bookLoanRequest);
      log.info("Sending Book loan request to SBI bank {}", customerBlockRequest);
      CardDetailsBlock cardDetailsBlock =
        customerBlockRequest.getCustomerBlockApiRequest().getCardDetails();
      final String encryptedCardNo =
        sbiEncryptionService.encryptRequest(cardDetailsBlock.getCardNumber());
      final String encryptedOtpValue =
        sbiEncryptionService.encryptRequest(cardDetailsBlock.getOtpValue());
      cardDetailsBlock.setCardNumber(encryptedCardNo);
      cardDetailsBlock.setOtpValue(encryptedOtpValue);
      customerBlockRequest.getCustomerBlockApiRequest().setCardDetails(cardDetailsBlock);
      log.debug("Encrypted SBI book-loan request sending to the  bank {}", customerBlockRequest);
      final JAXBElement<CustomerBlockRequest> bankRequest =
        objectFactory.createCustomerBlockRequest(customerBlockRequest);
      final JAXBElement<CustomerBlockResponse> response =
        sbiSoapClient.send(bankRequest, JAXBElement.class, sbiConfig.getBookLoanUrl());
      final CustomerBlockApiResponse customerBlockApiResponse =
        response.getValue().getCustomerBlockApiResponse();
      log.info("Book-loan sbi response received from the bank {}", response.getValue());
      final String errorCode =
        String.valueOf(customerBlockApiResponse.getResponse().getResponseCode());
      // Considering 0000 as success code mentioned in doc, if code changes needs to change code here as well
      if (!errorCode.equals(BANK_SUCCESS_CODE)) {
        throw FlexExceptionFactory.get(errorCode, API.ORDER_CONFIRMATION);
      }
      persister.saveBookLoanDetails(customerBlockApiResponse, bookLoanRequest);
      return transformService.transform(customerBlockApiResponse, bookLoanRequest);
    } catch (final FlexException ex) {
      log.error(ex.getStackTrace());
      log.error("Error occurred during book-loan process {}", ex.getMessage());
      throw ex;
    } catch (final Exception ex) {
      log.error(ex.getStackTrace());
      log.error("Error occurred at runtime during book-loan process {}", ex.getMessage());
      throw new FlexException(FlexErrorCode.INTERNAL_SERVER_ERROR, API.ORDER_CONFIRMATION);
    }
  }

  @Override
  public List<String> getServiceType() {

    return sbiConfig.getBankIds();
  }
}
