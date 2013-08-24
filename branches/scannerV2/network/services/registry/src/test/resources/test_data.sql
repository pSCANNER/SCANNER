
set search_path = scanner_registry;

INSERT into site (site_name, description) VALUES
('ISI', 'USC Information Sciences Institute');
 
INSERT INTO node (site_id,host_url, host_port, base_path, description, is_master) VALUES
(1, 'https://scanner.misd.isi.edu', 9998, '/scanner/query/', 'SCANNER MASTER', true),
(1, 'https://scanner-node1.misd.isi.edu', 8888, '/scanner/dataset/','ALTAMED SIM', false),
(1, 'https://scanner-node2.misd.isi.edu', 8888, '/scanner/dataset/', 'RAND SIM', false),
(1, 'https://scanner-node3.misd.isi.edu', 8888, '/scanner/dataset/', 'UCSD SIM', false);

INSERT INTO scanner_user (user_name, email, HSPC_documents, Phone, Reports_To, Active, First_Name, Middle_Initial, Last_Name, PubMed_Author_ID, is_superuser) VALUES
('dmeeker', 'dmeeker@rand.org', null, '2139262658', 0, true, 'Daniella', 'EP', 'Meeker', NULL, true),
('laura', 'laura@isi.edu', null, null, 0, true, 'Laura', NULL, 'Pearlman', NULL, true),
('mdarcy', 'mdarcy@isi.edu', null, null, 0, true, 'Mike', NULL, 'D''Arcy', NULL, true),
('serban', 'serban@isi.edu', null, null, 0, true, 'Serban', NULL, 'Voinea', NULL, true);

INSERT INTO study (study_name, description, IRB_ID, Protocol, Principal_Investigator_UID, Start_Date, End_Date, Clinical_Trials_ID, Analysis_Plan, study_status_type_id) VALUES
('MTM', 'Medication Therapy Management', 1, 'Analyze MTM data on multiple sites with both OCEANS and GLORE', 1, '2013-08-15', '2013-08-15', 0, 'Very Important Analysis on Multiple Sites',1),
('BEARI', 'Behavioral Economics for Acute Respitory Infections', 2, 'Analyze BEARI data on multiple ISI VMs with both OCEANS and GLORE', 1, '2013-08-15', '2013-08-15', 0, 'Determine if perfomrance is contingent on principal diagnosis',1);

INSERT INTO study_role (study_id,role_within_study) VALUES
(1,'Principle Investigator'),
(1,'Site Administrator'),
(1,'Investigator'),
(2,'Principle Investigator'),
(2,'Site Administrator'),
(2,'Investigator');

INSERT INTO user_role(user_id, role_id) VALUES
(1, 1),
(2, 3),
(3, 3),
(1, 4),
(2, 6),
(4, 1),
(4, 4);

INSERT INTO tool_library (library_name, version, description) VALUES
('OCEANS', '1', 'Executes Meta-Regression Across Multiple Sites'),
('GLORE', '1', 'Executes Virtually Pooled Analysis Across Multiple Sites');

INSERT INTO analysis_tool (tool_name, tool_path, Tool_Parent_Library_ID, Tool_Description, Input_Format_Specifications, Output_Format_Specifications, Information_Email) VALUES
('Logistic Regression', '/oceans/lr', 1, 'Meta-regression with logit', '', '', 'michael.matheny@vanderbilt.edu'),
('Logistic Regression', '/glore/lr', 2, 'Virtually pooled logistic regression ', '', '', 'x1jiang@ucsd.edu');

INSERT INTO data_set_definition (data_set_name, description, Data_Processing_XML, Data_Processing_Program, Author_UID, Originating_Study_ID, Data_Set_Confidentiality_Level) VALUES
('MTM-1', 'MTM Sample Dataset 1', null, null, 1, 1, 100),
('BEARI-1', 'BEARI Sample Dataset 1', null, null, 2, 2, 100);

INSERT INTO data_set_instance ( Data_Set_Definition_ID, data_set_instance_name, description, node_id, data_source) VALUES
(1, 'MTM Altamed', 'MTM Simulation data generated by Altamed', 2, 'MTM_SIMULATED_ALTAMED.csv'),
(1, 'MTM RAND', 'MTM Simulation data generated by RAND', 3, 'MTM_SIMULATED_RAND.csv'),
(1, 'MTM UCSD', 'MTM Simulation data generated by UCSD', 4, 'MTM_SIMULATED_UCSD.csv');

INSERT INTO study_policy_statement (study_id, data_set_definition_id, policy_authority, policy_originator, attestation, role_id, analysis_tool_id, access_mode, policy_status_id) VALUES
(1, 1, 400, 1, ' DataSetDefinition 1 will be analyzed with with GLORE Logistic Regression in a mode without approval before transfer of aggregate/patient-level data at node X.  ', 1, 2, 0, 0),
(1, 1, 400, 1, ' DataSetDefinition 1 will be analyzed with with OCEANS Logistic Regression in a mode with approval before transfer of aggregate/patient-level data at node X.  ', 1, 1, 1, 0),
(1, 1, 400, 1, ' DataSetDefinition 1 will be analyzed with with OCEANS Logistic Regression in a mode without approval before transfer of aggregate/patient-level data at node X.  ', 1, 1, 0, 0);

INSERT INTO policy_statement (data_set_instance_id, role_id, analysis_tool_id, access_mode_id, policy_status_id, parent_study_policy_statement_id) VALUES
(1,1,1,1,0,1),
(2,1,1,1,0,1),
(3,1,1,1,0,1),
(1,1,2,1,0,2),
(2,1,2,1,0,2),
(3,1,2,1,0,2);

INSERT INTO data_set_variable_metadata (data_set_definition, variable_name, variable_description, variable_type, variable_options) VALUES
(1, 'group', 'group', 'binary', NULL),
(1, 'male', 'male', 'binary', NULL),
(1, 'eth', 'ethnicity', 'binary', NULL),
(1, 'age', 'age', 'integer', NULL),
(1, 'smoke', 'smoking history', 'binary', NULL),
(1, 'categorical_insurance', 'insurance status ', 'binary', NULL),
(1, 'a1c baseline', 'HbA1c at baseline', 'continuous', NULL),
(1, 'sybp baseline', 'Systolic BP at baseline', 'continuous', NULL),
(1, 'dibp baseline', 'Diastolic BP at baseline', 'continuous', NULL),
(1, 'ldl baseline', 'LDL at basleine', 'continuous', NULL),
(1, 'totchol baseline', 'Total cholesterol baseline measurement', 'continuous', NULL),
(1, 'trig baseline', 'Triglycerides baseline measurement', 'continuous', NULL),
(1, 'hdl baseline', 'HDL baseline measurement', 'continuous', NULL),
(1, 'bmi baseline', 'BMI baseline', 'continuous', NULL),
(1, 'days between index date and final measure date', 'days between index date and final measure date', 'integer', NULL),
(1, 'number of visits', 'number of visits', 'integer', NULL),
(1, 'a1c final', 'a1c final', 'continuous', NULL),
(1, 'bmi final', 'bmi final', 'continuous', NULL),
(1, 'sybp final', 'sybp final', 'continuous', NULL),
(1, 'dibp_final', 'dibp final', 'continuous', NULL),
(1, 'ldl final', 'ldl final', 'continuous', NULL),
(1, 'totchol final', 'totchol final', 'continuous', NULL),
(1, 'trig final', 'trig final', 'continuous', NULL),
(1, 'hdl final', 'hdl final', 'continuous', NULL),
(1, 'outcome_a1c_final_lt_9', '1 if final A1C is less than 9', 'binary', NULL);
