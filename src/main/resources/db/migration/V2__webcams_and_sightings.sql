-- Database-managed reserve webcams and wildlife sightings log

CREATE TABLE IF NOT EXISTS webcams
(
    id             BIGSERIAL PRIMARY KEY,
    slug           VARCHAR(64)   NOT NULL UNIQUE,
    name_en        VARCHAR(160)  NOT NULL,
    name_cy        VARCHAR(160),
    description_en VARCHAR(512),
    description_cy VARCHAR(512),
    stream_url     VARCHAR(2048) NOT NULL,
    poster_url     VARCHAR(2048),
    active         BOOLEAN       NOT NULL DEFAULT TRUE,
    sort_order     INTEGER       NOT NULL DEFAULT 0
);

INSERT INTO webcams (slug, name_en, name_cy, description_en, description_cy, stream_url, poster_url, active, sort_order)
SELECT 'nest-cam',
       'Osprey Nest Camera',
       'Camera Nyth Gwalch y Pysgod',
       'Live view of the main osprey nesting platform at Cors Dyfi.',
       'Golygfa fyw o brif lwyfan nythu gwalch y pysgod yng Nghors Dyfi.',
       'https://www.youtube.com/embed/YnKZJ3SF5WM?rel=0',
       '/images/osprey.jpeg',
       TRUE,
       1
WHERE NOT EXISTS (SELECT 1 FROM webcams WHERE slug = 'nest-cam');

CREATE TABLE IF NOT EXISTS wildlife_sightings
(
    id            BIGSERIAL PRIMARY KEY,
    species       VARCHAR(160) NOT NULL,
    species_cy    VARCHAR(160),
    location_name VARCHAR(160),
    notes         VARCHAR(1024),
    notes_cy      VARCHAR(1024),
    sighted_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recorded_by   VARCHAR(64)
);

INSERT INTO wildlife_sightings (species, species_cy, location_name, notes, notes_cy, sighted_at, recorded_by)
SELECT 'Osprey (Telyn & Idris)',
       'Gwalch y Pysgod (Telyn ac Idris)',
       'Osprey Nest & 360 Observatory',
       'Breeding pair active around the Cors Dyfi nesting platform and main boardwalk channel.',
       'Pâr nythu yn weithgar o amgylch llwyfan nythu Cors Dyfi a phrif sianel y llwybr bwrdd.',
       CURRENT_TIMESTAMP,
       'dyfi_admin'
WHERE NOT EXISTS (SELECT 1 FROM wildlife_sightings);
