create table points_of_interest
(
	id bigint not null
		constraint points_of_interest_pkey
			primary key,
	description text,
	latitude double precision not null
		constraint latitude_chk
			check ((latitude < (90)::double precision) AND (latitude > ('-90'::integer)::double precision))
		constraint latitude_not_null_island_chk
			check (latitude <> (0)::double precision),
	longitude double precision not null
		constraint longitude_chk
			check ((longitude < (180)::double precision) AND (longitude > ('-180'::integer)::double precision))
		constraint longitude_not_null_island_chk
			check (longitude <> (0)::double precision),
	name varchar(255) not null
		constraint chk_name
			check (char_length((name)::text) >= 1),
	postcode varchar(8)
		constraint postcode_chk
			check ((postcode)::text ~* '^$|^[a-z]{1,2}\d[a-z\d]?\s*\d[a-z]{2}$'::text),
	distance_from_centre double precision,
	category varchar(9)
);

alter table points_of_interest owner to postgres;

