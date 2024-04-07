Before API gateway we are using these api's for getting or adding the data
    
For Product Service :
            
To save a service: http://localhost:8080/product/add
Request Body below:-
{
    "name":"Macbook",
    "price":2000,
    "quantity":10
}

To get a product : http://localhost:8080/product/findById/1

To reduce Quantity manually : http://localhost:8080/product/reduceQuantity/1?quantity=1


For Order Service :

For placing an order:  http://localhost:8082/order/placeOrder

{
"productId":1,
"amount": 1100,
"quantity":1,
"paymentMode":"CASH"
}

It will internally call other services to reduce quantity and payment service to add payement details to db

For Getting Data :

http://localhost:8082/order/getOrder/2



AFTER API GATEWAY

http://localhost:9090/order/getOrder/2