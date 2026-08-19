# Eventora Reviews Service

Independent domain microservice for event ratings and visitor reviews. It owns the `EventReview` entity, repository, service, validation and REST error handling, runs on port 8081 and uses a database separate from Eventora main.

## API

- `GET /api/reviews/event/{eventId}`
- `POST /api/reviews`
- `PUT /api/reviews/{reviewId}`
- `DELETE /api/reviews/{reviewId}?authorId={authorId}`

The main application invokes every endpoint through OpenFeign. A visitor can create one review per event and can edit or delete only their own review.

## Run and test

```bash
../mvnw spring-boot:run
../mvnw clean verify
```
