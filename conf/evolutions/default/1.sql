# --- !Ups

CREATE TABLE users(
  user_id uuid NOT NULL PRIMARY KEY,
  first_name VARCHAR,
  last_name VARCHAR,
  full_name VARCHAR,
  email VARCHAR,
  avatar_url VARCHAR);

CREATE TABLE logininfos(
  login_info_id uuid NOT NULL PRIMARY KEY,
  provider_id VARCHAR NOT NULL,
  provider_key VARCHAR NOT NULL);

CREATE TABLE userlogininfos(
  user_login_info_id uuid NOT NULL PRIMARY KEY,
  user_id uuid NOT NULL REFERENCES users,
  login_info_id uuid NOT NULL REFERENCES logininfos
  );

CREATE TABLE passwordinfos(
  password_info_id uuid NOT NULL PRIMARY KEY,
  hasher VARCHAR NOT NULL,
  password VARCHAR NOT NULL,
  salt VARCHAR,
  login_info_id uuid NOT NULL REFERENCES logininfos);

CREATE TABLE oauth1infos(
  oauth1_info_id uuid NOT NULL PRIMARY KEY,
  token VARCHAR NOT NULL,
  secret VARCHAR NOT NULL,
  login_info_id uuid NOT NULL REFERENCES logininfos);

CREATE TABLE oauth2infos(
  oauth2_info_id uuid NOT NULL PRIMARY KEY,
  accesstoken VARCHAR NOT NULL,
  tokentype VARCHAR,
  expiresin INTEGER,
  refreshtoken VARCHAR,
  login_info_id uuid NOT NULL REFERENCES logininfos);

CREATE TABLE openidinfos (
  id VARCHAR NOT NULL PRIMARY KEY,
  login_info_id uuid NOT NULL  REFERENCES logininfos);

CREATE TABLE openidattributes (
  id VARCHAR NOT NULL,
  key VARCHAR NOT NULL,
  value VARCHAR NOT NULL);

# --- !Downs

DROP TABLE openidattributes;
DROP TABLE openidinfos;
DROP TABLE oauth2infos;
DROP TABLE oauth1infos;
DROP TABLE passwordinfos;
DROP TABLE userlogininfos;
DROP TABLE logininfos;
DROP TABLE users;