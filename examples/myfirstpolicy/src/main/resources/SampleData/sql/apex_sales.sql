/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

--
-- Create apex_sales database and populate it
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Create the database and switch to it
--
CREATE DATABASE apex_sales;
\connect apex_sales;

--
-- Name: assistants; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE assistants (
    assistantid text,
    surname text,
    firstname text,
    middlename text,
    age text,
    grade text,
    phoneno text
);


ALTER TABLE assistants OWNER TO postgres;

--
-- Name: branches; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE branches (
    branchname text,
    branchid text,
    branchcategory text,
    street text,
    city text,
    country text,
    postcode text
);


ALTER TABLE branches OWNER TO postgres;

--
-- Name: items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE items (
    itemid text,
    description text,
    costprice text,
    barcode text,
    supplierid text,
    category text
);


ALTER TABLE items OWNER TO postgres;

--
-- Name: sales; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE sales (
    saleid text,
    amount text,
    itemid text,
    quantity text,
    assistantid text,
    branchname text,
    notes text
);


ALTER TABLE sales OWNER TO postgres;

--
-- Data for Name: assistants; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY assistants (assistantid, surname, firstname, middlename, age, grade, phoneno) FROM stdin;
1222	Citizen	Sean	Tadhg	23	3	871234321
126	Doe	Jane	Donna	56	2	861234567
13213	Svensson	Sven	Lars	72	1	854323456
22	Bloggs	Joe	John	19	3	839877654
33	Yamada	Taro	Nanno	34	3	892344333
343	Mustermann	Max	Fritz	21	2	887655432
\.


--
-- Data for Name: branches; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY branches (branchname, branchid, branchcategory, street, city, country, postcode) FROM stdin;
Al Raqqah	5667	Main	7 April Park	Al Raqqa	Syria	SY.RA.RA
Athlone	99556	Sub	Church Street	Athlone	Ireland	N37 E733
Cork	326	Main	Saint Patrick's Street	Cork	Ireland	T12 TE45
Dallas	323	Main	John West Road	Dallas, TX	USA	75228
Pyongyang	3233	Sub	Sungri Street	Pyongyang	Korea	23060
Tullamore	59	Franchise	O'Moore Street	Tullamore	Ireland	R35 YP55
Vladivostok	7884	Main	Admiral Yumasheva Street	Vladivostok	Russian Federation	690000
\.


--
-- Data for Name: items; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY items (itemid, description, costprice, barcode, supplierid, category) FROM stdin;
1232	Table and Chairs	122.34	233454432	12452	Furniture
1277689	Crested Ten	15.23	45543345	134435	Alcahol
16775645	Marlboro Lights	2.25	13215321	2332	Tobacco
234424	Stool	23.23	2132132	12452	Furniture
3455634	Bed	96.78	123123	13325	Furniture
\.


--
-- Data for Name: sales; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY sales (saleid, amount, itemid, quantity, assistantid, branchname, notes) FROM stdin;
6778392	22.45	234424	10	126	Athlone	Fabulous Sale
7883454	167.89	1277689	2	343	Tullamore	\N
8988516	2445.62	16775645	3545	1222	Cork	\N
10093578	10.11	1277689	5	22	Athlone	Good Product
11198640	233.45	234424	32	13213	Dallas	\N
12303702	8585.74	3455634	435	33	Vladivostok	\N
13408764	55	1232	45	1222	Vladivostok	\N
14513826	955	16775645	2	33	Pyongyang	\N
15618888	788.52	16775645	56	33	Dallas	\N
16723950	9555.12	234424	84	33	Al Raqqah	\N
17829012	8.78	3455634	5	1222	Cork	\N
18934074	1	1232	9	126	Al Raqqah	\N
20039136	0	16775645	9	126	Cork	\N
21144198	28.63	1277689	4515	126	Athlone	\N
22249260	9944.6	234424	4	126	Pyongyang	\N
23354322	8.74	3455634	5	343	Dallas	\N
24459384	8.77	1232	65	1222	Pyongyang	\N
25564446	2722.88	16775645	5	22	Vladivostok	\N
26669508	855.21	16775645	65	13213	Athlone	\N
27774570	644.37	234424	56	33	Tullamore	\N
28879632	633.8	3455634	56	1222	Al Raqqah	\N
29984694	743.74	1232	7787	33	Cork	\N
31089756	144.63	234424	65	33	Cork	\N
32194818	5.7	1277689	89	33	Athlone	\N
33299880	522.36	16775645	8	1222	Dallas	\N
34404942	855.36	1277689	897	126	Al Raqqah	\N
35510004	455.79	234424	5	126	Pyongyang	\N
36615066	1.1	3455634	6	343	Pyongyang	\N
37720128	966.37	1232	2	1222	Vladivostok	\N
38825190	5.27	16775645	2	22	Pyongyang	\N
39930252	444.5	16775645	232	13213	Dallas	\N
41035314	214.59	234424	2	33	Al Raqqah	\N
42140376	6.3	3455634	1	1222	Cork	\N
43245438	455.17	1232	1	33	Al Raqqah	\N
44350500	658.74	16775645	1	33	Cork	\N
45455562	75755.8	1277689	2313	33	Athlone	\N
46560624	0.01	234424	1165	1222	Pyongyang	\N
47665686	855.96	3455634	33	126	Dallas	\N
48770748	44444	1232	3	126	Pyongyang	\N
49875810	8555.6	16775645	3131	343	Vladivostok	\N
50980872	444577.2	16775645	8	1222	Pyongyang	\N
52085934	8277	234424	1	22	Dallas	\N
53190996	987.65	3455634	561	13213	Al Raqqah	\N
54296058	123.54	1232	6551	33	Cork	\N
\.


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

