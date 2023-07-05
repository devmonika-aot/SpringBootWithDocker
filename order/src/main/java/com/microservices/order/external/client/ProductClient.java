package com.microservices.order.external.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.order.exception.OrderCustomException;
import com.microservices.order.external.response.ProductResponse;
import com.microservices.order.model.ErrorResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProductClient {

  @Autowired RestTemplate restTemplate;

  private ObjectMapper mapper = new ObjectMapper();
  ResponseEntity<ProductResponse> response = null;
  ErrorResponse errorResponse = null;

  private static final String url = "http://localhost:8080/product/reduceQuantity";

  @CircuitBreaker(name = "serviceA", fallbackMethod = "getAPIFallBack")
  // @Retry(name = "serviceA")//, fallbackMethod = "retryFallBack"
  // @RateLimiter(name = "serviceA", fallbackMethod = "getAPIFallBack")
  // Fallback method call is optional
  public ResponseEntity<?> reduceProduct(long id, long quantity) {

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<?> entity = new HttpEntity<>(headers);
    String urlTemplate =
        UriComponentsBuilder.fromHttpUrl(url)
            .path("/{id}") // .path("/{path1}/{path2}")
            .queryParam("quantity", quantity)
            .buildAndExpand(id) //  .buildAndExpand(path1, path2);
            .encode()
            .toUriString();
    try {
      System.out.println("Retrying ");
      response = restTemplate.exchange(urlTemplate, HttpMethod.PUT, entity, ProductResponse.class);
    } catch (HttpClientErrorException e) {
      throw e;
    } catch (HttpStatusCodeException e) {
      throw e;
    } catch (Exception e) {
      // all the other error like
      // 1. RestClientException
      // 2.HttpServerErrorException etc will go to fallback as this error indicates something
      // wrong with server
      throw e;
    }

    return response;
  }

  public ResponseEntity<?> getAPIFallBack(HttpClientErrorException e) {
    System.out.println("HttpClientErrorException" + e);
    String body = e.getResponseBodyAsString();
    try {
      mapper.findAndRegisterModules();
      errorResponse = mapper.readValue(body, ErrorResponse.class);
    } catch (JsonProcessingException jsonProcessingException) {
      jsonProcessingException.printStackTrace();
    }
    throw new OrderCustomException()
        .builder()
        .errorMessage(errorResponse.getErrorMessage())
        .errorCode(errorResponse.getErrorCode())
        .errorDescription(errorResponse.getShortDescription())
        .build();
  }

  public ResponseEntity<?> getAPIFallBack(HttpStatusCodeException e) {
    // So Error code 4** and 3** is handled differently coz this error means
    // there is no issue with Server
    throw new OrderCustomException()
        .builder()
        .errorMessage("Returning 3** ERROR Response")
        .errorCode(HttpStatus.FORBIDDEN.value())
        .errorDescription("300_ERROR_CODE")
        .build();
  }

  public ResponseEntity<?> getAPIFallBack(Exception e) {
    throw new OrderCustomException()
        .builder()
        .errorMessage("Fallback response- Server is unavailable")
        .errorCode(HttpStatus.SERVICE_UNAVAILABLE.value())
        .errorDescription("SERVICE_UNAVAILABLE")
        .build();
  }
}
