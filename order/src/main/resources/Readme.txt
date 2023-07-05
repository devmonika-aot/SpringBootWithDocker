# http://localhost:8081/actuator/health
{"status":"UP","components":{"circuitBreakers":{"status":"UP","details":{"serviceA":{"status":"UP","details":{"failureRate":"-1.0%","failureRateThreshold":"50.0%","slowCallRate":"-1.0%","slowCallRateThreshold":"100.0%","bufferedCalls":1,"slowCalls":0,"slowFailedCalls":0,"failedCalls":0,"notPermittedCalls":0,"state":"CLOSED"}}}},"clientConfigServer":{"status":"UP","details":{"propertySources":["configserver:https://github.com/devmonika-aot/spring-microservice-app-config/application.properties","configClient"]}},"db":{"status":"UP","details":{"database":"MySQL","validationQuery":"isValid()"}},"discoveryComposite":{"status":"UP","components":{"discoveryClient":{"status":"UP","details":{"services":["api-gateway","order-service","config-server","product-service"]}},"eureka":{"description":"Remote status from Eureka server","status":"UP","details":{"applications":{"API-GATEWAY":1,"ORDER-SERVICE":1,"CONFIG-SERVER":1,"PRODUCT-SERVICE":1}}}}},"diskSpace":{"status":"UP","details":{"total":479444934656,"free":367462424576,"threshold":10485760,"exists":true}},"ping":{"status":"UP"},"refreshScope":{"status":"UP"}}}
#Implemented Below Concept
   ## 1. CircuitBreaker
    It has been configured in the yaml file  and in the ProductClient class annotated with @CircuitBreaker
    Its is basically if server is down, how it shd respond meaning
    how many second it will wait, CLOSE,OPEN and HALFOPEN state.

   ## 2. Retry
    It has been configured in the yaml file  and in the ProductClient class annotated with @Retry.
    Retrying once server is down
   ## 3. RateLimiter
    It has been configured in the yaml file  and in the ProductClient class annotated with @RateLimiter
    limit the calling frequency , ex with in 10s , uptop 10 call can be made in that
    server, If someone try to hit more than that it will stuck for 3s.

#Handle 4** and 3** error differently as this is not server error , It might cause coz of input request
So, these error doesn't cause due to server down.


