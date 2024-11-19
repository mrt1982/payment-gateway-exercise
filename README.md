
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
    "paymentMethodType": "CARD",
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
curl -iv 'http://localhost:8090/payment-gateway-api/payment/72ff74a1-647f-48bd-9f2d-1077589f4f8f' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json'
```
