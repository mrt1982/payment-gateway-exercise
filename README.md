
Candidate: Thurston George Davis
## Prerequisites: <br />
Will need Gradle and Java 21 installed
## To build and run the application: <br />
Start the bank simulator<br />
```
docker-compose up
```
Either with gradle spring-boot plugin

```
./gradlew bootRun
```
Or building/running the jar <br />
```
./gradlew clean build
java -jar build/libs/payment-gateway-challenge-java-0.0.1-SNAPSHOT.jar
```
To see if the application is up and running
```
curl localhost:8090/payment-gateway-api/health
```
## API Endpoints <br />
1. Create a Payment POST: /payment-gateway-api/payment
```
curl -iv 'http://localhost:8090/payment-gateway-api/payment' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json' \
--data '{
    "idempotencyKey": "8014726c-b503-4bae-a27e-5d6e7a2603ca",
    "card_number": "2222405343248877",
    "expiry_month": 4,
    "expiry_year": 2025,
    "currency": "GBP",
    "amount": "100",
    "cvv": "123"
}'
```
2. Get a Payment by Id GET: /payment-gateway-api/payment/{id}
```
curl -iv 'http://localhost:8090/payment-gateway-api/payment/4b7f0f73-4a11-4791-940f-833ed930b6c0' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json'
```


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**