# bulletinboard-ads
This is the **Spring Boot** version of the advertisements-service for the bulletin board application.
Advertisements can be created, deleted and viewed.
You can interact with the service using a REST client like Postman or the GUI.

## Ad-Blocker
**If the UI does not load, please make sure your ad-blocker is not accidentially blocking parts of the application ;-)**

## Relation to 'bulletinboard-reviews'
If a user got poor ratings from previous reviews, or hasn't received any reviews yet, the user is considered not trustworthy and will be colored red.

## How to work locally
### Execute tests
The tests can be executed with Maven: `mvn verify`

### Start service locally
The service needs a database to store its data.

The script `start-db.sh` can be used to start a local database (using Docker).

In order to reset the database state (schema & data), run `docker volume rm bb_ads_local_java`

Afterwards run `mvn spring-boot:run` to start the service.
The service will listen on port 8080.

## A word on cloud readiness

### Cloud Foundry
To speed a up the configuration for a deployment in Cloud Foundry a [manifest.yml](manifest.yml) with placeholders is provided.

### Kubernetes
For a deployment of the service in Kubernetes [pre-configured yml-files](.k8s) with placeholders are already part of the repository.
Along with a basic [Dockerfile](Dockerfile).

## Interact with the application

### Using the API
The following endpoints are supported and tested (remember to set the `application/json` content-type header):
- `GET /api/v1/ads`: get all ads
  Response: `200 OK`
  Response Body:
```
    [
        {
            "id": <int>,
            "title": <text>,
            "price": <number>,
            "contact": "<text>",
            "averageContactRating": <number>, //correlates to the average rating from the reviews service
            "currency": <text>,
            "metadata": {
                "createdAt": <date>,
                "modifiedAt": <date>,
                "version": <int>
            },
            "reviewsUrl": <text> //redirectUrl
        },
        ...
    ]
```
- `GET /api/v1/ads/:id`: get single ad
  Response: `200 OK`
```
    {
        "id": <int>,
        "title": <text>,
        "price": <number>,
        "contact": "<text>",
        "averageContactRating": <number>, //correlates to the average rating from the reviews service
        "currency": <text>,
        "metadata": {
            "createdAt": <date>,
            "modifiedAt": <date>,
            "version": <int>
        },
        "reviewsUrl": <text> //redirectUrl
    }
```
- `GET /api/v1/ads/:id`: get single ad
  Response: `200 OK`
- `GET /api/v1/ads/pages/:pagenumber`: get ads per page (20 per page)
  Response: `200 OK`
- `POST /api/v1/ads`: post a new ad
  Request Body:
```
    {
        "title": <text>,
        "currency": <text>,
        "price": <number>,
        "contact": <text>
    }
```
- `PUT /api/v1/ads/:id`: update an ad
  Request Body:
```
    {
        "id": <int>,
        "title": <text>,
        "currency": <text>,
        "price": <number>,
        "contact": <text>
    }
```
  Response: `201 Created`
- `DELETE /api/v1/ads`: delete all ads
  Response: `204 No Content`

