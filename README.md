# marketplace-backend

Marketplace-backend is the backend of our application. It is a web application written in Java using the Spring Boot framework. The backend is a RESTful API which utilizes different HTTP methods for communication. Server sent events are also used, for sending messages from the backend to the frontend. 

This application acts as our web server where we have defined endpoints for our frontend to make use of. 
PostgreSQL is used as the database and digitalocean.com to host content created by users. 
Railway is used to host our database, which provides a containerized environment application.

## Prerequisities 
- Java 21+
- PostgreSQL: [PostgreSQL](https://www.postgresql.org/)
- .env with the following environment variables set:
  - POSTGRES_DB
  - POSTGRES_USER
  - POSTGRES_PASSWORD
  - IMAGE_UPLOAD_DIRECTORY
  - IMAGE_HOST_URL
- Docker for running tests (OPTIONAL): [Docker](https://www.docker.com/)
  
## Installation (TERMINAL)
0. ```git clone https://github.com/Insanityandme/marketplace-backend.git```
1. ```cd marketplace-backend```

WINDOWS:
1. ```gradlew build -x test```
2. ```gradlew bootRun```

LINUX: 
1. ```./gradlew build -x test```
2. ```./gradlew bootRun```

It should look something like this: ![image](https://github.com/Insanityandme/marketplace-backend/assets/1380257/a8389660-41be-4928-ba69-f040fb17d574)

## Installation (IntelliJ)
0. ```git clone https://github.com/Insanityandme/marketplace-backend.git```
1. Open the project in IntelliJ
2. Let IntelliJ install all required dependencies
3. Press the play button to run the application

It should look something like this: ![image](https://github.com/Insanityandme/marketplace-backend/assets/1380257/5d74f24c-6d7e-444f-9090-50d189b0c7eb)

     
