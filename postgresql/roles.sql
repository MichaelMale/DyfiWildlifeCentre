create table roles
(
	id bigserial not null
		constraint roles_pk
			primary key,
	name varchar(255)
);

alter table roles owner to postgres;

