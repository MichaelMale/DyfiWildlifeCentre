# Dyfi Wildlife Centre — Interactive Visitor & Transport Map

An interactive bilingual (**English / Cymraeg**) web mapping application built for Montgomeryshire Wildlife Trust's **Dyfi Wildlife Centre (Cors Dyfi Nature Reserve)** near Machynlleth, Powys, Wales.

Originally developed as an Aberystwyth University Computer Science major project (`CS39440`) and modernized in 2026 to **Java 21**, **Spring Boot 3.5**, **Leaflet / OpenStreetMap**, **Flyway**, **Docker**, and **Playwright E2E testing** without proprietary API keys.

---

## Key Features

### Visitor Experience (`/`)
- **OpenStreetMap & Multi-Layer Leaflet Map:** Clustered Point of Interest markers categorized by **Wildlife**, **Transport**, and **Business**, with live bilingual (`en` / `cy`) UI and POI metadata.
- **Layer Switcher & Reserve Overlays:** Switch between **Street Map (OSM)**, **Topographic (OpenTopoMap)**, and **Satellite (Esri)** base layers, and toggle the **Cors Dyfi Boardwalks & Hides** GeoJSON overlay (`static/data/cors-dyfi-trails.geojson`).
- **Live Instant Search:** Filter reserve locations, 224 Welsh railway stations (`Thomas the Tank Engine` card fallback), and 247 local NaPTAN bus stops (`Magic School Bus` card fallback) by name or transport code.
- **Two-Way OpenStreetMap / OSRM Directions & GPS:** Compute driving or public-transport guidance **To Dyfi Wildlife Centre** or **From Dyfi Wildlife Centre** using a UK postcode (`postcodes.io`), place name (`Nominatim`), coordinate pair, or **"Use My Location"** (`navigator.geolocation`), complete with nearest transport hub recommendations.
- **Embedded Live Osprey Webcam (`/webcams`):** Database-managed YouTube / MP4 live stream viewer inside a responsive modal (`https://www.youtube.com/embed/YnKZJ3SF5WM`).
- **Latest Wildlife Sightings (`/sightings`):** Real-time reserve sightings modal updated from the staff console.
- **Visitor Display Kiosk Mode (`/?kiosk=true`):** Hides staff/admin controls cleanly and resets modals, search filters, and map framing after 60 seconds of inactivity.

### Staff Admin Console (`/admin`, `ROLE_ADMIN`)
- **Interactive Map Picker (`/admin/add` & `/admin/edit_active`):** Click/drag map placement, reserve centring, postcode lookup, image file upload (`/uploads/**`), and safety confirmation locks around protected railway stations and bus stops.
- **Managed POIs & Staff Users (`/admin/list`, `/admin/users`):** Categorized general POIs, Railway Stations, and Bus Stops with search filtering; user registration and self-deletion protection.
- **Seasonal Operations (`/admin/webcams`, `/admin/sightings`, `/admin/import`):** Live webcam management, wildlife sighting logging, and server-side CSV transport upsert/sync (Welsh rail stations + NaPTAN bus stops).

### Operations, API & Testing
- **Security Hardening:** Role-based Spring Security (`ROLE_ADMIN` for all `/admin/**`, `/poi/form_create`, `/poi/update`, `/poi/delete`, `/poi/get/**`, `/users/**`), CSRF protection, and externalized credentials (`.env` / `application-local.properties`).
- **Database Migrations:** Versioned schema & baseline seeds managed via **Flyway** (`src/main/resources/db/migration/V1..V3`).
- **Observability & API Docs:** Spring Boot Actuator (`/actuator/health`) and interactive Swagger / OpenAPI UI (`/swagger-ui.html`, `/v3/api-docs`).

---

## Tech Stack
- **Backend:** Java 21, Spring Boot 3.5.16 (Web, Security, Data JPA, Validation, Actuator), Flyway, Springdoc OpenAPI
- **Frontend:** Thymeleaf, Materialize CSS, Leaflet 1.9 + MarkerCluster
- **Database:** PostgreSQL 16 (production / local / Docker), H2 in PostgreSQL mode (Maven tests)
- **Testing & CI:** JUnit 5, Spring MockMvc, HtmlUnit, Playwright E2E, GitHub Actions (`.github/workflows/ci.yml`)

---

## Running Locally

### 1. With Docker Compose (Recommended)
Boot PostgreSQL 16 + Flyway + Spring Boot app + seeded Welsh transport datasets in one command:
```bash
docker compose up --build
```
- Map UI: `http://localhost:8080/`
- Kiosk Display: `http://localhost:8080/?kiosk=true`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`
- Default Docker bootstrap admin account: `dyfi_admin` / `${APP_BOOTSTRAP_ADMIN_PASSWORD:-dyfi2026!}`

### 2. Local Maven / PostgreSQL Run
1. Copy `.env.example` to `.env` or `application-local.properties` (both are gitignored) and set your PostgreSQL credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/dyfi_wildlife_centre
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```
2. Build and run with Java 21:
   ```bash
   ./mvnw clean package
   java -jar target/dyfiwildlifecentre-1.0.jar
   ```

---

## Running Tests

### Backend Unit, WebMvc & Integration Tests
```bash
./mvnw clean test
```

### Browser End-to-End Tests (Playwright)
```bash
cd e2e
npm install
npx playwright install chromium
npx playwright test
```

---

## License
Licensed under the [Apache License 2.0](LICENSE).
