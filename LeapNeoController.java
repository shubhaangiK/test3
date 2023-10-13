package com.billdesk.banks.controllers;

import com.billdesk.banks.BankApplicationFactory;
import com.billdesk.banks.support.ValidationService;
import com.billdesk.core.enums.API;
import com.billdesk.core.model.BookLoanRequest;
import com.billdesk.core.model.BookLoanResponse;
import com.billdesk.core.model.EligibilityRequest;
import com.billdesk.core.model.EligibilityResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/v1/leapneo")
@Log4j2
public class LeapNeoController {

  @Autowired
  private BankApplicationFactory bankApplicationFactory;
  @Autowired
  private ValidationService validationService;

  @PostMapping(
    value = "/book-loan",
    produces = { MediaType.APPLICATION_JSON_VALUE },
    consumes = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<BookLoanResponse> bookLoan(@Valid @RequestBody BookLoanRequest bookLoanRequest,
                                                   final BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      validationService.verifyBindingResultError(bindingResult);
    }
    log.info("book-loan request received : {}", bookLoanRequest);
    return ResponseEntity.ok()
                         .body(bankApplicationFactory.getBLService(bookLoanRequest.getBankId())
                                                     .bookLoan(bookLoanRequest));
  }

  @PostMapping(
    value = "/check-eligibility",
    produces = { MediaType.APPLICATION_JSON_VALUE },
    consumes = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<EligibilityResponse> checkEligibility(@Valid @RequestBody EligibilityRequest eligibilityRequest,
                                                              final BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      validationService.verifyBindingResultError(bindingResult);
    }
    log.info("Check eligibility request received : {}", eligibilityRequest);
    return ResponseEntity.ok()
                         .body(bankApplicationFactory.getEligibilityService(eligibilityRequest.getBankId())
                                                     .checkEligibility(eligibilityRequest));
  }
}
