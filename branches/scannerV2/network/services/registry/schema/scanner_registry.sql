create schema scanner_registry;
set search_path = scanner_registry;

create table if not exists tool_library (
  library_id serial not null primary key,
  libary_name text not null unique,
  library_version text not null,
  description text
);

CREATE TABLE IF NOT EXISTS users (
  user_id serial NOT NULL primary key,
  username text not null unique,
  email text NOT NULL,
  HSPC_documents text NOT NULL,
  Primary_Affliation integer NOT NULL,
  Secondary_Affliliation integer NOT NULL,
  Phone text NOT NULL,
  Reports_To integer NOT NULL references users(user_id),
  Active boolean NOT NULL,
  First_Name text NOT NULL,
  Middle_Initial text,
  Last_Name text NOT NULL,
  PubMed_Author_ID text
);

CREATE TABLE IF NOT EXISTS study(
  Study_ID serial NOT NULL primary key,
  IRB_ID integer NOT NULL,
  Protocol text NOT NULL,
  Principal_Investigator_UID integer NOT NULL references users(user_id),
  Start_Date date NOT NULL,
  End_Date date NOT NULL,
  Clinical_Trials_ID integer NOT NULL,
  Analysis_Plan text NOT NULL,
  Grant_IDs text NOT NULL,
  Data_Set_IDs text NOT NULL,
  DUA_IDs text NOT NULL
);

comment on column study.grant_ids is 'Daniella: actually need a table Grant ID-Study ID';
comment on column study.data_set_ids is 'Daniella: Need a table Data Set ID-Study ID';
comment on column study.dua_ids is 'Daniella: Need a table DUA-study';

create table if not exists roles (
  role_id serial not null primary key,
  study_id integer not null references study(study_id),
  role_within_study text not null,
  unique(study_id, role_within_study)
);

COMMENT on column roles.role_within_study is 'PI, CO-I, PM, etc.';

CREATE TABLE IF NOT EXISTS investigator_roles (
  Investigator_ID integer NOT NULL references users(user_id),
  role_id integer NOT NULL references roles(role_id),
  primary key (Investigator_ID, role_id)
);


CREATE TABLE IF NOT EXISTS analysis_tools (
  Tool_ID serial NOT NULL primary key,
  tool_name text not null,
  Tool_Parent_Library_ID integer NOT NULL references tool_library(library_id),
  Tool_Description text NOT NULL,
  Input_Format_Specifications text NOT NULL,
  Output_Format_Specifications text NOT NULL,
  Curator_UID integer NOT NULL,
  Information_Email text NOT NULL,
  unique (tool_name, tool_parent_library_id)
);

create table confidentiality_levels (
  level_id serial not null primary key,
  level_name text not null unique
);

insert into confidentiality_levels(level_name) values ('SAFE HARBOR'), ('DUA-COVERED LIMITED DATA SET'), ('IDENTIFIED DATA');

create table data_set_policy_authorities (
  authority_id serial not null primary key,
  authority_name text not null unique
);

CREATE TABLE IF NOT EXISTS data_set_definition (
  Data_Set_Definition_ID serial NOT NULL primary key,
  Data_Description_XML text NOT NULL,
  Data_Processing_XML text NOT NULL,
  Data_Processing_Program text NOT NULL,
  Author_UID integer NOT NULL references users(user_id),
  Originating_Study_ID integer NOT NULL references study(study_id),
  Data_Set_Confidentiality_Level integer NOT NULL references confidentiality_levels(level_id)
);

COMMENT on column data_set_definition.data_description_xml is 'Daniella: Path to XML describing data';
COMMENT on column data_set_definition.data_processing_xml is 'Daniella: Path to XML for data processing specifications';
COMMENT on column data_set_definition.data_processing_program is 'Daniella: Path to SQL or other data processing program';
COMMENT on column data_set_definition.author_uid is 'Daniella: UID of author';
COMMENT on column data_set_definition.originating_study_id is 'Daniella: ID of study using this data set';
COMMENT on column data_set_definition.data_set_confidentiality_level is 'Daniella: This is a key to the type of legal regulations this data set is subject to (safe harbor, limited data set, identified data)';

CREATE TABLE IF NOT EXISTS policy_registry (
  Policy_ID integer NOT NULL,
  Authority_Type text not null check (authority_type in ('LEGAL','STUDY','SITE ADMINISTRATION','NETWORK ADMINISTRATION')),
  Resource_Type_Governed text not null check (resource_type_governed in ('DATA SET INSTANCE','METHOD')),
  Assertion text NOT NULL,
  Data_Resource_Instance_Resource_ID integer NOT NULL,
  Data_Resource_Definition_ID integer NOT NULL,
  data_resource_confidentiality_level integer not null references confidentiality_levels(level_id),
  Method_Resource_ID integer NOT NULL,
  Data_Source_ID integer NOT NULL,
  Study_ID integer NOT NULL,
  PRIMARY KEY (Policy_ID)
);

CREATE TABLE IF NOT EXISTS source_data_warehouse (
  source_data_warehouse_id serial not null primary key,
  Connectivity_Manager_ID integer NOT NULL references users(user_id),
  Data_Manager_ID integer NOT NULL references users(user_id),
  data_warehouse_confidentiality_level integer not null references confidentiality_levels(level_id),
  Schema_Documentation text NOT NULL,
  ETL_Documentation text NOT NULL,
  ETL_Programs text NOT NULL
);

COMMENT on table source_data_warehouse is 'Daniella: Documentation of the data source, points of contact and authorities';


CREATE TABLE IF NOT EXISTS data_set_instance (
  Data_Set_Instance_ID serial NOT NULL primary key,
  Data_Set_Definition_ID integer NOT NULL references data_set_definition(data_set_definition_id),
  Data_Set_Instance_Location text NOT NULL,
  Curator_UID integer NOT NULL references users(user_id),
  Study_ID integer NOT NULL references study(study_id),
  Source_Data_Warehouse_ID integer NOT NULL references source_data_warehouse (source_data_warehouse_id),
  Data_Slice_ID integer DEFAULT NULL
);


CREATE TABLE IF NOT EXISTS dua_study(
  DUA_ID integer NOT NULL,
  Study_ID integer NOT NULL,
  primary key (DUA_ID,Study_ID)
);

COMMENT on TABLE dua_study is 'Daniella: Table linking the DUA to the study';


comment on column policy_registry.authority_type is 'Daniella: There is a hierarchy of authorities, study authority most commonly';
comment on column policy_registry.resource_type_governed is 'Daniella: Members of a study group have access to data set instances data set instances, data set instances must be approved for access by methods in a study protocol';
comment on column policy_registry.assertion is 'Daniella: e.g. "I as an authority or delegate for --this raw data source-- approve -this data set- to be accessed by -this method- for members of -this study/group-"';




