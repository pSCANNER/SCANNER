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
  description text,
  irb_id integer NOT NULL,
  protocol text,
  principal_investigator_uid integer NOT NULL references scanner_user(user_id),
  start_date date,
  end_date date,
  clinical_trials_id integer,
  analysis_plan text,
  study_status_type_id integer not null references study_status_type(study_status_type_id)
);

create table if not exists study_role (
  role_id serial not null primary key,
  study_id integer not null references study(study_id),
  role_within_study text not null,
  unique(study_id, role_within_study)
);

COMMENT on column study_role.role_within_study is 'PI, CO-I, PM, etc.';

create table if not exists standard_role (
  standard_role_id serial not null primary key,
  standard_role_name text not null unique,
  description text,
  create_by_default boolean default true,
  add_to_study_policy_by_default boolean default false
);

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
  description text,
  data_processing_xml text,
  data_processing_program text,
  author_uid integer references scanner_user(user_id),
  originating_study_id integer references study(study_id),
  data_set_confidentiality_level integer NOT NULL references confidentiality_level(level_id)
);

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
  site_name text not null unique,
  description text
);

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
  data_set_instance_name text NOT NULL unique,
  description text,
  node_id integer not null references node(node_id),
  data_source text NOT NULL
);

create table if not exists analysis_policy_statement (
  analysis_policy_statement_id serial not null primary key,
  data_set_instance_id integer not null references data_set_instance(data_set_instance_id),
  role_id integer not null references study_role(role_id),
  analysis_tool_id integer not null references analysis_tool(tool_id),
  access_mode_id integer not null references access_mode(access_mode_id),
  policy_status_id integer not null references policy_status_type(policy_status_type_id),
  parent_study_policy_statement_id integer not null references study_policy_statement(study_policy_statement_id)
);

COMMENT on table analysis_policy_statement is 'Users in role <role_id> may run tool <tool_id> on data set instance <data_set_instance_id> in mode <access_mode> if status is active';

create table if not exists site_policy (
  site_policy_id serial not null primary key,
  site_id integer not null references site(site_id),
  role_id integer not null references study_role(role_id)
);

create table if not exists study_management_policy (
  study_policy_id serial not null primary key,
  study_id integer not null references study(study_id),
  role_id integer not null references study_role(role_id)
);

create table if not exists study_requested_site (
  study_requested_site_id serial not null primary key,
  study_id integer not null references study(study_id),
  site_id integer not null references site(site_id),
  unique(study_id, site_id)
);

CREATE TABLE IF NOT EXISTS data_set_variable_metadata
(
  data_set_variable_metadata_id serial NOT NULL primary key,
  data_set_definition integer NOT NULL references data_set_definition(data_set_definition_id),
  variable_name text NOT NULL, 
  variable_type text NOT NULL,
  variable_description text, 
  variable_options text,
  unique (data_set_definition, variable_name)
);
COMMENT ON TABLE data_set_variable_metadata
  IS 'this is information about the data set variables that can be shared across multiple studies and analysis instances';
COMMENT ON COLUMN data_set_variable_metadata.variable_name IS 'variable name is unique wihtin data set';
COMMENT ON COLUMN data_set_variable_metadata.variable_description IS 'this is the tooltip and description of the variable';

create table drugs_code_set (
    concept_id integer not null primary key,
    concept_name text not null,
    valid_start_date date not null,
    valid_end_date date default '31-dec-2099'  not null,
-- The rest of these probably won't be used in the initial version of prep-to-research
    concept_level integer not null,
    concept_class text not null,
    vocabulary_id integer not null,
    concept_code text not null
);

create index drugs_code_set_concept_name_idx on drugs_code_set(concept_name);
create index drugs_code_set_date_idx on drugs_code_set(valid_start_date, valid_end_date);

COMMENT ON TABLE drugs_code_set IS 'code sets for drugs, for use in prep-to-research';
COMMENT ON COLUMN drugs_code_set.concept_id IS 'OMOP concept ID';
COMMENT ON COLUMN drugs_code_set.concept_name IS 'OMOP concept name. This will be searched on in prep-to-research';
COMMENT ON COLUMN drugs_code_set.valid_start_date IS 'Date when this code starts being valid. Most queries should check that now() >= valid_start_date';
COMMENT ON COLUMN drugs_code_set.valid_end_date IS 'Date when this code starts being valid. Most queries should check that now() <= valid_end_date';
COMMENT ON COLUMN drugs_code_set.concept_level IS 'Currently, always 2 in the *_code tables.';
COMMENT ON COLUMN drugs_code_set.concept_class IS 'Always "Ingredient" in the drugs_code_set table.';
COMMENT ON COLUMN drugs_code_set.vocabulary_id IS 'Always 8 (RxNorm) in the drugs_code_set table.';
COMMENT ON COLUMN drugs_code_set.concept_code IS 'Code from original vocabulary (i.e., RxNorm code).';


create table conditions_code_set (
    concept_id integer not null primary key,
    concept_name text not null,
    valid_start_date date not null,
    valid_end_date date default '31-dec-2099'  not null,
-- The rest of these probably won't be used in the initial version of prep-to-research
    concept_level integer not null,
    concept_class text not null,
    vocabulary_id integer not null,
    concept_code text not null
);

create index conditions_code_set_concept_name_idx on conditions_code_set(concept_name);
create index conditions_code_set_date_idx on conditions_code_set(valid_start_date, valid_end_date);

COMMENT ON TABLE conditions_code_set IS 'code sets for conditions, for use in prep-to-research';
COMMENT ON COLUMN conditions_code_set.concept_id IS 'OMOP concept ID';
COMMENT ON COLUMN conditions_code_set.concept_name IS 'OMOP concept name. This will be searched on in prep-to-research';
COMMENT ON COLUMN conditions_code_set.valid_start_date IS 'Date when this code starts being valid. Most queries should check that now() >= valid_start_date';
COMMENT ON COLUMN conditions_code_set.valid_end_date IS 'Date when this code starts being valid. Most queries should check that now() <= valid_end_date';
COMMENT ON COLUMN conditions_code_set.concept_level IS 'Currently, always 2 in the *_code tables.';
COMMENT ON COLUMN conditions_code_set.concept_class IS 'Always "Clinical finding" in the conditions_code_set table.';
COMMENT ON COLUMN conditions_code_set.vocabulary_id IS 'Always 1 (SnoMed) in the conditions_code_set table.';
COMMENT ON COLUMN conditions_code_set.concept_code IS 'Code from original vocabulary (i.e., SnoMed code).';

create table favorites_code_set (
    concept_id integer not null primary key,
    concept_name text not null,
    valid_start_date date not null,
    valid_end_date date default '31-dec-2099'  not null,
-- The rest of these probably won't be used in the initial version of prep-to-research
    concept_level integer not null,
    concept_class text not null,
    vocabulary_id integer not null,
    concept_code text not null
);

create index favorites_code_set_concept_name_idx on favorites_code_set(concept_name);
create index favorites_code_set_date_idx on favorites_code_set(valid_start_date, valid_end_date);

COMMENT ON TABLE favorites_code_set IS 'some code sets for use in prep-to-research';
COMMENT ON COLUMN favorites_code_set.concept_id IS 'OMOP concept ID';
COMMENT ON COLUMN favorites_code_set.concept_name IS 'OMOP concept name. This will be searched on in prep-to-research';
COMMENT ON COLUMN favorites_code_set.valid_start_date IS 'Date when this code starts being valid. Most queries should check that now() >= valid_start_date';
COMMENT ON COLUMN favorites_code_set.valid_end_date IS 'Date when this code starts being valid. Most queries should check that now() <= valid_end_date';
COMMENT ON COLUMN favorites_code_set.concept_class IS 'Currently, always 2 in the *_code tables.';
COMMENT ON COLUMN favorites_code_set.concept_class IS 'Always "Ingredient" in the favorites_code_set table.';
COMMENT ON COLUMN favorites_code_set.vocabulary_id IS 'Initially, always the HQMF vocabulary ID in the favorites_code_set table.';
COMMENT ON COLUMN favorites_code_set.concept_code IS 'Code from original vocabulary (i.e., HQMF OID).';

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
