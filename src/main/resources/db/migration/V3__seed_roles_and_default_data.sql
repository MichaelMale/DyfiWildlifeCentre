-- Idempotent baseline data for fresh databases (safe no-op on populated databases)

INSERT INTO roles (name)
SELECT 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO points_of_interest (
    id, name, name_cy, description, description_cy,
    latitude, longitude, postcode, distance_from_centre,
    category, image_url
)
SELECT nextval('points_of_interest_seq'),
       'Dyfi Wildlife Centre',
       'Canolfan Bywyd Gwyllt Dyfi',
       'Cors Dyfi Nature Reserve visitor centre, 360 Observatory, and Dyfi Osprey Project.',
       'Canolfan ymwelwyr Gwarchodfa Natur Cors Dyfi, Arsyllfa 360, a Phrosiect Gwalch y Pysgod Dyfi.',
       52.568774,
       -3.918031,
       '',
       0.0,
       'wildlife',
       '/images/osprey.jpeg'
WHERE NOT EXISTS (SELECT 1 FROM points_of_interest);
