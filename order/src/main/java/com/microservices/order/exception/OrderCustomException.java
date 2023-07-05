package com.microservices.order.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class OrderCustomException extends RuntimeException {
    private String errorMessage;
    private long errorCode;
    private String errorDescription;

    public OrderCustomException(String errorMessage, long errorCode, String errorDescription) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }


    public OrderCustomException() {

    }
}
