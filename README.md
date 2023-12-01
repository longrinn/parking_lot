# Parking lot
Parking lot spring boot application with postgreSQL database in docker

- [Application Configuration](#application-configuration)
- [Database configuration](#database-configuration)
- [Populate empty database with tables](#populate-empty-database-with-tables)
  - [Database connection in IntelliJ Idea](#database-connection-in-intellij-idea)
  - [Running Flyway Migrations](#running-flyway-migrations)
    - [From Terminal](#from-terminal)
    - [From Maven plugins](#from-maven-plugins)
  - [Verifying the Setup](#verifying-the-setup)
- [Endpoints Documentation](#endpoints-documentation) 

## Application Configuration
To start working with application, consider to don one of the next options:
- Create .env file into src/main/resources with next properties:
    ```text
    flyway.url=
    flyway.user=
    flyway.password=
    ```
- Add environment variables:
    ```text
    FLYWAY_URL=
    FLYWAY_USER=
    FLYWAY_PASSWORD=
    ```

## Database configuration
### 1. Open the terminal where your docker-compose.yaml file is located

### 2. To start your postgres container
Run:
``` shell
docker compose up
```

If you don't want to docker to block your terminal, run:
```shell
docker compose up -d
```

### 3. Your container is up and running
Your database is accessible on localhost at the address
localhost:5432

Credentials are:
- db_name: parking_lot_database
- username: postgres
- password: postgres

## Populate empty database with tables

This guide will walk you through the process of setting up your database using Flyway migrations in our Spring Boot application.

### Database connection in IntelliJ Idea

Before you begin, you have to make sure that your docker container is running.

1. Go to the right corner of your screen and find Database tab
2. Click on '+' and select Data Source
3. You have to select PostgreSQL
4. You will be redirected to a screen where you will insert credentials from application.properties
5. Test connection. If successful, apply and click 'OK'

### Running Flyway Migrations

To execute the Flyway migrations, follow these steps:

#### From Terminal

1. **Open Terminal/Command Prompt:** Navigate to the root directory of the project where the `pom.xml` file is located.

2. **Run Flyway Migrate Command:** Execute the following Maven command:

    ```shell
    mvn flyway:migrate
    ```

   This command will initiate the Flyway migration process, which will apply the necessary database migrations.

#### From Maven plugins

1. In your right corner you may find Maven tab.
2. Find plugins folder and search for the flyway plugin. If you cannot find it, probably your flyway plugin from pom.xml is not recognized yet. _To add the plugin: You have to right click on pom.xml, search for Maven and click on reload project._
3. After you have found the flyway plugin, click on it and select flyway:migrate

### Verifying the Setup

After running the migrations, you should verify that the tables and schema changes have been correctly applied:

1. **Access your Database Console:** Log in to your database management tool or console.

2. **Check the Schema:** Look for the new tables, columns, and any other schema changes that should have been applied. 

You may run each of these queries separately:
1. SELECT * FROM Client;
2. SELECT * FROM Credentials;
3. SELECT * FROM Role;

## Endpoints Documentation

To view all the endpoints and all the JSON files required for the request, you can access the following link:
- [Local Host Swagger](http://localhost:8080/swagger-ui/index.html)
- [Server Swagger](http://parkinglot-be.app.mddinternship.com/swagger-ui/index.html)