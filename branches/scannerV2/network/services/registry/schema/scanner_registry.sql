set search_path = scanner_registry;

CREATE TABLE IF NOT EXISTS scanner_user (
  user_id serial NOT NULL primary key,
  user_name text not null unique,
  email text NOT NULL,
  hspc_documents text,
  phone text,
  reports_to integer,
  active boolean default true NOT NULL,
  first_name text NOT NULL,
  middle_initial text,
  last_name text NOT NULL,
  pubmed_author_id text,
  is_superuser boolean not null default false
);

CREATE TABLE IF NOT EXISTS study (
  study_id serial NOT NULL primary key,
  study_name text NOT NULL unique,
  irb_id integer NOT NULL,
  protocol text,
  principal_investigator_uid integer NOT NULL references scanner_user(user_id),
  start_date date,
  end_date date,
  clinical_trials_id integer,
  analysis_plan text,
  study_status_type_id integer not null references study_status_type(study_status_type_id)
);

/*
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

create table study_grant (
  study_id integer not null references study(study_id),
  grant_id integer not null references scanner_grant(grant_id),
  primary key (study_id, grant_id)
);

COMMENT ON table study_grant is 'Placeholder study/grant xref table.';

CREATE TABLE IF NOT EXISTS source_data_warehouse (
  source_data_warehouse_id serial not null primary key,
  connectivity_manager_id integer NOT NULL references scanner_user(user_id),
  data_manager_id integer NOT NULL references scanner_user(user_id),
  data_warehouse_confidentiality_level integer not null references confidentiality_level(level_id),
  schema_documentation text,
  etl_documentation text,
  etl_programs text
);

COMMENT on table source_data_warehouse is 'Placeholder. Daniella: Documentation of the data source, points of contact and authorities';

CREATE TABLE IF NOT EXISTS study_data_warehouse (
  study_id integer not null references study(study_id),
  source_data_warehouse_id integer not null references source_data_warehouse(source_data_warehouse_id)
);

COMMENT ON table study_data_warehouse is 'Placeholder study/warehouse xref table.';
*/

create table if not exists study_role (
  role_id serial not null primary key,
  study_id integer not null references study(study_id),
  role_within_study text not null,
  unique(study_id, role_within_study)
);

COMMENT on column study_role.role_within_study is 'PI, CO-I, PM, etc.';

CREATE TABLE IF NOT EXISTS user_role (
  user_role_id serial not null primary key,
  user_id integer NOT NULL references scanner_user(user_id),
  role_id integer NOT NULL references study_role(role_id),
  unique (User_ID, role_id)
);

create table if not exists tool_library (
  library_id serial not null primary key,
  library_name text not null,
  version text not null,
  description text,
  unique(library_name, version)
);

CREATE TABLE IF NOT EXISTS analysis_tool (
  tool_id serial NOT NULL primary key,
  tool_name text not null,
  tool_path text not null,
  tool_parent_library_id integer NOT NULL references tool_library(library_id),
  tool_description text NOT NULL,
  input_format_specifications text NOT NULL,
  output_format_specifications text NOT NULL,
  information_email text not null,
  unique (tool_name, tool_parent_library_id)
);

CREATE TABLE IF NOT EXISTS data_set_definition (
  data_set_definition_id serial NOT NULL primary key,
  data_set_name text not null unique,
  data_description_xml text not null,
  data_processing_xml text,
  data_processing_program text,
  author_uid integer references scanner_user(user_id),
  originating_study_id integer references study(study_id),
  data_set_confidentiality_level integer NOT NULL references confidentiality_level(level_id)
);

COMMENT on column data_set_definition.data_description_xml is 'Daniella: Path to XML describing data';
COMMENT on column data_set_definition.data_processing_xml is 'Daniella: Path to XML for data processing specifications';
COMMENT on column data_set_definition.data_processing_program is 'Daniella: Path to SQL or other data processing program';
COMMENT on column data_set_definition.author_uid is 'Daniella: UID of author';
COMMENT on column data_set_definition.originating_study_id is 'Daniella: ID of study using this data set';
COMMENT on column data_set_definition.data_set_confidentiality_level is 'Daniella: This is a key to the type of legal regulations this data set is subject to (safe harbor, limited data set, identified data)';

create table study_policy_statement (
  study_policy_statement_id serial not null primary key,
  study_id integer not null references study(study_id),
  data_set_definition_id integer not null references data_set_definition(data_set_definition_id),
  policy_authority integer not null references data_set_policy_authority(data_set_policy_authority_id),
  policy_originator integer not null references scanner_user(user_id),
  attestation text,
  role_id integer not null references study_role(role_id),
  analysis_tool_id integer not null references analysis_tool(tool_id),
  access_mode integer not null references access_mode(access_mode_id),
  policy_status_id integer not null references policy_status_type(policy_status_type_id)
);

create table if not exists site (
  site_id serial NOT NULL primary key,
  site_name text not null unique
);

-- The node table must have node_id and site_id, but Mike should feel free to change anything else

CREATE TABLE IF NOT EXISTS node (
  node_id serial NOT NULL primary key,
  site_id integer not null references site(site_id),
  host_url text NOT NULL,
  host_port integer NOT NULL,
  base_path text NOT NULL,
  description text,
  is_master boolean NOT NULL default false
);


CREATE TABLE IF NOT EXISTS data_set_instance (
  data_set_instance_id serial NOT NULL primary key,
  data_set_definition_id integer NOT NULL references data_set_definition(data_set_definition_id),
  node_id integer not null references node(node_id),
  data_source text NOT NULL
);

create table if not exists policy_statement (
  policy_statement_id serial not null primary key,
  data_set_instance_id integer not null references data_set_instance(data_set_instance_id),
  role_id integer not null references study_role(role_id),
  analysis_tool_id integer not null references analysis_tool(tool_id),
  access_mode_id integer not null references access_mode(access_mode_id),
  policy_status_type_id integer not null references policy_status_type(policy_status_type_id),
  parent_study_policy_statement_id integer not null references study_policy_statement(study_policy_statement_id)
);

COMMENT on table policy_statement is 'Users in role <role_id> may run toll <tool_id> on data set instance <data_set_instance_id> in mode <access_mode> if status is active';

create or replace view active_policy as
  select s.* from policy_statement s join policy_status_type t on s.policy_status_type_id = t.policy_status_type_id
    where t.policy_status_type_name = 'active';

 
create table if not exists site_policy (
  site_policy_id serial not null primary key,
  site_id integer not null references site(site_id),
  role_id integer not null references study_role(role_id)
);

--- More Mike-owned tables
/*
CREATE TABLE IF NOT EXISTS analysis_instance (
  analysis_instance_id serial NOT NULL primary key,
  -- I don't think study_policy_statement_id belongs here, but if it does, it should be a foreign key to the study_policy_statement table - LP
  study_policy_statement_id integer NOT NULL,
  -- Is there some table of states? - LP
  analysis_instance_state integer NOT NULL
);

CREATE TABLE IF NOT EXISTS analysis_instance_site_status (
  analysis_instance_site_status_id serial NOT NULL primary key,
  analysis_instance_id integer NOT NULL references analysis_instance(analysis_instance_id),
  -- I don't think study_policy_statement_id belongs here, but if it does, it should be a foreign key to the policy table - LP
  policy_instance_id integer NOT NULL,
  -- Is there a table of statuses? Are they the same as for analysis_table_state above?
  analysis_instance_site_status integer NOT NULL
);

CREATE TABLE IF NOT EXISTS result_instance (
  result_instance_id serial not null primary key,
  analysis_instance_id integer NOT NULL references analysis_instance(analysis_instance_id),
  -- What is result_location? Is it a foreign key to some other table?
  result_location integer NOT NULL,
  result_instance_url text NOT NULL
);
*/
