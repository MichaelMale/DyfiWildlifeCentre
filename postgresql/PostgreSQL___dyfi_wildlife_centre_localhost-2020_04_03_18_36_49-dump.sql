--
-- PostgreSQL database dump
--

-- Dumped from database version 11.7 (Ubuntu 11.7-0ubuntu0.19.10.1)
-- Dumped by pg_dump version 11.7 (Ubuntu 11.7-0ubuntu0.19.10.1)

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
-- Name: dyfi; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA dyfi;


ALTER SCHEMA dyfi OWNER TO postgres;

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

SET default_tablespace = '';

SET default_with_oids = false;

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
-- Data for Name: points_of_interest; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.points_of_interest VALUES (4, 'Place Four Description', 50.6937947999999992, -2.0061703999999998, 'Poole Harbour');
INSERT INTO public.points_of_interest VALUES (1, 'Place One Description', 52.5687739999999977, -3.91803100000000004, 'Dyfi Wildlife Centre');
INSERT INTO public.points_of_interest VALUES (2, 'Place Two Description', 52.412505699999997, -4.06107999999999958, 'Aberystwyth University');
INSERT INTO public.points_of_interest VALUES (3, 'Place Three Description', 52.416298900000001, -4.07767000000000035, 'Glyndale');


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hibernate_sequence', 13, true);


--
-- Name: points_of_interest points_of_interest_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.points_of_interest
    ADD CONSTRAINT points_of_interest_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

