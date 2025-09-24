package com.github.mangila.resilience.orderservice;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestErrorHandler {

    /**
     * Handle the exception thrown when the circuit breaker is open.
     */
    @ExceptionHandler(CallNotPermittedException.class)
    public ProblemDetail handleCallNotPermittedException(CallNotPermittedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }


    /**
     * Handle the exception thrown when the rate limiter is overloaded.
     */
    @ExceptionHandler(RequestNotPermitted.class)
    public ProblemDetail handleRequestNotPermittedException(RequestNotPermitted ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }
}
