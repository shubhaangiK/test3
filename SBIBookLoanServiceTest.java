package com.billdesk.banks.sbi.impl;

import com.billdesk.banks.AbstractTest;
import com.billdesk.banks.sbi.config.SBIConfig;
import com.billdesk.banks.sbi.eligibility.CustomerBlockApiResponse;
import com.billdesk.banks.sbi.eligibility.CustomerBlockRequest;
import com.billdesk.banks.sbi.eligibility.ObjectFactory;
import com.billdesk.banks.sbi.translator.BDToSBIBookLoanRequestTranslator;
import com.billdesk.banks.service.BookLoanResponseDao;
import com.billdesk.core.model.BookLoanRequest;
import com.billdesk.core.model.BookLoanResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.xml.bind.JAXBElement;

import static com.billdesk.banks.constants.AppTestConstants.ENCRYPTED_CARD_NO;
import static com.billdesk.banks.constants.AppTestConstants.ENCRYPTED_OTP_VALUE;
import static com.billdesk.banks.constants.AppTestConstants.SBI_BANK_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class SBIBookLoanServiceTest extends AbstractTest {

  private static final ObjectFactory objectFactory = new ObjectFactory();
  @Mock
  SBIConfig sbiConfig;
  @Mock
  BDToSBIBookLoanRequestTranslator translator;
  @Mock
  SBIEncryptionService sbiEncryptionService;
  @Mock
  SBISoapClient sbiSoapClient;
  @Mock
  SBIBookLoanResponseTransformService transformService;
  @InjectMocks
  SBIBookLoanService sbiBookLoanService;
  @Mock
  private BookLoanResponseDao persister;

  @Test
  @SneakyThrows
  void bookLoanSuccess() {

    BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(SBI_BANK_ID);
    final CustomerBlockRequest customerBlockRequest = getCustomerBlockRequest();
    when(sbiConfig.getMerchantName()).thenReturn(RandomStringUtils.randomAlphanumeric(5));
    Mockito.when(translator.translate(any(BookLoanRequest.class))).thenReturn(customerBlockRequest);
    Mockito.when(sbiEncryptionService.encryptRequest(anyString())).thenReturn(ENCRYPTED_CARD_NO);
    Mockito.when(sbiEncryptionService.encryptRequest(anyString())).thenReturn(ENCRYPTED_OTP_VALUE);
    when(sbiConfig.getBookLoanUrl()).thenReturn(RandomStringUtils.randomAlphanumeric(10));
    when(sbiSoapClient.send(any(JAXBElement.class), eq(JAXBElement.class), anyString())).thenReturn(
      objectFactory.createCustomerBlockResponse(getCustomerBlockResponse(customerBlockRequest)));
    when(transformService.transform(any(CustomerBlockApiResponse.class),
                                    any(BookLoanRequest.class))).thenReturn(getBookLoanResponse(
      bookLoanRequest));
    BookLoanResponse bookLoanResponse = sbiBookLoanService.bookLoan(getBookLoanRequest());
    Assertions.assertNotNull(bookLoanResponse);
    Assertions.assertEquals(bookLoanRequest.getMercId(), bookLoanResponse.getMercId());
    Assertions.assertEquals(bookLoanRequest.getPgRefNo(), bookLoanResponse.getPgRefNo());
  }

  @Test()
  @SneakyThrows
  void testRuntimeError() {

    BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(SBI_BANK_ID);
    final CustomerBlockRequest customerBlockRequest = getCustomerBlockRequest();
    Mockito.when(translator.translate(any(BookLoanRequest.class))).thenReturn(customerBlockRequest);
    Mockito.when(sbiEncryptionService.encryptRequest(anyString())).thenReturn(ENCRYPTED_CARD_NO);
    Mockito.when(sbiEncryptionService.encryptRequest(anyString())).thenReturn(ENCRYPTED_OTP_VALUE);
    when(sbiConfig.getBookLoanUrl()).thenReturn(RandomStringUtils.randomAlphanumeric(10));
    when(sbiSoapClient.send(any(JAXBElement.class), eq(JAXBElement.class), anyString())).thenThrow(
      new RuntimeException());
    Assertions.assertThrows(RuntimeException.class,
                            () -> sbiBookLoanService.bookLoan(getBookLoanRequest()));
  }
}
