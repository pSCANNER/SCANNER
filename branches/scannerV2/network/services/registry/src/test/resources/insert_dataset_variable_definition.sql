--
-- PostgreSQL database dump
--

-- Dumped from database version 9.2.4
-- Dumped by pg_dump version 9.2.4
-- Started on 2013-08-21 19:45:36

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = scanner_registry, pg_catalog;

--
-- TOC entry 2037 (class 0 OID 0)
-- Dependencies: 202
-- Name: data_set_variable_specificati_data_set_variable_specificati_seq; Type: SEQUENCE SET; Schema: scanner_registry; Owner: postgres
--

SELECT pg_catalog.setval('data_set_variable_specificati_data_set_variable_specificati_seq', 33, true);


--
-- TOC entry 2028 (class 0 OID 17141)
-- Dependencies: 203
-- Data for Name: data_set_variable_specification; Type: TABLE DATA; Schema: scanner_registry; Owner: postgres
--
---JUST WANT TO MAKE SURE I CAN GET THIS INTO SVN WITHOUT SCREWING THE REST OF THE TREE UP
CREATE TABLE scanner_registry.data_set_variable_specification
(
  data_set_variable_specification_id integer NOT NULL DEFAULT nextval('scanner_registry.data_set_variable_specificati_data_set_variable_specificati_seq'::regclass),
  data_set_definition integer NOT NULL,
  variable_name text NOT NULL, -- variable name is unique wihtin data set
  variable_description text, -- this is the tooltip and description of the variable
  variable_type text,
  variable_gui text,
  CONSTRAINT data_set_variable_specification_pkey PRIMARY KEY (data_set_variable_specification_id),
  CONSTRAINT data_set_variable_specification_data_set_definition_fkey FOREIGN KEY (data_set_definition)
      REFERENCES scanner_registry.data_set_definition (data_set_definition_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE scanner_registry.data_set_variable_specification
  OWNER TO postgres;
COMMENT ON TABLE scanner_registry.data_set_variable_specification
  IS 'this is information about the data set that can be shared across multiple studies and analysis instances';
COMMENT ON COLUMN scanner_registry.data_set_variable_specification.variable_name IS 'variable name is unique wihtin data set';
COMMENT ON COLUMN scanner_registry.data_set_variable_specification.variable_description IS 'this is the tooltip and description of the variable';



INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (2, 1, 'group', 'group', 'binary', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (3, 1, 'male_gender', 'male', 'binary', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (4, 1, 'ethnicity', 'ethnicity', 'binary', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (5, 1, 'age', 'age', 'integer', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (6, 1, 'smoke', 'smoking history', 'binary', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (7, 1, 'insurance', 'insurance status ', 'binary', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (8, 1, 'a1c_baseline', 'HbA1c at baseline', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (9, 1, 'sybp_baseline', 'Systolic BP at baseline', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (10, 1, 'dibp_baseline', 'Diastolic BP at baseline', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (11, 1, 'ldl_baseline', 'LDL at basleine', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (12, 1, 'totchol_baseline', 'Total cholesterol baseline measurement', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (13, 1, 'trig_baseline', 'Triglycerides baseline measurement', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (14, 1, 'hdl_baseline', 'HDL baseline measurement', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (15, 1, 'bmi_baseline', 'BMI baseline', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (16, 1, 'date_of_initial_visit_with_CP', 'date of initial visit with CP', 'date', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (17, 1, 'dt_init_A1c', 'date of initial visit with MD where A1C > 9%', 'date', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (18, 1, 'CP_id_for_initial_visit_with_CP', 'CP id for initial visit with CP', 'text', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (19, 1, 'MD_id_for_initial_visit_with_MD', 'MD id for initial visit with MD', 'text', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (20, 1, 'days_between_index_date_and_final_measure_date', 'days between index date and final measure date', 'integer', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (21, 1, 'date_of_final_measure', 'date of final measure', 'date', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (22, 1, 'MD_id_for_final_measure', 'MD id for final measure', 'text', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (23, 1, 'number_of_visits', 'number of visits', 'integer', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (24, 1, 'a1c_final', 'a1c final', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (25, 1, 'bmi_final', 'bmi final', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (26, 1, 'sybp_final', 'sybp final', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (27, 1, 'dibp_final', 'dibp final', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (28, 1, 'ldl_final', 'ldl final', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (29, 1, 'totchol_final', 'totchol final', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (30, 1, 'trig_final', 'trig final', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (31, 1, 'hdl_final', 'hdl final', 'continuous', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (32, 1, 'Tx', 'Tx', 'binary', NULL);
INSERT INTO data_set_variable_specification (data_set_variable_specification_id, data_set_definition, variable_name, variable_description, variable_type, variable_gui) VALUES (33, 1, 'outcome_a1c_final_lt_9', '1 if final A1C is less than 9', 'binary', NULL);


SET default_tablespace = '';

--
-- TOC entry 2025 (class 2606 OID 17149)
-- Name: data_set_variable_specification_pkey; Type: CONSTRAINT; Schema: scanner_registry; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_set_variable_specification
    ADD CONSTRAINT data_set_variable_specification_pkey PRIMARY KEY (data_set_variable_specification_id);


--
-- TOC entry 2026 (class 2606 OID 17150)
-- Name: data_set_variable_specification_data_set_definition_fkey; Type: FK CONSTRAINT; Schema: scanner_registry; Owner: postgres
--

ALTER TABLE ONLY data_set_variable_specification
    ADD CONSTRAINT data_set_variable_specification_data_set_definition_fkey FOREIGN KEY (data_set_definition) REFERENCES data_set_definition(data_set_definition_id);


-- Completed on 2013-08-21 19:45:36

--
-- PostgreSQL database dump complete
--

