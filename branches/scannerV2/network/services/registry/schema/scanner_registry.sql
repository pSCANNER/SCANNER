create schema scanner_registry;
set search_path = scanner_registry;

CREATE TABLE IF NOT EXISTS analysis_tools (
  Tool_ID integer NOT NULL,
  Tool_Parent_Library_ID integer NOT NULL,
  Tool_Description text NOT NULL,
  Input_Format_Specifications text NOT NULL,
  Output_Format_Specidications text NOT NULL,
  Curator_UID integer NOT NULL,
  Information_Email text NOT NULL,
  PRIMARY KEY (Tool_ID)
);


CREATE TABLE IF NOT EXISTS data_set_definition (
  Data_Set_Definition_ID integer NOT NULL,
  Data_Description_XML text NOT NULL,
  Data_Processing_XML text NOT NULL,
  Data_Processing_Program text NOT NULL,
  Author_UID integer NOT NULL,
  Originating_Study_ID integer NOT NULL,
  Data_Set_Regulation_Type integer NOT NULL,
  PRIMARY KEY (Data_Set_Definition_ID)
);

COMMENT on column data_set_definition.data_description_xml is 'Daniella: Path to XML describing data';
COMMENT on column data_set_definition.data_processing_xml is 'Daniella: Path to XML for data processing specifications';
COMMENT on column data_set_definition.data_processing_program is 'Daniella: Path to SQL or other data processing program';
COMMENT on column data_set_definition.author_uid is 'Daniella: UID of author';
COMMENT on column data_set_definition.originating_study_id is 'Daniella: ID of study using this data set';
COMMENT on column data_set_definition.data_set_regulation_type is 'Daniella: This is a key to the type of legal regulations this data set is subject to (safe harbor, limited data set, identified data)';

CREATE TABLE IF NOT EXISTS data_set_instance (
  Data_Set_Instance_ID integer NOT NULL,
  Data_Set_Definition_ID integer NOT NULL,
  Data_Set_Instance_Path text NOT NULL,
  Curator_UID integer NOT NULL,
  Study_ID integer NOT NULL,
  Source_Data_Warehouse_ID integer NOT NULL,
  Data_Slice_ID integer DEFAULT NULL,
  PRIMARY KEY (Data_Set_Instance_ID)
);


CREATE TABLE IF NOT EXISTS dua_study(
  DUA_ID integer NOT NULL,
  Study_ID integer NOT NULL,
  primary key (DUA_ID,Study_ID)
);

COMMENT on TABLE dua_study is 'Daniella: Table linking the DUA to the study';


CREATE TABLE IF NOT EXISTS investigator_study (
  Study_ID integer NOT NULL,
  Investigator_ID integer NOT NULL,
  Role integer NOT NULL,
  primary key (Study_ID,Investigator_ID,Role)
);

COMMENT on column investigator_study.role is 'PI, CO-I, PM, etc.';

CREATE TABLE IF NOT EXISTS policy_registry (
  Policy_ID integer NOT NULL,
  Authority_Type text not null check (authority_type in ('LEGAL','STUDY','SITE ADMINISTRATION','NETWORK ADMINISTRATION')),
  Resource_Type_Governed text not null check (resource_type_governed in ('DATA SET INSTANCE','METHOD')),
  Assertion text NOT NULL,
  Data_Resource_Instance_Resource_ID integer NOT NULL,
  Data_Resource_Definition_ID integer NOT NULL,
  Data_Resource_Legal_Type text not null check (data_resource_legal_type in ('SAFE HARBOR','DUA-COVERED LIMITED DATA SET','IDENTIFIED DATA')),
  Method_Resource_ID integer NOT NULL,
  Data_Source_ID integer NOT NULL,
  Study_ID integer NOT NULL,
  PRIMARY KEY (Policy_ID)
);

comment on column policy_registry.authority_type is 'Daniella: There is a hierarchy of authorities, study authority most commonly';
comment on column policy_registry.resource_type_governed is 'Daniella: Members of a study group have access to data set instances data set instances, data set instances must be approved for access by methods in a study protocol';
comment on column policy_registry.assertion is 'Daniella: e.g. "I as an authority or delegate for --this raw data source-- approve -this data set- to be accessed by -this method- for members of -this study/group-"';


CREATE TABLE IF NOT EXISTS source_data_warehouse (
  Connectivity_Manager_ID integer NOT NULL,
  Data_Manager_ID integer NOT NULL,
  Policy integer NOT NULL,
  Schema_Documentation text NOT NULL,
  ETL_Documentation text NOT NULL,
  ETL_Programs text NOT NULL,
  PRIMARY KEY (Connectivity_Manager_ID)
);

COMMENT on table source_data_warehouse is 'Daniella: Documentation of the data source, points of contact and authorities';


CREATE TABLE IF NOT EXISTS study(
  Study_ID integer NOT NULL,
  IRB_ID integer NOT NULL,
  Protocol text NOT NULL,
  Principal_Investigator_UID integer NOT NULL,
  Start_Date integer NOT NULL,
  End_Date integer NOT NULL,
  Clinical_Trials_ID integer NOT NULL,
  Analysis_Plan text NOT NULL,
  Grant_IDs text NOT NULL,
  Data_Set_IDs text NOT NULL,
  DUA_IDs text NOT NULL,
  PRIMARY KEY (Study_ID)
);

comment on column study.grant_ids is 'Daniella: actually need a table Grant ID-Study ID';
comment on column study.data_set_ids is 'Daniella: Need a table Data Set ID-Study ID';
comment on column study.dua_ids is 'Daniella: Need a table DUA-study';

CREATE TABLE IF NOT EXISTS users (
  UID integer NOT NULL,
  email integer NOT NULL,
  HSPC_documents text NOT NULL,
  Primary_Affliation integer NOT NULL,
  Secondary_Affliliation integer NOT NULL,
  Phone text NOT NULL,
  Reports_To integer NOT NULL,
  Active integer NOT NULL,
  Approved_Roles integer NOT NULL,
  First_Name text NOT NULL,
  Middle_Initial text,
  Last_Name text NOT NULL,
  PubMed_Author_ID text,
  PRIMARY KEY (UID)
);

comment on column users.approved_roles is 'Daniella: not sure if this goes here, but would be investigator, PM, etc.';
