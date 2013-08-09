\i constants.sql

create schema scanner_registry;
set search_path = scanner_registry;

create table if not exists tool_library (
  library_id serial not null primary key,
  library_name text not null unique,
  library_version text not null,
  description text,
  unique(library_name, library_version)
);

CREATE TABLE IF NOT EXISTS scanner_user (
  user_id serial NOT NULL primary key,
  username text not null unique,
  email text NOT NULL,
  HSPC_documents text NOT NULL,
  Primary_Affliation integer NOT NULL,
  Secondary_Affliliation integer NOT NULL,
  Phone text NOT NULL,
  Reports_To integer NOT NULL references scanner_user(user_id),
  Active boolean NOT NULL,
  First_Name text NOT NULL,
  Middle_Initial text,
  Last_Name text NOT NULL,
  PubMed_Author_ID text
);

CREATE TABLE IF NOT EXISTS dua (
  dua_id serial not null primary key,
  dua_detail text
);

COMMENT ON table dua is 'Placeholder DUA table.';

CREATE TABLE IF NOT EXISTS scanner_grant (
  grant_id serial not null primary key,
  grant_detail text
);

COMMENT ON table scanner_grant is 'Placeholder grant table.';

create table study_status_type (
  study_status_id integer not null primary key,
  study_status_name text not null unique
);

CREATE TABLE IF NOT EXISTS study (
  Study_ID serial NOT NULL primary key,
  IRB_ID integer NOT NULL,
  Protocol text NOT NULL,
  Principal_Investigator_UID integer NOT NULL references scanner_user(user_id),
  Start_Date date NOT NULL,
  End_Date date NOT NULL,
  Clinical_Trials_ID integer NOT NULL,
  Analysis_Plan text NOT NULL
);

create table study_grant (
  study_id integer not null references study(study_id),
  grant_id integer not null references scanner_grant(grant_id),
  primary key (study_id, grant_id)
);

create table confidentiality_level (
  level_id integer not null primary key,
  level_name text not null unique
);

insert into confidentiality_level(level_id, level_name) values
  (:safe_harbor, 'SAFE HARBOR'),
  (:dua_covered_lds, 'DUA-COVERED LIMITED DATA SET'),
  (:identified_data, 'IDENTIFIED DATA');

CREATE TABLE IF NOT EXISTS source_data_warehouse (
  source_data_warehouse_id serial not null primary key,
  Connectivity_Manager_ID integer NOT NULL references scanner_user(user_id),
  Data_Manager_ID integer NOT NULL references scanner_user(user_id),
  data_warehouse_confidentiality_level integer not null references confidentiality_level(level_id),
  Schema_Documentation text NOT NULL,
  ETL_Documentation text NOT NULL,
  ETL_Programs text NOT NULL
);

COMMENT on table source_data_warehouse is 'Daniella: Documentation of the data source, points of contact and authorities';

CREATE TABLE IF NOT EXISTS study_data_warehouse (
  study_id integer not null references study(study_id),
  source_data_warehouse_id integer not null references source_data_warehouse(source_data_warehouse_id)
);

create table if not exists scanner_role (
  role_id serial not null primary key,
  study_id integer not null references study(study_id),
  role_within_study text not null,
  unique(study_id, role_within_study)
);

COMMENT on column scanner_role.role_within_study is 'PI, CO-I, PM, etc.';

CREATE TABLE IF NOT EXISTS investigator_role (
  Investigator_ID integer NOT NULL references scanner_user(user_id),
  role_id integer NOT NULL references scanner_role(role_id),
  primary key (Investigator_ID, role_id)
);


CREATE TABLE IF NOT EXISTS analysis_tool (
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

create table data_set_policy_authority (
  authority_id serial not null primary key,
  authority_name text not null unique
);

insert into data_set_policy_authority(authority_id, authority_name) values
  (:legal, 'LEGAL'),
  (:study, 'STUDY'),
  (:site_admin, 'SITE ADMINISTRATION'),
  (:network_admin, 'NETWORK ADMINISTRATION');

  
CREATE TABLE IF NOT EXISTS data_set_definition (
  Data_Set_Definition_ID serial NOT NULL primary key,
  Data_Description_XML text NOT NULL,
  Data_Processing_XML text NOT NULL,
  Data_Processing_Program text NOT NULL,
  Author_UID integer NOT NULL references scanner_user(user_id),
  Originating_Study_ID integer NOT NULL references study(study_id),
  Data_Set_Confidentiality_Level integer NOT NULL references confidentiality_level(level_id)
);

COMMENT on column data_set_definition.data_description_xml is 'Daniella: Path to XML describing data';
COMMENT on column data_set_definition.data_processing_xml is 'Daniella: Path to XML for data processing specifications';
COMMENT on column data_set_definition.data_processing_program is 'Daniella: Path to SQL or other data processing program';
COMMENT on column data_set_definition.author_uid is 'Daniella: UID of author';
COMMENT on column data_set_definition.originating_study_id is 'Daniella: ID of study using this data set';
COMMENT on column data_set_definition.data_set_confidentiality_level is 'Daniella: This is a key to the type of legal regulations this data set is subject to (safe harbor, limited data set, identified data)';

create table policy_status_type (
  policy_status_type_id integer primary key,
  policy_status_type_name text not null unique
);

insert into policy_status_type (policy_status_type_id, policy_status_type_name) values
 (:active, 'active'),
 (:denied, 'denied'),
 (:revoked, 'revoked'),
 (:inconsistent, 'inconsistent');

create table if not exists access_mode (
  access_mode_id serial not null primary key,
  access_mode_name text not null unique
);
insert into access_mode(access_mode_id, access_mode_name) values (:sync, 'synchronous'), (:async, 'asynchronous');

create table abstract_policy (
  abstract_policy_id serial not null primary key,
  study_id integer not null references study(study_id),
  data_set_definition_id integer not null references data_set_definition(data_set_definition_id),
  policy_authority integer not null references data_set_policy_authority(authority_id),
  policy_originator integer not null references scanner_user(user_id),
  attestation text not null,
  role_id integer not null references scanner_role(role_id),
  analysis_tool_id integer not null references analysis_tool(tool_id),
  access_mode integer not null references access_mode(access_mode_id),
  policy_status_id integer not null references policy_status_type(policy_status_type_id)
);


CREATE TABLE IF NOT EXISTS data_set_instance (
  Data_Set_Instance_ID serial NOT NULL primary key,
  Data_Set_Definition_ID integer NOT NULL references data_set_definition(data_set_definition_id),
  Data_Set_Instance_Location text NOT NULL,
  Curator_UID integer NOT NULL references scanner_user(user_id),
  Study_ID integer NOT NULL references study(study_id),
  Source_Data_Warehouse_ID integer NOT NULL references source_data_warehouse (source_data_warehouse_id),
  Data_Slice_ID integer DEFAULT NULL
);

create table if not exists policy_statement (
  policy_statement_id serial not null primary key,
  data_set_instance_id integer not null references data_set_instance(data_set_instance_id),
  role_id integer not null references scanner_role(role_id),
  analysis_tool_id integer not null references analysis_tool(tool_id),
  access_mode integer not null references access_mode(access_mode_id),
  policy_status_id integer not null references policy_status_type(policy_status_type_id)
);

COMMENT on table policy_statement is 'Users in role <role_id> may run toll <tool_id> on data set instance <data_set_instance_id> in mode <access_mode> if status is active';

create or replace view active_policy as
  select * from policy_statement where policy_status_id = :active;
