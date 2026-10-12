# Hostel Finder: Spring Boot Backend

REST API for the Hostel Finder project (Student / Owner / Admin), built with Java 17, Spring Boot 3.2, Spring Security + JWT, Spring Data JPA and MySQL.

## Run
1. Install JDK 17+, Maven 3.9+ and MySQL 8.
2. Set environment variables (or edit `src/main/resources/application.properties`):
   `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (32+ characters).
3. `mvn spring-boot:run`
4. Swagger UI: http://localhost:8080/swagger-ui.html

The database `hostel_finder` and all tables are created automatically on first run (`docs/schema.sql` is for reference).
A default admin is seeded: `admin@hostelfinder.com` / `Admin@123` (change it) plus one sample college.

## Typical flow
```
POST /api/v1/auth/register   {"name":"Ravi","email":"owner@x.com","password":"secret1","role":"OWNER"}
POST /api/v1/auth/login      -> {"token": "..."}   (send as  Authorization: Bearer <token>)
POST /api/v1/owner/hostels   {"name":"Sri Sai Hostel","latitude":17.45,"longitude":78.35,"rent":6000,
                              "hostelType":"BOYS","foodAvailable":true,"contact":"9999999999","collegeId":1}
POST /api/v1/owner/hostels/1/rooms {"roomType":"2-sharing","totalBeds":4,"availableBeds":2,"rent":6000}
POST /api/v1/owner/hostels/1/facilities {"name":"Wifi"}
PATCH /api/v1/admin/hostels/1/status {"status":"APPROVED"}     (as admin)
GET  /api/v1/colleges/1/hostels?maxRent=7000&maxDistance=3&wifi=true&sortBy=rent
POST /api/v1/student/recommendations {"collegeId":1,"maxBudget":7000,"maxDistance":3,
                                      "foodRequired":true,"wifiRequired":true,"availabilityRequired":true}
```

## Endpoints (base `/api/v1`)
| Area | Endpoints |
|---|---|
| Auth | POST /auth/register, /auth/login |
| Public | GET /colleges, /colleges/{id}/hostels (filters), /hostels/{id}, /hostels/{id}/reviews |
| Student | POST /student/recommendations; GET /student/compare?ids=1,2; GET/POST/DELETE /student/saved/{hostelId}; POST /student/hostels/{id}/reviews; DELETE /student/reviews/{id} |
| Owner | GET/POST /owner/hostels; PUT/DELETE /owner/hostels/{id}; POST /owner/hostels/{id}/rooms, /facilities, /images; PUT /owner/rooms/{roomId}/availability?availableBeds= |
| Admin | GET /admin/hostels?status=PENDING; PATCH /admin/hostels/{id}/status; GET /admin/users; PATCH /admin/users/{id}/block; POST /admin/colleges; DELETE /admin/reviews/{id} |

## Structure
`controller` (REST) → `service` (business logic, recommendation) → `repository` (JPA) → MySQL; `security` (JWT filter), `config`, `dto`, `entity`, `exception`, `util` (Haversine).

## Notes
- New or edited hostels are `PENDING` until an admin approves them; only `APPROVED` hostels are public.
- Images: upload to cloud storage and send the URL to `/owner/hostels/{id}/images`.
- The report with diagrams (ER, DFD, use case) is in `docs/`.
- Not included yet: pagination and file upload (images are URLs).

## Troubleshooting
- `No plugin found for prefix 'speing-boot'` -> typo in the command. Use exactly `mvn spring-boot:run` (or run `run.bat` / `run.sh`).
- Run Maven from the `backend` folder (where `pom.xml` is), not from the project root.
- MySQL must be running; set `DB_USER` / `DB_PASSWORD` to your credentials.
- `cannot find symbol getXxx()` / `variable not initialized in the default constructor` -> Lombok was not running. `pom.xml` now declares Lombok as an annotation processor (v1.18.42). Run `mvn clean spring-boot:run`. Check `java -version` and `mvn -v` show the same JDK (17+).
- `Access denied for user 'root'@'localhost' (using password: YES)` -> wrong MySQL password. Set it in `src/main/resources/application.properties` (`spring.datasource.password`) or via the `DB_PASSWORD` environment variable. Default in this project is `system`.
- `Field 'ai_generated' doesn't have a default value` -> an old/extra column in the `colleges` table. `SchemaFix` fixes it at startup, or run: `ALTER TABLE colleges ALTER COLUMN ai_generated SET DEFAULT 0;`
- Unexpected errors show `Something went wrong [reason]` while `app.debug-errors=true` (see application.properties); set it to `false` for production.
