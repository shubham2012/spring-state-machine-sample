# spring-state-machine-sample
This is a spring boot state machine sample.
We have tried to cover few of the state transitions and type of transitions that you could do in the state machine
I have mostly tried to focus on the overall picture rather than type of transitions that you can do. 
For different type of transitions you can check out https://www.baeldung.com/spring-state-machine

Here I have focussed on how you can have your dedicated state machine and how can you leverage the handler functionality in a better way to handle all the transition flow properly

## Step to run after cloning this repo 
Install Java 11

Install maven 

run simple mvn clean install 

then just the file StateMachineSampleApplication



## Validate the state machine by API call 
### Request
curl --location --request POST 'http://localhost:80/state-machine-service/state-machine/invoke' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data-raw '{
"orderId": "CREATE_NORMAL_JIT",
"event": "DISPATCH_BY_VENDOR",
"updatedBy": "Test user",
"remarks": "Dispatched by vendor in bulk",
"location": "Test Location"
}'

### Response
{
"orderUpdate": {
"orderId": "CREATE_NORMAL_JIT",
"event": "DISPATCH_BY_VENDOR",
"updatedBy": "Test user",
"remarks": "Dispatched by vendor in bulk",
"location": "Test Location"
},
"orderUpdateResponseCode": "SUCCESS",
"orderStatusPreEvent": "CREATED",
"orderStatusPostEvent": "PROCESSING"
}


### Request
curl --location --request POST 'http://localhost:80/state-machine-service/state-machine/invoke' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data-raw '{
"orderId": "CREATE_NORMAL_JIT",
"event": "PACK",
"updatedBy": "Test user",
"remarks": "Dispatched by vendor in bulk",
"location": "Test Location"
}'

### Response
{
"orderUpdateResponseCode": "TRANSITION_NOT_ALLOWED",
"orderStatusPreEvent": "CREATED",
"exceptionMessage": "PACK"
}


