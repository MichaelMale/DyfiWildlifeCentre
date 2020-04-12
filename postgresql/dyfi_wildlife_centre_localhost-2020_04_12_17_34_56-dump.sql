--
-- PostgreSQL database dump
--

-- Dumped from database version 12.2
-- Dumped by pg_dump version 12.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: authorities; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.authorities (
    user_role_id integer NOT NULL,
    username character varying(50) NOT NULL,
    authority character varying(50) NOT NULL
);


ALTER TABLE public.authorities OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: points_of_interest; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.points_of_interest (
    id bigint NOT NULL,
    description text,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT chk_name CHECK ((char_length((name)::text) >= 1)),
    CONSTRAINT latitude_chk CHECK (((latitude >= ('-180'::integer)::double precision) AND (latitude <= (180)::double precision))),
    CONSTRAINT longitude_chk CHECK (((longitude >= ('-90'::integer)::double precision) AND (longitude <= (90)::double precision)))
);


ALTER TABLE public.points_of_interest OWNER TO postgres;

--
-- Name: user_roles_user_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_roles_user_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_roles_user_role_id_seq OWNER TO postgres;

--
-- Name: user_roles_user_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_roles_user_role_id_seq OWNED BY public.authorities.user_role_id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    enabled boolean DEFAULT true NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: authorities user_role_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authorities ALTER COLUMN user_role_id SET DEFAULT nextval('public.user_roles_user_role_id_seq'::regclass);


--
-- Data for Name: authorities; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.authorities (user_role_id, username, authority) FROM stdin;
1	michael	ROLE_ADMIN
2	matthew	ROLE_USER
\.


--
-- Data for Name: points_of_interest; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.points_of_interest (id, description, latitude, longitude, name) FROM stdin;
4	Place Four Description	50.6937948	-2.0061704	Poole Harbour
1	Place One Description	52.568774	-3.918031	Dyfi Wildlife Centre
2	Place Two Description	52.4125057	-4.06108	Aberystwyth University
15	The parliament of Wales, otherwise known as Senedd Cymru.	51.4639	-3.1621	National Assembly for Wales
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (username, password, enabled) FROM stdin;
michael	{noop}123456	t
matthew	{noop}123456	t
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hibernate_sequence', 16, true);


--
-- Name: user_roles_user_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_roles_user_role_id_seq', 2, true);


--
-- Name: points_of_interest points_of_interest_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.points_of_interest
    ADD CONSTRAINT points_of_interest_pkey PRIMARY KEY (id);


--
-- Name: authorities user_roles_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT user_roles_pk PRIMARY KEY (user_role_id);


--
-- Name: users users_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (username);


--
-- Name: user_roles_role_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX user_roles_role_uindex ON public.authorities USING btree (authority);


--
-- Name: user_roles_username_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX user_roles_username_uindex ON public.authorities USING btree (username);


--
-- Name: authorities user_roles_users_username_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT user_roles_users_username_fk FOREIGN KEY (username) REFERENCES public.users(username);


--
-- PostgreSQL database dump complete
--

