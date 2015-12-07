## Delivery service

The delivery service project is designed to find the most economical path for a delivery.

Given a map with routes, all that is needed is inform the origin, destination, autonomy and liter price, with these attributes the service will find the most economical path according to database.

## Getting Started


* Install Java JDK 7 or newer
* Install Maven 3.3
* Install Sqlite3

After setting the prerequisites you can start the project:

1. Clone the project at the command prompt:

  ```sh
  $ git clone https://github.com/j133y/delivery-service-java.git
  $ cd delivery-service-java
  ```

2. Compile the project using maven:
  ```sh
  $ mvn compile
  ```
  
3. Run the tests:
  ```sh
  $ mvn test
  ```

4. Package and run the service:
  ```sh
  $ mvn package
  $ java -jar ./target/delivery-service-java-1.0-SNAPSHOT.jar
  ```
  
  or using:
  
  ```sh
  $ mvn exec:java
  ```

6. After following the steps above, the project will bootup. The next section shows how to use the service.


## Using the delivery service

The following examples uses the ```curl``` library, if you don't have it installed yet, please install it before moving on. 

### Versioning

This service is intented to be versioned, that requires each request having the correct version on headers.

By default, all requests receive the v1 version of the API. I encourage you to explicitly request this version via the Accept header:

```
Accept: application/vnd.delivery.v1
```

### Schema

All API access is over HTTP, if you are on development enviroment, the API can be accessed from the ```http://localhost:8080/api/```. All data is sent and received as JSON.


### Client Errors

There are three possible types of client errors on API calls that receive request bodies:


1. Sending invalid JSON will result in a 400 Bad Request response.

```
HTTP/1.1 400 Bad Request
```

2. Sending invalid fields will result in a 422 Unprocessable Entity response.

```
HTTP/1.1 422 Unprocessable Entity
```

3. Requesting invalid entity will result in a 404 Not Found response.

```
HTTP/1.1 404 Not Found
```

### Maps

#### Create a map with routes

Create a map resource with routes.

```
POST /maps
```

**Parameters**

| Name              | Type        | Description  |
| ----------------- |-------------| ----------------------------------------|
| name              | string      | **Required**. The map name. |
| routes | array       | **Required**. An array of route objects.|

**Route**

| Name              | Type        | Description  |
| ----------------- |-------------| ----------------------------------------|
| origin            | string      | **Required**. The origin name. |
| destination       | string      | **Required**. The destination name.|
| distance          | integer     | **Required**. The distance between origin and destination.|

**Example**

```sh
curl -XPOST -H "Accept: application/vnd.delivery.v1" -H "Accept: application/json" -H "Content-Type: application/json" -d '{ "name": "Mapa RS", "routes": [{ "origin": "Porto Alegre", "destination": "Torres", "distance": 100 }, { "origin": "Torres", "destination": "Tramandai", "distance": 50 }] }' http://localhost:8080/api/maps -v
```

**Response**

```
POST /api/maps HTTP/1.1
Host: localhost:8080
User-Agent: curl/7.43.0
Accept: application/vnd.delivery.v1
Accept: application/json
Content-Type: application/json

HTTP/1.1 201 Created
Location: http://localhost:8080/api/maps/8

{
  "id": 8,
  "name": "Mapa RS",
  "routes": [
    {
      "id": 16,
      "origin": "Porto Alegre",
      "destination": "Torres",
      "distance": 100
    },
    {
      "id": 17,
      "origin": "Torres",
      "destination": "Tramandai",
      "distance": 50
    }
  ]
}
```

#### Estimate Delivery

Estimate the most econimical path for the given delivery information.

```
POST /maps/estimate_delivery
```

**Parameters**

| Name              | Type        | Description  |
| ----------------- |-------------| ----------------------------------------|
| name              | string      | **Required**. The map name. |
| origin            | string      | **Required**. The delivery origin. |
| destination       | string      | **Required**. The delivery destination. |
| liter_price       | integer     | **Required**. The fuel price per liter. |
| autonomy          | integer     | **Required**. The autonomy of the transportation. |


**Example**

```sh
curl -XPOST -H "Accept: application/vnd.delivery.v1" -H "Accept: application/json" -H "Content-Type: application/json" -d '{ "name": "Mapa salvador", "origin": "Salvador", "destination": "Guarapari", "literPrice": 2.5, "autonomy": 10}' http://localhost:8080/api/maps/estimate_delivery -v
```

**Response**

```
POST /api/maps/estimate_delivery HTTP/1.1
Host: localhost:8080
User-Agent: curl/7.43.0
Accept: application/vnd.delivery.v1
Accept: application/json
Content-Type: application/json

HTTP/1.1 200 OK

{
  "routes": [
    "salvador",
    "ilheus",
    "guarapari"
  ],
  "cost": 162.5
}

```

#### List maps

List all maps resources with routes.

```
GET /maps
```

**Example**

```sh
curl -XGET -H "Accept: application/vnd.delivery.v1" -H "Accept: application/json" http://localhost:8080/api/maps -v
```

**Reponse**
```
GET /api/maps HTTP/1.1
Host: localhost:8080
User-Agent: curl/7.43.0
Accept: application/vnd.delivery.v1
Accept: application/json

HTTP/1.1 200 OK

[
  {
    "id": 1,
    "name": "Mapa Sp/Rj",
    "routes": [
      {
        "id": 1,
        "origin": "São Paulo",
        "destination": "Rio de Janeiro",
        "distance": 800
      }
    ]
  }
]
```
