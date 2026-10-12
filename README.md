# Courier and Parcel Tracking System

Jakarta Servlets + JSP web app (JDK 17, MySQL, Apache Tomcat 10.1) for scheduling pickups, assigning delivery agents, tracking parcels, and generating invoices.

## Requirements

- JDK 17
- Apache Maven 3.9+
- MySQL 8 (`root` / `root` by default — change [src/main/resources/db.properties](src/main/resources/db.properties))
- Apache Tomcat **10.1** (Jakarta EE 10)

Git is optional. This machine did not have `git` on PATH when the project was created; run `git init` locally if you want version control.

## Database

The app creates `courier_db` and tables on first start, then seeds demo users if the `users` table is empty.

You can also run the scripts yourself:

```sql
SOURCE src/main/resources/schema.sql;
```

Demo logins (created automatically):

| Username   | Password  | Role     |
|------------|-----------|----------|
| admin      | admin123  | ADMIN    |
| agent1     | agent123  | AGENT    |
| customer1  | cust123   | CUSTOMER |

## Build and deploy

```bash
mvn package
```

Copy `target/courier-parcel-tracking.war` to Tomcat `webapps/`, start Tomcat, then open:

http://localhost:8080/courier-parcel-tracking/

## Main flows

- **Public:** home page tracking by number (`/track`)
- **Customer:** register/login, schedule pickup, track, view invoices
- **Agent:** assigned deliveries, status updates (including confirm delivery)
- **Admin:** customers, agents, parcels, assignment, invoices, reports, users
