create table users
(
	id bigserial not null
		constraint users_pk
			primary key,
	username varchar(32) not null
		constraint chk_username
			check ((length((username)::text) >= 6) AND (length((username)::text) <= 32)),
	password varchar(60) not null
);

alter table users owner to postgres;

create unique index users_username_uindex
	on users (username);

