# bulletinboard-reviews
This is the **SpringBoot** version of the reviews-service for the bulletin board application.
Reviews of users can be created, deleted and viewed.
You can interact with the service using a REST client like Postman or the GUI.

**Note:** Reviews refer to the _users_ that advertize things, _not_ the advertised things.

## Ad-Blocker
**If the UI does not load, please make sure your ad-blocker is not accidentially blocking parts of the application ;-)**

## How to work locally
### Execute tests
The tests can be executed with maven: `mvn verify`

### Start service locally
The service needs a database to store its data.

The script `start-db.sh` can be used to start a local database (using Docker).

In order to reset the database state (schema & data), run `docker volume rm bb_reviews_local_java`

Afterwards run `mvn spring-boot:run` to start the service.
The service will listen on port 9090.

## A word on cloud readiness

### Cloud Foundry
To speed a up the configuration for a deployment in Cloud Foundry a [manifest.yml](manifest.yml) with placeholders is provided.

### Kubernetes
For a deployment of the service in Kubernetes [pre-configured yml-files](.k8s) with placeholders are already part of the repository.
Along with a basic [Dockerfile](Dockerfile).

## Interact with the application

### Using the GUI
Reviews for a user `[email]` can be made at `/#/reviews/:email`.

### Using the API
The following endpoints are supported and tested (remember to set the `application/json` content-type header):
- `GET /api/v1/averageRatings/:email`: given the email of a user, get his/her average rating
  Response: `200 OK`
  Response Body:
  ```
    { "averageRating": <number> }
  ```

  **Note:** `<number>` may be zero, if there are no reviews for this user. The endpoint doesn't return 404 in this case, because the cases of "user not existing" and "user doesn't have any reviews yet" cannot be distinguished.
- `GET /api/v1/reviews`: get all reviews
  Response: `200 OK`
  Response Body:
  ```
    [
        {
            "revieweeEmail": <text>, 
            "reviewerEmail": <text>, 
            "rating": <integer>, 
            "comment": <text>
        },
        ...
    ]
  ```
- `POST /api/v1/reviews`: post a new review
  Request Body:
  ```
    {
        "revieweeEmail": <text>, 
        "reviewerEmail": <text>, 
        "rating": <integer>, 
        "comment": <text>
    }
  ```
  Response: `201 Created`
- `DELETE /api/v1/reviews`: delete all reviews
  Response: `204 No Content`

