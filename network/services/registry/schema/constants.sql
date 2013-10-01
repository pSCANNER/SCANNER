create schema scanner_registry;
set search_path = scanner_registry;

create table confidentiality_level (
  level_id integer not null primary key,
  level_name text not null unique,
  description text not null unique
);

insert into confidentiality_level (level_id, level_name, description) values
 (100, 'safe_harbor', 'SAFE HARBOR'), 
 (200, 'dua_covered_lds', 'DUA-COVERED LIMITED DATA SET'),
 (300, 'identified_data', 'IDENTIFIED DATA');

create table data_set_policy_authority (
  data_set_policy_authority_id integer not null primary key,
  data_set_policy_authority_name text not null unique,
  description text not null unique
);

insert into data_set_policy_authority (data_set_policy_authority_id, data_set_policy_authority_name, description) values
 (100, 'legal', 'LEGAL'),
 (200, 'study', 'STUDY'),
 (300, 'site_admin', 'SITE ADMINISTRATION'),
 (400, 'network_admin', 'NETWORK ADMINISTRATION');

create table policy_status_type (
  policy_status_type_id integer not null primary key,
  policy_status_type_name text not null unique,
  description text not null unique
);

insert into policy_status_type (policy_status_type_id, policy_status_type_name, description) values
 (0, 'active', 'active'),
 (1, 'denied', 'denied'),
 (2, 'revoked', 'revoked'),
 (3, 'inconsistent', 'inconsistent');


create table access_mode (
  access_mode_id integer not null primary key,
  access_mode_name text not null unique,
  description text not null unique
);

insert into access_mode (access_mode_id, access_mode_name, description) values
 (0, 'sync', 'sync'),
 (1, 'async', 'async');

create table study_status_type (
  study_status_type_id integer not null primary key,
  study_status_type_name text not null unique,
  description text not null unique
);

insert into study_status_type (study_status_type_id, study_status_type_name, description) values
 (1, 'defined', 'defined');

