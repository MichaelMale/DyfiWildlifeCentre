create table users_roles
(
	users_id bigint not null
		constraint users_roles_pk
			primary key
		constraint users_roles_users_id_fk
			references users,
	roles_id bigint not null
		constraint users_roles_roles_id_fk
			references roles
);

alter table users_roles owner to postgres;

