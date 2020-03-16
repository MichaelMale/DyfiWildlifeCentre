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

INSERT INTO public.points_of_interest VALUES (4, 'Poole Harbour is a large natural harbour in Dorset, southern England, with the town of Poole on its shores. The harbour is a drowned valley (ria) formed at the end of the last ice age and is the estuary of several rivers, the largest being the Frome. The harbour has a long history of human settlement stretching to pre-Roman times. The harbour is extremely shallow (average depth 48 cm [19 in]), with one main dredged channel through the harbour, from the mouth to Holes Bay.

Poole Harbour has an area of approximately 36 km (14 sq mi).', 50.6937947999999992, -2.0061703999999998, 'Poole Harbour');
INSERT INTO public.points_of_interest VALUES (1, 'The Dyfi Osprey Project is located at the Cors Dyfi nature reserve near Derwenlas, in the county of Powys, Wales and is under the management of the Montgomeryshire Wildlife Trust.', 52.5687739999999977, -3.91803100000000004, 'Dyfi Wildlife Centre');
INSERT INTO public.points_of_interest VALUES (3, 'This is the place where this application was developed, a student household maintained by ALP Property Management.', 52.416298900000001, -4.07767000000000035, 'Glyndale');
INSERT INTO public.points_of_interest VALUES (2, 'Aberystwyth University (Welsh: Prifysgol Aberystwyth) is a public research university in Aberystwyth, Wales. Aberystwyth was a founding member institution of the former federal University of Wales. The university has over 8,000 students studying across 3 academic faculties and 17 departments.

Founded in 1872 as University College Wales, Aberystwyth, it became a founder member of the University of Wales in 1894 and changed its name to the University College of Wales, Aberystwyth. In the mid-1990s, the university again changed its name to the University of Wales, Aberystwyth. On 1 September 2007, the University of Wales ceased to be a federal university and Aberystwyth became independent again.

Aberystwyth University is placed in the UK’s top 50 universities in the main national rankings and it has become the first university to be awarded with the prestigious prize "University of the year for teaching quality" by The Times/Sunday Times Good University Guide for two consecutive years (2018 and 2019). According to the Research Excellence Framework REF 2014, Aberystwyth ranks in the top 50 out of 154 in the UK for impact, quality of publications and research environment.

It is the first university in the world to be awarded Plastic Free University status (for single-use plastic items).', 52.412505699999997, -4.06107999999999958, 'Aberystwyth University');


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hibernate_sequence', 9, true);


--
-- Name: points_of_interest points_of_interest_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.points_of_interest
    ADD CONSTRAINT points_of_interest_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

