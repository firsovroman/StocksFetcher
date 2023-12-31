package com.example.stocksfetcher.ws;

import com.example.stocksfetcher.config.ApperateConfig;
import com.example.stocksfetcher.models.Companies;
import com.example.stocksfetcher.models.Stock;
import com.example.stocksfetcher.models.Stocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

public class ApperateClient {

    protected final Logger logger = LoggerFactory.getLogger(ApperateClient.class);


    private final ApperateConfig config;

    protected final RestTemplate restTemplate;


    public ApperateClient(ApperateConfig config) {
        this.config = config;
        this.restTemplate = createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        return new RestTemplateBuilder()
                .build();
    }

    /**
     * потянуть ~ 11 190 компаний
     *
     */

    public Companies sendCompanyRequest() {
        logger.debug("companyRequest() called");
        long started = System.nanoTime();

        // Создайте URL с токеном
        String url = config.getUrl() + "ref-data/symbols?token=" + config.getToken();

        // Создайте ParameterizedTypeReference для сопоставления с объектом Companies
        ParameterizedTypeReference<Companies> responseType = new ParameterizedTypeReference<>() {};

        // Выполните GET-запрос и сопоставьте ответ с объектом Companies
        ResponseEntity<Companies> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, responseType);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        Companies response = responseEntity.getBody();

        if (HttpStatus.OK != httpStatus || response == null) {
            throw new IllegalStateException("Bad or empty response: " + httpStatus);
        }

        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
        logger.debug("completed in {} msecs", elapsed);

        return response;
    }


    public Stock stockRequest(String symbol) {
        logger.debug("stockRequest() called");
        long started = System.nanoTime();

        // Создайте URL с токеном
        String url = config.getUrl() + "data/core/quote/" + symbol + "?token=" + config.getToken();

        // Создайте ParameterizedTypeReference для сопоставления с объектом Companies
        ParameterizedTypeReference<Stocks> responseType = new ParameterizedTypeReference<>() {};

        // Выполните GET-запрос и сопоставьте ответ с объектом Companies
        ResponseEntity<Stocks> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, responseType);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        Stocks response = responseEntity.getBody();

        if (HttpStatus.OK != httpStatus || response == null) {
            throw new IllegalStateException("Bad or empty response: " + httpStatus);
        }

        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
        logger.debug("completed in {} msecs", elapsed);

        if (response.size() != 1) {
            throw new IllegalStateException("In response more than one stock: " + httpStatus);
        }

        return response.get(0);
    }



}
