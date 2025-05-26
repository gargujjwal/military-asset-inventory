--
-- PostgreSQL database dump
--

-- Dumped from database version 16.9 (Debian 16.9-1.pgdg120+1)
-- Dumped by pg_dump version 16.9 (Debian 16.9-1.pgdg120+1)

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
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: assignment_transactions; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.assignment_transactions (
    quantity_assigned integer NOT NULL,
    quantity_expended integer NOT NULL,
    assigned_to character varying(255) NOT NULL,
    id character varying(255) NOT NULL,
    notes character varying(255)
);


ALTER TABLE public.assignment_transactions OWNER TO devuser;

--
-- Name: audit_log; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.audit_log (
    quantity_changed integer NOT NULL,
    "timestamp" timestamp(6) without time zone NOT NULL,
    action character varying(255) NOT NULL,
    done_by character varying(255) NOT NULL,
    equipment_name character varying(255) NOT NULL,
    id character varying(255) NOT NULL,
    transaction_type character varying(255) NOT NULL,
    CONSTRAINT audit_log_transaction_type_check CHECK (((transaction_type)::text = ANY ((ARRAY['ASSIGNMENT'::character varying, 'PURCHASE'::character varying, 'TRANSFER'::character varying, 'EXPENDITURE'::character varying])::text[])))
);


ALTER TABLE public.audit_log OWNER TO devuser;

--
-- Name: bases; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.bases (
    created_at timestamp(6) without time zone NOT NULL,
    id character varying(255) NOT NULL,
    location character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.bases OWNER TO devuser;

--
-- Name: equipment_categories; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.equipment_categories (
    description character varying(255),
    id character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    unit_of_measure character varying(255) NOT NULL
);


ALTER TABLE public.equipment_categories OWNER TO devuser;

--
-- Name: equipment_inventories; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.equipment_inventories (
    closing_balance integer NOT NULL,
    opening_balance integer NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    last_updated timestamp(6) without time zone NOT NULL,
    base_id character varying(255) NOT NULL,
    equipment_id character varying(255) NOT NULL,
    id character varying(255) NOT NULL
);


ALTER TABLE public.equipment_inventories OWNER TO devuser;

--
-- Name: equipments; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.equipments (
    description character varying(255) NOT NULL,
    equipment_category_id character varying(255),
    id character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.equipments OWNER TO devuser;

--
-- Name: expenditure_transactions; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.expenditure_transactions (
    description character varying(255),
    id character varying(255) NOT NULL,
    reason character varying(255) NOT NULL
);


ALTER TABLE public.expenditure_transactions OWNER TO devuser;

--
-- Name: inventory_transactions; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.inventory_transactions (
    quantity_change integer NOT NULL,
    resulting_balance integer NOT NULL,
    transaction_date timestamp(6) without time zone NOT NULL,
    transaction_type character varying(31) NOT NULL,
    id character varying(255) NOT NULL,
    inventory_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL
);


ALTER TABLE public.inventory_transactions OWNER TO devuser;

--
-- Name: purchase_transactions; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.purchase_transactions (
    id character varying(255) NOT NULL,
    notes character varying(255),
    vendor_name character varying(255) NOT NULL
);


ALTER TABLE public.purchase_transactions OWNER TO devuser;

--
-- Name: transfer_transactions; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.transfer_transactions (
    dest_base_id character varying(255) NOT NULL,
    id character varying(255) NOT NULL,
    inverse_transaction_id character varying(255),
    notes character varying(255),
    source_base_id character varying(255) NOT NULL
);


ALTER TABLE public.transfer_transactions OWNER TO devuser;

--
-- Name: user_base_assignments; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.user_base_assignments (
    is_active boolean NOT NULL,
    assignment_date timestamp(6) without time zone NOT NULL,
    base_id character varying(255),
    id character varying(255) NOT NULL,
    user_id character varying(255)
);


ALTER TABLE public.user_base_assignments OWNER TO devuser;

--
-- Name: users; Type: TABLE; Schema: public; Owner: devuser
--

CREATE TABLE public.users (
    created_at timestamp(6) without time zone NOT NULL,
    full_name character varying(255) NOT NULL,
    id character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    role character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'BASE_COMMANDER'::character varying, 'LOGISTICS_OFFICER'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO devuser;

--
-- Data for Name: assignment_transactions; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.assignment_transactions (quantity_assigned, quantity_expended, assigned_to, id, notes) FROM stdin;
5	5	Ram	202af131-0cc2-4790-af39-2e0e4ad769da	
\.


--
-- Data for Name: audit_log; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.audit_log (quantity_changed, "timestamp", action, done_by, equipment_name, id, transaction_type) FROM stdin;
1000	2025-05-25 23:25:59.243427	CREATE	Default Admin	14b4631f-6ff9-4c0f-97ca-e94d34783247	2fa987e3-7d8d-4314-8bc8-7b45959972e7	PURCHASE
100	2025-05-25 23:26:37.417109	CREATE	Default Admin	5619b7a7-eab8-4393-8b0b-b06de5b36264	5aba0835-995e-4224-83b0-2261d7b415ea	PURCHASE
1000	2025-05-25 23:27:17.687526	CREATE	Default Admin	0edcfe63-ef43-4274-b205-079cc35a7627	2756ef33-284a-48b4-8cb2-a2c03f168dc5	PURCHASE
-100	2025-05-25 23:27:43.125504	CREATE	Default Admin	AK-47 Rifle	5dcb8ba0-4802-4f78-8fe9-1432d16ed7f1	EXPENDITURE
-5	2025-05-25 23:28:33.932986	CREATE	Default Admin	Jeep	098ece6d-5bca-4d12-bb3a-b6b64e3be313	ASSIGNMENT
\.


--
-- Data for Name: bases; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.bases (created_at, id, location, name) FROM stdin;
2025-05-25 23:22:48.792028	bb96f520-efe7-4258-8266-b52e231f3a34	New Delhi	Base Alpha
2025-05-25 23:22:48.792028	c5641d60-1e76-4b61-a577-c3a8b2a758c8	Mumbai	Base Bravo
2025-05-25 23:23:50.357103	90016105-470e-4c2b-8937-475e1ac94618	India	Fort Alpha
\.


--
-- Data for Name: equipment_categories; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.equipment_categories (description, id, name, unit_of_measure) FROM stdin;
Firearms and Ammunition	e2a62115-8af6-4ba5-9a0a-9c192e8e86ac	Weapons	units
Radios and Signal Equipments	3f2b849c-c841-4d74-95f7-b8d8baa94840	Communication Devices	pieces
Bandages, First Aid Kits	30536337-53fa-430a-b964-123dbad8a8fa	Medical Supplies	packs
helps in transportation	af29dbf2-9467-4c94-95e0-45f91c8689b4	Vehicle	unit
\.


--
-- Data for Name: equipment_inventories; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.equipment_inventories (closing_balance, opening_balance, created_at, last_updated, base_id, equipment_id, id) FROM stdin;
1000	1000	2025-05-25 23:27:17.685744	2025-05-25 23:27:17.685754	c5641d60-1e76-4b61-a577-c3a8b2a758c8	0edcfe63-ef43-4274-b205-079cc35a7627	f34611ee-2799-4f04-a627-3e15a041d7cd
900	1000	2025-05-25 23:25:59.239157	2025-05-25 23:27:43.126213	bb96f520-efe7-4258-8266-b52e231f3a34	14b4631f-6ff9-4c0f-97ca-e94d34783247	0d286c50-9a1e-4f57-9021-975e395264b5
95	100	2025-05-25 23:26:37.41506	2025-05-25 23:28:33.934977	bb96f520-efe7-4258-8266-b52e231f3a34	5619b7a7-eab8-4393-8b0b-b06de5b36264	5a0872f3-5fd9-4374-9fb0-d2ced249cb28
\.


--
-- Data for Name: equipments; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.equipments (description, equipment_category_id, id, name) FROM stdin;
Automatic rifle used in combat	e2a62115-8af6-4ba5-9a0a-9c192e8e86ac	14b4631f-6ff9-4c0f-97ca-e94d34783247	AK-47 Rifle
Portable radio device	3f2b849c-c841-4d74-95f7-b8d8baa94840	8725db96-42c4-4eed-a3ea-84cbefb29860	Motorola Radio
Medical emergency supplies	30536337-53fa-430a-b964-123dbad8a8fa	0edcfe63-ef43-4274-b205-079cc35a7627	First Aid Kit
good vehicle	af29dbf2-9467-4c94-95e0-45f91c8689b4	5619b7a7-eab8-4393-8b0b-b06de5b36264	Jeep
\.


--
-- Data for Name: expenditure_transactions; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.expenditure_transactions (description, id, reason) FROM stdin;
	6fb87139-c5c2-41bd-8f47-0039f0bf628b	Mission
\.


--
-- Data for Name: inventory_transactions; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.inventory_transactions (quantity_change, resulting_balance, transaction_date, transaction_type, id, inventory_id, user_id) FROM stdin;
1000	1000	2025-05-25 23:25:59.241292	PURCHASE	eda63fdf-6ebe-4933-9254-d2781f1f1f74	0d286c50-9a1e-4f57-9021-975e395264b5	ac3d473b-9614-4228-82b3-e1f7765ffd24
100	100	2025-05-25 23:26:37.416188	PURCHASE	ec918e44-f88c-43c6-8916-ca0407cd65af	5a0872f3-5fd9-4374-9fb0-d2ced249cb28	ac3d473b-9614-4228-82b3-e1f7765ffd24
1000	1000	2025-05-25 23:27:17.686458	PURCHASE	07205b26-609d-4638-a798-552284f84092	f34611ee-2799-4f04-a627-3e15a041d7cd	ac3d473b-9614-4228-82b3-e1f7765ffd24
-100	900	2025-05-25 23:27:43.122869	EXPENDITURE	6fb87139-c5c2-41bd-8f47-0039f0bf628b	0d286c50-9a1e-4f57-9021-975e395264b5	ac3d473b-9614-4228-82b3-e1f7765ffd24
-5	95	2025-05-25 23:28:33.930754	ASSIGNMENT	202af131-0cc2-4790-af39-2e0e4ad769da	5a0872f3-5fd9-4374-9fb0-d2ced249cb28	ac3d473b-9614-4228-82b3-e1f7765ffd24
\.


--
-- Data for Name: purchase_transactions; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.purchase_transactions (id, notes, vendor_name) FROM stdin;
eda63fdf-6ebe-4933-9254-d2781f1f1f74		Ujjwal
ec918e44-f88c-43c6-8916-ca0407cd65af	\N	Ujjwal
07205b26-609d-4638-a798-552284f84092	\N	Ujjwal
\.


--
-- Data for Name: transfer_transactions; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.transfer_transactions (dest_base_id, id, inverse_transaction_id, notes, source_base_id) FROM stdin;
\.


--
-- Data for Name: user_base_assignments; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.user_base_assignments (is_active, assignment_date, base_id, id, user_id) FROM stdin;
t	2025-05-25 23:25:31.816738	bb96f520-efe7-4258-8266-b52e231f3a34	fb52be52-7356-4a22-8877-61e4a95b9ab5	f0fc59e7-abee-44cc-b205-36f1abb99656
t	2025-05-25 23:25:39.971793	bb96f520-efe7-4258-8266-b52e231f3a34	e15ea3fa-3650-4411-a3c4-8126862538bf	1eaf6516-68d4-4b3b-9e22-e6130c0cd47a
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: devuser
--

COPY public.users (created_at, full_name, id, password_hash, role, username) FROM stdin;
2025-05-25 23:22:48.78385	Default Admin	ac3d473b-9614-4228-82b3-e1f7765ffd24	$2y$10$2WGq2rtCp/Q3u2WkD6z2Q.AWe6Y.iOvPMxISriPgw73WjmPX/PL3O	ADMIN	admin
2025-05-25 23:25:07.331439	commander	f0fc59e7-abee-44cc-b205-36f1abb99656	$2a$10$9zbKUSDwDxrmxmZmXFJt.Oi5x.z520Jn93X6EDwAtr3eO2u5hR7lW	BASE_COMMANDER	commander
2025-05-25 23:25:16.63735	officer	1eaf6516-68d4-4b3b-9e22-e6130c0cd47a	$2a$10$Sp1whKzh65uo.I/xYr..j.d.4yppEIPy959strTE2452ztb7I/nq6	LOGISTICS_OFFICER	officer
\.


--
-- Name: assignment_transactions assignment_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.assignment_transactions
    ADD CONSTRAINT assignment_transactions_pkey PRIMARY KEY (id);


--
-- Name: audit_log audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.audit_log
    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (id);


--
-- Name: bases bases_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.bases
    ADD CONSTRAINT bases_pkey PRIMARY KEY (id);


--
-- Name: equipment_categories equipment_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.equipment_categories
    ADD CONSTRAINT equipment_categories_pkey PRIMARY KEY (id);


--
-- Name: equipment_inventories equipment_inventories_base_id_equipment_id_key; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.equipment_inventories
    ADD CONSTRAINT equipment_inventories_base_id_equipment_id_key UNIQUE (base_id, equipment_id);


--
-- Name: equipment_inventories equipment_inventories_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.equipment_inventories
    ADD CONSTRAINT equipment_inventories_pkey PRIMARY KEY (id);


--
-- Name: equipments equipments_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.equipments
    ADD CONSTRAINT equipments_pkey PRIMARY KEY (id);


--
-- Name: expenditure_transactions expenditure_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.expenditure_transactions
    ADD CONSTRAINT expenditure_transactions_pkey PRIMARY KEY (id);


--
-- Name: inventory_transactions inventory_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.inventory_transactions
    ADD CONSTRAINT inventory_transactions_pkey PRIMARY KEY (id);


--
-- Name: purchase_transactions purchase_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.purchase_transactions
    ADD CONSTRAINT purchase_transactions_pkey PRIMARY KEY (id);


--
-- Name: transfer_transactions transfer_transactions_inverse_transaction_id_key; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.transfer_transactions
    ADD CONSTRAINT transfer_transactions_inverse_transaction_id_key UNIQUE (inverse_transaction_id);


--
-- Name: transfer_transactions transfer_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.transfer_transactions
    ADD CONSTRAINT transfer_transactions_pkey PRIMARY KEY (id);


--
-- Name: user_base_assignments user_base_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.user_base_assignments
    ADD CONSTRAINT user_base_assignments_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: expenditure_transactions fk3n3hinonq6xdoq58pe5343u54; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.expenditure_transactions
    ADD CONSTRAINT fk3n3hinonq6xdoq58pe5343u54 FOREIGN KEY (id) REFERENCES public.inventory_transactions(id);


--
-- Name: user_base_assignments fk66d4hggdqha4p1mq4otbju0c0; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.user_base_assignments
    ADD CONSTRAINT fk66d4hggdqha4p1mq4otbju0c0 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: inventory_transactions fk8cm8f2fdpwsk02joqyuhrfrvk; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.inventory_transactions
    ADD CONSTRAINT fk8cm8f2fdpwsk02joqyuhrfrvk FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: transfer_transactions fk986yeqnlqyybar7irhck2yhhd; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.transfer_transactions
    ADD CONSTRAINT fk986yeqnlqyybar7irhck2yhhd FOREIGN KEY (dest_base_id) REFERENCES public.bases(id);


--
-- Name: purchase_transactions fkaa81dendlqnigcukfs9mh6i0a; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.purchase_transactions
    ADD CONSTRAINT fkaa81dendlqnigcukfs9mh6i0a FOREIGN KEY (id) REFERENCES public.inventory_transactions(id);


--
-- Name: assignment_transactions fkccakfnd11hy8yobvt490p6tdt; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.assignment_transactions
    ADD CONSTRAINT fkccakfnd11hy8yobvt490p6tdt FOREIGN KEY (id) REFERENCES public.inventory_transactions(id);


--
-- Name: transfer_transactions fkcr4134m3hv25x28mk861adh88; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.transfer_transactions
    ADD CONSTRAINT fkcr4134m3hv25x28mk861adh88 FOREIGN KEY (inverse_transaction_id) REFERENCES public.transfer_transactions(id);


--
-- Name: user_base_assignments fkf6pguy5cijx66v6mgvwyq44bu; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.user_base_assignments
    ADD CONSTRAINT fkf6pguy5cijx66v6mgvwyq44bu FOREIGN KEY (base_id) REFERENCES public.bases(id);


--
-- Name: inventory_transactions fkk2abhuhs1ymtrx3s0dabt9n62; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.inventory_transactions
    ADD CONSTRAINT fkk2abhuhs1ymtrx3s0dabt9n62 FOREIGN KEY (inventory_id) REFERENCES public.equipment_inventories(id);


--
-- Name: equipments fkk344q901aodl4ql3tp9mb0257; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.equipments
    ADD CONSTRAINT fkk344q901aodl4ql3tp9mb0257 FOREIGN KEY (equipment_category_id) REFERENCES public.equipment_categories(id);


--
-- Name: equipment_inventories fkkjex8ybda24id70ofm6fr3qj5; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.equipment_inventories
    ADD CONSTRAINT fkkjex8ybda24id70ofm6fr3qj5 FOREIGN KEY (equipment_id) REFERENCES public.equipments(id);


--
-- Name: transfer_transactions fkn535fevhjoluvwwkjnx3gjpht; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.transfer_transactions
    ADD CONSTRAINT fkn535fevhjoluvwwkjnx3gjpht FOREIGN KEY (source_base_id) REFERENCES public.bases(id);


--
-- Name: equipment_inventories fknktpgdvbha0fr0ehrp1wblwc2; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.equipment_inventories
    ADD CONSTRAINT fknktpgdvbha0fr0ehrp1wblwc2 FOREIGN KEY (base_id) REFERENCES public.bases(id);


--
-- Name: transfer_transactions fknu2xams250b0o34wao04kxsqg; Type: FK CONSTRAINT; Schema: public; Owner: devuser
--

ALTER TABLE ONLY public.transfer_transactions
    ADD CONSTRAINT fknu2xams250b0o34wao04kxsqg FOREIGN KEY (id) REFERENCES public.inventory_transactions(id);


--
-- PostgreSQL database dump complete
--

