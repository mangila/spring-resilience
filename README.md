# spring-resilience

Design for resilience is the engineering practice for the application to be able to withstand for failures and recover from them.

Java and Spring Boot ecosystem can use a Resilience4j library to use stable and production-ready resilience patterns.

To name a few of the resilience patterns:

* Circuit Breaker
* Retry Strategy
* Timeout Strategy
* Bulkhead Pattern
* Rate Limiting
* Fallback Pattern
* Chaos Engineering


### Circuit Breaker

Middleware that keeps track of the number of failed requests and if the number of failures exceeds a threshold, it can stop sending requests to the service.

### Retry Strategy

If a request fails, it can be retried. The client can decide how many times to retry the request.

### Timeout Strategy

If a request takes too long to complete, it can be canceled.

### Bulkhead Pattern

To make sure the service doesn't get overloaded, it can be limited to a certain number of concurrent requests.

### Rate Limiting

To prevent a service from being overwhelmed, it can be limited to a certain number of requests per second.

### Fallback Pattern

If a service fails, it can return a fallback response.

### Chaos Engineering

To test the resilience of a service, it can be randomly failing requests.


