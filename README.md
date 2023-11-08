# Parking lot
Parking lot spring boot application with postgreSQL database in docker

- [Database configuration](#database-configuration)

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
