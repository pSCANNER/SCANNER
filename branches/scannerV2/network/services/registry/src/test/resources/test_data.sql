
set search_path = scanner_registry;

INSERT into site (site_name) VALUES
('ISI');
 
INSERT INTO node (site_id,host_url, host_port, base_path, description, is_master) VALUES
(1, 'https://scanner.misd.isi.edu', 8888, '/scanner/query', 'SCANNER MASTER', true),
(1, 'https://scanner-node1.misd.isi.edu', 8888, '/scanner/dataset','ALTAMED SIM', false),
(1, 'https://scanner-node2.misd.isi.edu', 8888, '/scanner/dataset', 'RAND SIM', false),
(1, 'https://scanner-node3.misd.isi.edu', 8888, '/scanner/dataset', 'UCSD SIM', false);

INSERT INTO scanner_user (user_name, email, HSPC_documents, Phone, Reports_To, Active, First_Name, Middle_Initial, Last_Name, PubMed_Author_ID, is_superuser) VALUES
('dmeeker', 'dmeeker@rand.org', '', '2139262658', 0, true, 'Daniella', 'EP', 'Meeker', NULL, true),
('laura', 'laura@isi.edu', '', '', 0, true, 'Laura', NULL, 'Pearlman', NULL, true),
('mdarcy', 'mdarcy@isi.edu', '', '', 0, true, 'Mike', NULL, 'D''Arcy', NULL, true);

INSERT INTO study (study_name,IRB_ID, Protocol, Principal_Investigator_UID, Start_Date, End_Date, Clinical_Trials_ID, Analysis_Plan, study_status_type_id) VALUES
('MTM', 1, 'Analyze MTM data on multiple sites with both OCEANS and GLORE', 1, '2013-08-15', '2013-08-15', 0, 'Very Important Analysis on Multiple Sites',1),
('BEARI', 2, 'Analyze BEARI data on multiple ISI VMs with both OCEANS and GLORE', 1, '2013-08-15', '2013-08-15', 0, 'Determine if perfomrance is contingent on principal diagnosis',1);

INSERT INTO scanner_role (study_id,role_within_study) VALUES
(1,'developer');

INSERT INTO investigator_role(investigator_id, role_id) VALUES
(1, 1),
(2, 1),
(3, 1);

INSERT INTO tool_library (library_name, version, description) VALUES
('GLORE', '1', 'Executes Virtually Pooled Analysis Across Multiple Sites'),
('OCEANS', '1', 'Executes meta-regression across multple sites');

INSERT INTO analysis_tool (tool_name, Tool_Parent_Library_ID, Tool_Description, Input_Format_Specifications, Output_Format_Specifications, Curator_UID, Information_Email) VALUES
('lr', 1, 'Virtually pooled logistic regression ', '', '', 0, 'x1jiang@ucsd.edu'),
('lr', 2, 'Meta-regression with logit', '', '', 0, 'michael.matheny@vanderbilt.edu');

INSERT INTO data_set_definition (Data_Description_XML, Data_Processing_XML, Data_Processing_Program, Author_UID, Originating_Study_ID, Data_Set_Confidentiality_Level) VALUES
('', '', '', 1, 1, 100),
('', '', '', 2, 2, 100);

INSERT INTO abstract_policy (study_id, data_set_definition_id, policy_authority, policy_originator, attestation, role_id, analysis_tool_id, access_mode, policy_status_id) VALUES
(1, 1, 400, 1, ' Data Set Instance X of DataSetDefinition 1 will be analyzed with with GLORE logistic regression in a mode with approval before transfer of aggregate/patient-level data at node X.  ', 1, 1, 0, 1),
(1, 1, 400, 1, ' Data Set Instance X of DataSetDefinition 1 will be analyzed with with GLORE logistic regression in a mode without approval before transfer of aggregate/patient-level data at node X.  ', 1, 1, 0, 1);

INSERT INTO data_set_instance (Data_Set_Definition_ID, node_id, data_set_instance_location, Curator_UID, Study_ID, Source_Data_Warehouse_ID, data_slice_id) VALUES
(1, 2, 'MTM_SIMULATED_ALTAMED.csv', 1, 1, NULL, NULL),
(1, 3, 'MTM_SIMULATED_RAND.csv', 3, 1, NULL, NULL),
(1, 4, 'MTM_SIMULATED_UCSD.csv', 2, 1, NULL, NULL);