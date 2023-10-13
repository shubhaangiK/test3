package com.billdesk.banks.integrationtest;

import com.billdesk.banks.AbstractIT;
import com.billdesk.core.enums.FlexErrorCode;
import com.billdesk.core.enums.GenericErrorCode;
import com.billdesk.core.enums.Result;
import com.billdesk.core.model.BookLoanRequest;
import com.billdesk.core.model.EligibilityRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.billdesk.banks.constants.AppTestConstants.AXIS_ENCRYPTED_FAILURE_REQUEST;
import static com.billdesk.banks.constants.AppTestConstants.BOOK_LOAN_URL;
import static com.billdesk.banks.constants.AppTestConstants.CHECK_ELIGIBILITY_URL;
import static com.billdesk.banks.constants.AppTestConstants.INTERNAL_SERVER_ERROR_STATUS;
import static com.billdesk.banks.constants.AppTestConstants.PG_REF_NO;
import static com.billdesk.banks.constants.AppTestConstants.SBI_BANK_ID;
import static com.billdesk.banks.constants.AppTestConstants.SUCCESS;
import static com.billdesk.banks.constants.AppTestConstants.SUCCESS_TRX_CODE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class LeapNeoControllerIT extends AbstractIT {

  private static final String HDFC_BANK_ID = "HL5";
  private static final String ICICI_BANK_ID = "ICE";
  private static final String AXIS_BANK_ID = "ASE";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @SneakyThrows
  void testEligibilityInvalidBankId() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber(null);
    eligibilityRequest.setBankId("HS");
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityInvalidMerId() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber(null);
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    eligibilityRequest.setMercId(RandomStringUtils.randomAlphanumeric(15));
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityInvalidAmount() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber(null);
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    eligibilityRequest.setAmount(00.0011);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityInvalidPGRefNo() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setPgRefNo(RandomStringUtils.randomAlphanumeric(12));
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.PG_REF_NO_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.PG_REF_NO_INVALID.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(GenericErrorCode.PG_REF_NO_INVALID.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityInvalidTenure() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTenure(300);
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.TENURE_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.TENURE_INVALID.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(GenericErrorCode.TENURE_INVALID.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityInvalidItemCode() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setItemCode(RandomStringUtils.randomAlphanumeric(25));
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.ITEM_CODE_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.ITEM_CODE_INVALID.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(GenericErrorCode.ITEM_CODE_INVALID.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityMobileNoBlank() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber(null);
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.MOBILE_NUMBER_BLANK.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.MOBILE_NUMBER_BLANK.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(GenericErrorCode.MOBILE_NUMBER_BLANK.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityMobileNoInvalid() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("900000000022");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.MOBILE_NUMBER_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.MOBILE_NUMBER_INVALID.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(GenericErrorCode.MOBILE_NUMBER_INVALID.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityPgRefNoNull() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setPgRefNo(null);
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.PG_REF_NO_BLANK.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.PG_REF_NO_BLANK.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(GenericErrorCode.PG_REF_NO_BLANK.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityStoreIdInvalid() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    eligibilityRequest.setStoreId(RandomStringUtils.randomAlphanumeric(25));
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.STORE_ID_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.STORE_ID_INVALID.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(GenericErrorCode.STORE_ID_INVALID.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilityStoreNameInvalid() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    eligibilityRequest.setStoreName(RandomStringUtils.randomAlphanumeric(300));
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.STORE_NAME_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.STORE_NAME_INVALID.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(GenericErrorCode.STORE_NAME_INVALID.getStatus()));
  }

  @Test
  @SneakyThrows
  void testEligibilitySuccess() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("100000000");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isOk())
           .andExpect(MockMvcResultMatchers.jsonPath("$.bankid")
                                           .value(eligibilityRequest.getBankId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.merc_id")
                                           .value(eligibilityRequest.getMercId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.pg_ref_no")
                                           .value(eligibilityRequest.getPgRefNo()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.amount")
                                           .value(eligibilityRequest.getAmount()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code").value(SUCCESS_TRX_CODE))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Result.SUCCESS.toString()));
  }

  @Test
  @SneakyThrows
  void testEligibilitySuccessUsingPAN() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setPanNumber("CCAPS9542M");
    eligibilityRequest.setTransactionId("100000001");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isOk())
           .andExpect(MockMvcResultMatchers.jsonPath("$.bankid")
                                           .value(eligibilityRequest.getBankId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.merc_id")
                                           .value(eligibilityRequest.getMercId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.pg_ref_no")
                                           .value(eligibilityRequest.getPgRefNo()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.amount")
                                           .value(eligibilityRequest.getAmount()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code").value(SUCCESS_TRX_CODE))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Result.SUCCESS.toString()));
  }

  @Test
  @SneakyThrows
  void identifierTypeCannotBeBlank() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A010");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.IDENTIFIER_TYPE_BLANK.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.IDENTIFIER_TYPE_BLANK.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.IDENTIFIER_TYPE_BLANK.getStatus()));
  }

  @Test
  @SneakyThrows
  void identifierValueCannotBeBlank() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A011");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INDENTIFIER_VALUE_BLANK.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INDENTIFIER_VALUE_BLANK.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.INDENTIFIER_VALUE_BLANK.getStatus()));
  }

  @Test
  @SneakyThrows
  void invalidMCCCode() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A031");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_MCC_CODE.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_MCC_CODE.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.INVALID_MCC_CODE.getStatus()));
  }

  @Test
  @SneakyThrows
  void quantityCannotBeBlank() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("100000A048");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.BLANK_QUANTITY.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.BLANK_QUANTITY.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.BLANK_QUANTITY.getStatus()));
  }

  @Test
  @SneakyThrows
  void invalidQuantity() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("100000A049");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_QUANTITY.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_QUANTITY.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.INVALID_QUANTITY.getStatus()));
  }

  @Test
  @SneakyThrows
  void channelTypeCanNotBeBlank() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A012");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.CHANNEL_TYPE_BLANK.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.CHANNEL_TYPE_BLANK.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.CHANNEL_TYPE_BLANK.getStatus()));
  }

  @Test
  @SneakyThrows
  void channelNameCanNotBeBlank() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A013");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.CHANNEL_NAME_BLANK.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.CHANNEL_NAME_BLANK.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.CHANNEL_NAME_BLANK.getStatus()));
  }

  @Test
  @SneakyThrows
  void invalidCredentials() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A001");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_CREDENTIALS.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_CREDENTIALS.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.INVALID_CREDENTIALS.getStatus()));
  }

  @Test
  @SneakyThrows
  void merchantUsernameCannotBeBlank() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A002");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.MERCHANT_USERNAME_BLANK.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.MERCHANT_USERNAME_BLANK.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.MERCHANT_USERNAME_BLANK.getStatus()));
  }

  @Test
  @SneakyThrows
  void merchantPasswordCannotBeBlank() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A003");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.MERCHANT_PASSWORD_BLANK.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.MERCHANT_PASSWORD_BLANK.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.MERCHANT_PASSWORD_BLANK.getStatus()));
  }

  @Test
  @SneakyThrows
  void InvalidMerchantUsernameCharCount() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A004");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.MERCHANT_NAME_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.MERCHANT_NAME_INVALID.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.MERCHANT_NAME_INVALID.getStatus()));
  }

  @Test
  @SneakyThrows
  void InvalidMerchantPasswordCharCount() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000A005");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.MERCHANT_PASSWORD_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.MERCHANT_PASSWORD_INVALID.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.MERCHANT_PASSWORD_INVALID.getStatus()));
  }

  @Test
  @SneakyThrows
  void channelTypeShouldBeOnline() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000131");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.CHANNEL_TYPE_ONLINE.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.CHANNEL_TYPE_ONLINE.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.CHANNEL_TYPE_ONLINE.getStatus()));
  }

  @Test
  @SneakyThrows
  void customerUnavailable() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000001");
    eligibilityRequest.getCardless().setCardEnd("9091");
    eligibilityRequest.setTransactionId("100000002");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.CUSTOMER_UNAVAILABLE.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.CUSTOMER_UNAVAILABLE.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.CUSTOMER_UNAVAILABLE.getStatus()));
  }

  @Test
  @SneakyThrows
  void loanAmountLengthInvalid() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000002");
    eligibilityRequest.getCardless().setCardEnd("9092");
    eligibilityRequest.setTransactionId("100000003");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_LOAN_AMOUNT.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_LOAN_AMOUNT.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.INVALID_LOAN_AMOUNT.getStatus()));
  }

  @Test
  @SneakyThrows
  void eligibilityInvalidRequest() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000003");
    eligibilityRequest.getCardless().setCardEnd("9093");
    eligibilityRequest.setTransactionId("100000004");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .accept(MediaType.APPLICATION_JSON)
                                                                        .content(objectMapper.writeValueAsString(
                                                                          eligibilityRequest)))
                                         .andExpect(status().isInternalServerError());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                                 .value(FlexErrorCode.INVALID_REQUEST.getErrorCode()))
                 .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                 .value(FlexErrorCode.INVALID_REQUEST.getErrorDesc()))
                 .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                                 .value(FlexErrorCode.INVALID_REQUEST.getStatus()));
  }

  @Test
  @SneakyThrows
  void unableToProcessRequest() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.getCardless().setMobileNumber("9000000004");
    eligibilityRequest.getCardless().setCardEnd("9094");
    eligibilityRequest.setTransactionId("100000008");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.UNABLE_TO_PROCESS_REQUEST.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.UNABLE_TO_PROCESS_REQUEST.getErrorDesc()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.UNABLE_TO_PROCESS_REQUEST.getStatus()));
  }

  @Test
  @SneakyThrows
  void loanAmountInvalid() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1000000022");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.AMOUNT_NOT_ELIGIBLE_FOR_EMI.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.AMOUNT_NOT_ELIGIBLE_FOR_EMI.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.AMOUNT_NOT_ELIGIBLE_FOR_EMI.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testLoanAmountRangeInvalid() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1000000023");
    eligibilityRequest.setBankId(HDFC_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_LOAN_AMOUNT_RANGE.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_LOAN_AMOUNT_RANGE.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_LOAN_AMOUNT_RANGE.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanSuccess() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("100000007");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isOk())
           .andExpect(MockMvcResultMatchers.jsonPath("$.merc_id")
                                           .value(bookLoanRequest.getMercId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.pg_ref_no")
                                           .value(bookLoanRequest.getPgRefNo()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.emi_details.amount")
                                           .value(bookLoanRequest.getAmount()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.emi_details.interest_rate")
                                           .value(bookLoanRequest.getInterestRate()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.emi_details.tenure")
                                           .value(bookLoanRequest.getTenure()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidBankId() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId("HH");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidMerId() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setMercId(RandomStringUtils.randomAlphanumeric(15));
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidAmount() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setAmount(20.0221);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidOTP() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setOtp(12333333);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidPgRefNo() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setPgRefNo(RandomStringUtils.randomAlphanumeric(10));
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.PG_REF_NO_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(GenericErrorCode.PG_REF_NO_INVALID.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.PG_REF_NO_INVALID.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidTenure() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTenure(255);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.TENURE_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(GenericErrorCode.TENURE_INVALID.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.TENURE_INVALID.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidInterestRate() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setInterestRate(255);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.INTEREST_RATE_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(GenericErrorCode.INTEREST_RATE_INVALID.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.INTEREST_RATE_INVALID.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidItemCode() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setItemCode(RandomStringUtils.randomAlphanumeric(30));
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.ITEM_CODE_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(GenericErrorCode.ITEM_CODE_INVALID.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.ITEM_CODE_INVALID.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidInvoiceNo() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(30));
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoan_InvalidPgRefNo() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setTransactionId("100000005");
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setPgRefNo("test");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(GenericErrorCode.PG_REF_NO_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(GenericErrorCode.PG_REF_NO_INVALID.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(GenericErrorCode.PG_REF_NO_INVALID.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoan_ValidateOtpFailed() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("100000006");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void customerNotEligible() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("100000009");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.CUSTOMER_NOT_ELIGIBLE.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.CUSTOMER_NOT_ELIGIBLE.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.CUSTOMER_NOT_ELIGIBLE.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void validateOtpMismatch() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("100000010");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.OTP_MISMATCH.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.OTP_MISMATCH.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.OTP_MISMATCH.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void validateOtpUserNotEligible() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("100000012");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.OC_CUSTOMER_NOT_ELIGIBLE_FOR_AMOUNT.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.OC_CUSTOMER_NOT_ELIGIBLE_FOR_AMOUNT.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.OC_CUSTOMER_NOT_ELIGIBLE_FOR_AMOUNT.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void validateOtpInvalidOtp() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("100000013");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_OTP_HDFC.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_OTP_HDFC.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_OTP_HDFC.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanBankRefNoUnavailable() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("1000000016");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.BANK_REFERENCE_NO_INVALID.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.BANK_REFERENCE_NO_INVALID.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.BANK_REFERENCE_NO_INVALID.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanMobileNoUnavailable() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(HDFC_BANK_ID);
    bookLoanRequest.setTransactionId("1000000021");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.MOBILE_NO_UNAVAILABLE.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.MOBILE_NO_UNAVAILABLE.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.MOBILE_NO_UNAVAILABLE.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testEligibilitySuccessICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setPgRefNo("123456789999990");
    eligibilityRequest.setTransactionId("1681380765003");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isOk())
           .andExpect(MockMvcResultMatchers.jsonPath("$.bankid")
                                           .value(eligibilityRequest.getBankId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.merc_id")
                                           .value(eligibilityRequest.getMercId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.pg_ref_no")
                                           .value(eligibilityRequest.getPgRefNo()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.amount")
                                           .value(eligibilityRequest.getAmount()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code").value(SUCCESS_TRX_CODE))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(SUCCESS));
  }

  @Test
  @SneakyThrows
  void testEligibilityInvalidTenureICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765004");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_TENURE_MONTHS.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_TENURE_MONTHS.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_TENURE_MONTHS.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testEligibilityCustomerUnAvailableICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765005");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.CUSTOMER_DETAILS_NOT_FOUND.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.CUSTOMER_DETAILS_NOT_FOUND.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.CUSTOMER_DETAILS_NOT_FOUND.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testEligibilityCustomerPanMismatchICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765006");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.CUSTOMER_PAN_MISMATCH.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.CUSTOMER_PAN_MISMATCH.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.CUSTOMER_PAN_MISMATCH.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testDuplicateTransactionICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765007");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.DUPLICATE_TRANSACTION_REQUEST.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.DUPLICATE_TRANSACTION_REQUEST.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.DUPLICATE_TRANSACTION_REQUEST.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testEligibilityCustomerNotEligibleICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765008");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.CUSTOMER_NOT_ELIGIBLE.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.CUSTOMER_NOT_ELIGIBLE.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.CUSTOMER_NOT_ELIGIBLE.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testEligibilityAmountNotEligibleICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765009");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_AMOUNT.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_AMOUNT.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_AMOUNT.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testEligibilityInvalidInvoiceAmountICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765010");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_AMOUNT.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_AMOUNT.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_AMOUNT.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testEligibilityInvalidBankResponseICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765011");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.GENERIC_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.GENERIC_ERROR.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.GENERIC_ERROR.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testTimeoutErrorICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765012");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.TRANSACTION_TIMEOUT.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.TRANSACTION_TIMEOUT.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.TRANSACTION_TIMEOUT.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testMerchantNotFoundICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765013");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.MERCHANT_NOT_EXIST.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.MERCHANT_NOT_EXIST.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.MERCHANT_NOT_EXIST.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testTransactionIdMismatchICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765014");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.TRANSACTION_ID_MISMATCH.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.TRANSACTION_ID_MISMATCH.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.TRANSACTION_ID_MISMATCH.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testInvalidEncryptedRequestICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765015");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_ENCRYPTED_REQUEST.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_ENCRYPTED_REQUEST.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_ENCRYPTED_REQUEST.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testInvalidRequestICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765016");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_JSON.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_JSON.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_JSON.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testFieldFormatInvalidICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765017");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.FORMAT_MISMATCH.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.FORMAT_MISMATCH.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.FORMAT_MISMATCH.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testFieldMissingICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765018");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.MANDATORY_FIELD_MISSING.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.MANDATORY_FIELD_MISSING.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.MANDATORY_FIELD_MISSING.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testConnectionTimeoutErrorICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765019");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.EMI_TRANSACTION_TIMEOUT.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.EMI_TRANSACTION_TIMEOUT.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.EMI_TRANSACTION_TIMEOUT.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testResponseTimeoutErrorICICI() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setTransactionId("1681380765020");
    eligibilityRequest.setBankId(ICICI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.RESPONSE_TIMEOUT.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.RESPONSE_TIMEOUT.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.RESPONSE_TIMEOUT.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanSuccessICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765113");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isOk())
           .andExpect(MockMvcResultMatchers.jsonPath("$.merc_id")
                                           .value(bookLoanRequest.getMercId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.pg_ref_no")
                                           .value(bookLoanRequest.getPgRefNo()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.emi_details.amount")
                                           .value(bookLoanRequest.getAmount()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.emi_details.interest_rate")
                                           .value(bookLoanRequest.getInterestRate()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.emi_details.tenure")
                                           .value(bookLoanRequest.getTenure()));
  }

  @Test
  @SneakyThrows
  void testValidateOTPInvalidOTPICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765115");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_OTP.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_OTP.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_OTP.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testInvalidCallICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765116");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.PAYMENT_PROCESSING_FAILED.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.PAYMENT_PROCESSING_FAILED.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.PAYMENT_PROCESSING_FAILED.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanFailedICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765117");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.BLOCK_OFFER_FAILED.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.BLOCK_OFFER_FAILED.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.BLOCK_OFFER_FAILED.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testValidateOTPDuplicateTransactionICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765118");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.DUPLICATE_TRANSACTION_REQUEST.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.DUPLICATE_TRANSACTION_REQUEST.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.DUPLICATE_TRANSACTION_REQUEST.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testValidateOtpTransactionIdMismatchICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("16813807651181");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.TRANSACTION_ID_MISMATCH.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.TRANSACTION_ID_MISMATCH.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.TRANSACTION_ID_MISMATCH.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanDuplicateTransactionICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765119");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.DUPLICATE_TRANSACTION_REQUEST.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.DUPLICATE_TRANSACTION_REQUEST.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.DUPLICATE_TRANSACTION_REQUEST.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidTenureICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765120");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_TENURE_MONTHS.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_TENURE_MONTHS.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_TENURE_MONTHS.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanAmountNotEligibleICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765121");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INVALID_AMOUNT.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.INVALID_AMOUNT.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.INVALID_AMOUNT.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanTransactionTimeoutICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765122");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.TRANSACTION_TIMEOUT.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.TRANSACTION_TIMEOUT.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.TRANSACTION_TIMEOUT.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanOfferFailedICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765123");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.BLOCK_OFFER_FAILED.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.BLOCK_OFFER_FAILED.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.BLOCK_OFFER_FAILED.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanBankSideErrorICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765124");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.TECHNICAL_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.TECHNICAL_ERROR.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.TECHNICAL_ERROR.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanInvalidRequestICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765125");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.EMPTY_JSON_REQUEST.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.EMPTY_JSON_REQUEST.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.EMPTY_JSON_REQUEST.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanFieldDataMissingICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765126");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.MANDATORY_FIELD_DATA_MISSING.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.MANDATORY_FIELD_DATA_MISSING.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.MANDATORY_FIELD_DATA_MISSING.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testBookLoanFieldLengthExceedsICICI() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(ICICI_BANK_ID);
    bookLoanRequest.setTransactionId("1681380765127");
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.FIELD_LENGTH_EXCEEDED.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_type")
                                           .value(FlexErrorCode.FIELD_LENGTH_EXCEEDED.getErrorType()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                           .value(FlexErrorCode.FIELD_LENGTH_EXCEEDED.getErrorDesc()));
  }

  @Test
  @SneakyThrows
  void testEligibilitySuccessAxisBank() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setPgRefNo("TE5974DCEMI11111");
    eligibilityRequest.setBankId(AXIS_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isOk())
           .andExpect(MockMvcResultMatchers.jsonPath("$.bankid")
                                           .value(eligibilityRequest.getBankId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.merc_id")
                                           .value(eligibilityRequest.getMercId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.pg_ref_no")
                                           .value(eligibilityRequest.getPgRefNo()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.amount")
                                           .value(eligibilityRequest.getAmount()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code").value(SUCCESS_TRX_CODE))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACCEPT"));
  }

  @Test
  @SneakyThrows
  void testEligibilityFailureAxisBank() {

    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    eligibilityRequest.setPgRefNo("TE5974DCEMI22222");
    eligibilityRequest.setBankId(AXIS_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(CHECK_ELIGIBILITY_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                                            eligibilityRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(INTERNAL_SERVER_ERROR_STATUS));
  }

  @Test
  @SneakyThrows
  void testAxisValidateOtpFailure() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setTransactionId(AXIS_ENCRYPTED_FAILURE_REQUEST);
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setPgRefNo("14568908092821");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getStatus()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanSuccess() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo(PG_REF_NO);
    bookLoanRequest.setInvoiceNumber(PG_REF_NO);
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isOk())
           .andExpect(MockMvcResultMatchers.jsonPath("$.merc_id")
                                           .value(bookLoanRequest.getMercId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.pg_ref_no")
                                           .value(bookLoanRequest.getPgRefNo()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.emi_details.amount")
                                           .value(bookLoanRequest.getAmount()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanError005() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo("14568908092831");
    bookLoanRequest.setInvoiceNumber("14568908092831");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.EEE_CUSTOMER_NOT_ELIGIBLE_FOR_EMI.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.EEE_CUSTOMER_NOT_ELIGIBLE_FOR_EMI.getStatus()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanError004() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo("14568908092822");
    bookLoanRequest.setInvoiceNumber("14568908092822");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.EEE_AMOUNT_NOT_ELIGIBLE.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.EEE_AMOUNT_NOT_ELIGIBLE.getStatus()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanError002() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo("14568908092824");
    bookLoanRequest.setInvoiceNumber("14568908092824");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.EEE_OFFER_ALREADY_BLOCKED.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.EEE_OFFER_ALREADY_BLOCKED.getStatus()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanError003() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo("14568908092825");
    bookLoanRequest.setInvoiceNumber("14568908092825");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.EEE_OFFER_EXPIRED.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.EEE_OFFER_EXPIRED.getStatus()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanError012() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo("14568908092826");
    bookLoanRequest.setInvoiceNumber("14568908092826");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.ETE_INVALID_RESPONSE_FROM_BANK.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.ETE_INVALID_RESPONSE_FROM_BANK.getStatus()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanError006() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo("14568908092827");
    bookLoanRequest.setInvoiceNumber("14568908092827");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.ETE_INPUT_VALUE_MISMATCH.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.ETE_INPUT_VALUE_MISMATCH.getStatus()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanError008() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo("14568908092828");
    bookLoanRequest.setInvoiceNumber("14568908092828");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.IRE_INVALID_LOGIC_CODE.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.IRE_INVALID_LOGIC_CODE.getStatus()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanError009() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo("14568908092829");
    bookLoanRequest.setInvoiceNumber("14568908092829");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.IRE_INVALID_MOBILE_NUMBER.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.IRE_INVALID_MOBILE_NUMBER.getStatus()));
  }

  @Test
  @SneakyThrows
  void testAxisBookLoanError010() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setBankId(AXIS_BANK_ID);
    bookLoanRequest.setAmount(20000.00);
    bookLoanRequest.setPgRefNo("14568908092830");
    bookLoanRequest.setInvoiceNumber("14568908092830");
    bookLoanRequest.setOtp(223130);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.IRE_INVALID_UNIQUE_REFERENCE_NUMBER.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(FlexErrorCode.IRE_INVALID_UNIQUE_REFERENCE_NUMBER.getStatus()));
  }

  @Test
  @SneakyThrows
  void testSBIBookLoanSuccess() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setTransactionId("SBIHI73779450230078964995");
    bookLoanRequest.setInvoiceNumber("98692289779869");
    bookLoanRequest.setBankId(SBI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isOk())
           .andExpect(MockMvcResultMatchers.jsonPath("$.merc_id")
                                           .value(bookLoanRequest.getMercId()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.pg_ref_no")
                                           .value(bookLoanRequest.getPgRefNo()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.bank_reference_no")
                                           .value(bookLoanRequest.getBankReferenceNo()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code").value(SUCCESS_TRX_CODE))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
  }

  @Test
  @SneakyThrows
  void testSBIBookLoanFailure() {

    final BookLoanRequest bookLoanRequest = getBookLoanRequest();
    bookLoanRequest.setTransactionId("SBIHI73779450230078964996");
    bookLoanRequest.setInvoiceNumber("98692289779869");
    bookLoanRequest.setBankId(SBI_BANK_ID);
    mockMvc.perform(MockMvcRequestBuilders.post(BOOK_LOAN_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(bookLoanRequest)))
           .andExpect(status().isInternalServerError())
           .andExpect(MockMvcResultMatchers.jsonPath("$.error_code")
                                           .value(FlexErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                           .value(INTERNAL_SERVER_ERROR_STATUS));
  }
}