package com.billdesk.banks.icici.impl;

import com.billdesk.banks.AbstractTest;
import com.billdesk.banks.config.ProxyConfig;
import com.billdesk.banks.icici.config.ICICIConfig;
import com.billdesk.banks.icici.model.ICICIEligibilityRequest;
import com.billdesk.banks.icici.model.ICICIEligibilityResponse;
import com.billdesk.core.exception.FlexException;
import com.billdesk.core.model.EligibilityRequest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ICICIClientTest extends AbstractTest {

  @Mock
  private ProxyConfig proxyConfig;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.Builder builder;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private Mono<ICICIEligibilityResponse> iciciEligibilityResponseMono;
  @InjectMocks
  private ICICIClient iciciClient;
  @Mock
  private ICICIConfig iciciConfig;

  @Test
  @SneakyThrows
  void testSuccess() {

    when(iciciConfig.getApikey()).thenReturn(RandomStringUtils.randomAlphanumeric(32));
    when(proxyConfig.getHost()).thenReturn("");
    when(proxyConfig.getPort()).thenReturn(null);
    when(iciciConfig.getKeyStorePath()).thenReturn("");
    when(iciciConfig.getKeyStorePass()).thenReturn("");
    final MockedStatic<WebClient> webClientMockedStatic = mockStatic(WebClient.class);
    webClientMockedStatic.when(WebClient::builder).thenReturn(builder);
    when(builder.build()).thenReturn(webClient);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any(ICICIEligibilityRequest.class))).thenReturn(
      requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(eq(ICICIEligibilityResponse.class))).thenReturn(
      iciciEligibilityResponseMono);
    final EligibilityRequest eligibilityRequest = getEligibilityRequest();
    final ICICIEligibilityResponse iciciEligibilityResponseMock =
      getICICIEligibilityResponse(eligibilityRequest);
    when(iciciEligibilityResponseMono.block()).thenReturn(iciciEligibilityResponseMock);
    final ICICIEligibilityResponse iciciEligibilityResponse = iciciClient.send(
      "http://localhost:8000/api/v1/cardless-emi/EligibilityBillDesk",
      getICICIEligibilityRequest(eligibilityRequest),
      ICICIEligibilityResponse.class);
    Assertions.assertNotNull(iciciEligibilityResponse);
    Assertions.assertEquals(iciciEligibilityResponseMock.getErrorCode(),
                            iciciEligibilityResponse.getErrorCode());
    Assertions.assertEquals(iciciEligibilityResponseMock.getErrorMessage(),
                            iciciEligibilityResponse.getErrorMessage());
    Assertions.assertEquals(iciciEligibilityResponseMock.getStatus(),
                            iciciEligibilityResponse.getStatus());
    Assertions.assertEquals(iciciEligibilityResponseMock.getData().getTransactionId(),
                            iciciEligibilityResponse.getData().getTransactionId());
    webClientMockedStatic.close();
  }

  @Test
  @SneakyThrows
  void testClientSideError() {

    when(iciciConfig.getApikey()).thenReturn(RandomStringUtils.randomAlphanumeric(32));
    final MockedStatic<WebClient> webClientMockedStatic = mockStatic(WebClient.class);
    webClientMockedStatic.when(WebClient::builder).thenReturn(builder);
    when(builder.build()).thenReturn(webClient);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodyUriSpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any(ICICIEligibilityRequest.class))).thenReturn(
      requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenThrow(new RuntimeException());
    Assertions.assertThrows(FlexException.class,
                            () -> iciciClient.send(
                              "http://localhost:8000/api/v1/cardless-emi/EligibilityBillDesk",
                              getICICIEligibilityRequest(getEligibilityRequest()),
                              ICICIEligibilityResponse.class));
    webClientMockedStatic.close();
  }

  @Test
  void testGetHeadersMap() {

    final Properties headers = new Properties();
    when(iciciConfig.getApikey()).thenReturn(RandomStringUtils.randomAlphanumeric(32));
    final Map<String, String> headersMap =
      ReflectionTestUtils.invokeMethod(iciciClient, "getHeadersMap", headers);
    Assertions.assertNotNull(headersMap);
  }
}
