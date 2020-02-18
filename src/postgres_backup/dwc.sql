--
-- PostgreSQL database dump
--

-- Dumped from database version 11.7 (Debian 11.7-1.pgdg100+1)
-- Dumped by pg_dump version 11.7 (Debian 11.7-1.pgdg100+1)

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
-- Name: dyfi; Type: SCHEMA; Schema: -; Owner: dyfi_admin
--

CREATE SCHEMA dyfi;


ALTER SCHEMA dyfi OWNER TO dyfi_admin;

--
-- Name: dyfi_wildlife_centre; Type: SCHEMA; Schema: -; Owner: dyfi_admin
--

CREATE SCHEMA dyfi_wildlife_centre;


ALTER SCHEMA dyfi_wildlife_centre OWNER TO dyfi_admin;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: points_of_interest; Type: TABLE; Schema: dyfi; Owner: dyfi_admin
--

CREATE TABLE dyfi.points_of_interest (
    name_en character varying(100) NOT NULL,
    name_cy character varying(100) NOT NULL,
    desc_en text,
    desc_cy text,
    latitude real NOT NULL,
    longitude real NOT NULL
);


ALTER TABLE dyfi.points_of_interest OWNER TO dyfi_admin;

--
-- Name: users; Type: TABLE; Schema: dyfi; Owner: dyfi_admin
--

CREATE TABLE dyfi.users (
    user_id integer NOT NULL,
    first_name character varying(75) NOT NULL,
    last_name character varying(75) NOT NULL,
    email character varying(254) NOT NULL,
    CONSTRAINT proper_email CHECK (((email)::text ~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$'::text))
);


ALTER TABLE dyfi.users OWNER TO dyfi_admin;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: dyfi; Owner: dyfi_admin
--

CREATE SEQUENCE dyfi.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dyfi.users_user_id_seq OWNER TO dyfi_admin;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: dyfi; Owner: dyfi_admin
--

ALTER SEQUENCE dyfi.users_user_id_seq OWNED BY dyfi.users.user_id;


--
-- Name: users user_id; Type: DEFAULT; Schema: dyfi; Owner: dyfi_admin
--

ALTER TABLE ONLY dyfi.users ALTER COLUMN user_id SET DEFAULT nextval('dyfi.users_user_id_seq'::regclass);


--
-- Data for Name: points_of_interest; Type: TABLE DATA; Schema: dyfi; Owner: dyfi_admin
--

COPY dyfi.points_of_interest (name_en, name_cy, desc_en, desc_cy, latitude, longitude) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: dyfi; Owner: dyfi_admin
--

COPY dyfi.users (user_id, first_name, last_name, email) FROM stdin;
\.


--
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: dyfi; Owner: dyfi_admin
--

SELECT pg_catalog.setval('dyfi.users_user_id_seq', 3, true);


--
-- Name: points_of_interest point_of_interest_pk; Type: CONSTRAINT; Schema: dyfi; Owner: dyfi_admin
--

ALTER TABLE ONLY dyfi.points_of_interest
    ADD CONSTRAINT point_of_interest_pk PRIMARY KEY (name_en);


--
-- Name: users users_pk; Type: CONSTRAINT; Schema: dyfi; Owner: dyfi_admin
--

ALTER TABLE ONLY dyfi.users
    ADD CONSTRAINT users_pk PRIMARY KEY (user_id);


--
-- Name: point_of_interest_name_cy_uindex; Type: INDEX; Schema: dyfi; Owner: dyfi_admin
--

CREATE UNIQUE INDEX point_of_interest_name_cy_uindex ON dyfi.points_of_interest USING btree (name_cy);


--
-- Name: point_of_interest_name_en_uindex; Type: INDEX; Schema: dyfi; Owner: dyfi_admin
--

CREATE UNIQUE INDEX point_of_interest_name_en_uindex ON dyfi.points_of_interest USING btree (name_en);


--
-- PostgreSQL database dump complete
--

