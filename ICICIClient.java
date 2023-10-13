package com.billdesk.banks.icici.impl;

import com.billdesk.banks.config.ProxyConfig;
import com.billdesk.banks.icici.config.ICICIConfig;
import com.billdesk.core.enums.FlexErrorCode;
import com.billdesk.core.exception.FlexException;
import com.billdesk.core.service.Client;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import javax.net.ssl.TrustManagerFactory;
import java.nio.file.Files;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.billdesk.banks.icici.constants.AppConstants.API_KEY;

@Service
@Log4j2
public class ICICIClient implements Client {

  @Autowired
  private ProxyConfig proxyConfig;
  @Autowired
  private ICICIConfig iciciConfig;

  public WebClient getConnection() {
    // If no keystore and proxy details supplied then just return the normal webclient
    if ((StringUtils.isBlank(iciciConfig.getKeyStorePath()) && StringUtils.isBlank(iciciConfig.getKeyStorePass())) && StringUtils.isBlank(
      proxyConfig.getHost())) {
      return WebClient.builder().build();
    }
    try {
      final HttpClient httpClient = HttpClient.create();
      // Add the proxy config
      if (StringUtils.isNotBlank(proxyConfig.getHost()) && proxyConfig.getPort() != null) {
        httpClient.tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                                                                               .host(proxyConfig.getHost()))
                                                          .port(proxyConfig.getPort()));
      }
      // Read client certificate from JKS and set it to the trust store
      final KeyStore trustStore = KeyStore.getInstance("JKS");
      trustStore.load(Files.newInputStream(ResourceUtils.getFile(iciciConfig.getKeyStorePath())
                                                        .toPath()),
                      iciciConfig.getKeyStorePass().toCharArray());
      final TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(trustStore);
      final SslContext sslContext =
        SslContextBuilder.forClient().protocols("TLS").trustManager(trustManagerFactory).build();
      httpClient.secure(spec -> spec.sslContext(sslContext));
      return WebClient.builder()
                      .clientConnector(new ReactorClientHttpConnector(httpClient))
                      .build();
    } catch (final Exception e) {
      log.error("Encountered error while creating SSL Context. Error - {}", e.getMessage());
      throw new FlexException(FlexErrorCode.GENERIC_ERROR);
    }
  }

  @Override
  public <K, T> K send(final String url, final T request, final Class<K> clazz) {

    try {
      final Properties headers = this.getHeaders();
      return getConnection().post()
                            .uri(url)
                            .headers(httpHeaders -> httpHeaders.setAll(this.getHeadersMap(headers)))
                            .bodyValue(request)
                            .retrieve()
                            .bodyToMono(clazz)
                            .block();
    } catch (final Exception e) {
      log.error("Encountered exception while sending the request. Details - {}", e.getMessage());
      throw new FlexException(FlexErrorCode.GENERIC_ERROR);
    }
  }

  @Override
  public Properties getHeaders() {

    final Properties headers = Client.super.getHeaders();
    headers.put(API_KEY, iciciConfig.getApikey());
    return headers;
  }

  private Map<String, String> getHeadersMap(final Properties properties) {

    return properties.entrySet()
                     .stream()
                     .collect(Collectors.toMap(e -> String.valueOf(e.getKey()),
                                               e -> String.valueOf(e.getValue()),
                                               (prev, next) -> next,
                                               HashMap::new));
  }
}
