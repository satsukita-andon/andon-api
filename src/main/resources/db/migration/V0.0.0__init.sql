/**
 * util
 */
create type publishing_status as enum ('private', 'published', 'suspended');

/**
 * user data
 */
create table users (
  id serial primary key,
  login varchar(30) not null unique,
  password varchar(256) not null,
  name varchar(30) not null,
  biography text,
  times smallint not null,
  class_first smallint,
  class_second smallint,
  class_third smallint,
  chief_first boolean,
  chief_second boolean,
  chief_third boolean,
  icon_url text,
  email text unique,
  admin boolean not null default false,
  suspended boolean not null default false,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

/**
 * festival data
 */
create table festivals (
  id smallserial primary key,
  times smallint not null unique,
  theme varchar(50) not null,
  theme_roman varchar(100) not null,
  theme_kana varchar(100) not null,
  thumbnail_url text
);

/**
 * class data
 */
create table classes (
  id smallserial primary key,
  times smallint not null references festivals (times) on delete cascade,
  grade smallint not null,
  class smallint not null,
  title varchar(200) not null,
  title_kana varchar(200),
  description text,
  score numeric(7, 3), -- official score: 0-9999.999
  header_image_url text,
  thumbnail_url text,
  created_at timestamptz not null,
  updated_at timestamptz not null,
  unique (times, grade, class)
);

create table prizes (
  id smallserial primary key,
  code varchar(30) not null unique, -- grand, gold, silver, bronze, alumni, rekka, etc
  label varchar(30) not null unique, -- 行灯大賞, 金賞, 銀賞, 銅賞, 同窓会賞, 烈夏賞, etc
  index smallint not null, -- 0: nothing, 100: grand
  color varchar(6) not null -- color code: 000000~ffffff
);

create table class_prize_rel (
  id smallserial primary key,
  class_id smallint not null references classes (id) on delete cascade,
  prize_id smallint not null references prizes (id) on delete cascade,
  unique (class_id, prize_id)
);

create table class_tags (
  id serial primary key,
  class_id smallint not null references classes (id) on delete cascade,
  label varchar(50) not null,
  unique (class_id, label)
);

create table class_reviews (
  id serial primary key,
  class_id smallint not null references classes (id) on delete cascade,
  user_id integer not null references users (id) on delete cascade,
  title varchar(200) not null,
  body text not null,
  score numeric(2, 1), -- 0.0-5.0
  status publishing_status not null,
  created_at timestamptz not null,
  updated_at timestamptz not null,
  unique (class_id, user_id)
);

create table class_comments ( -- not review: e.g., "How to make such a great face?"
  id serial primary key,
  class_id integer not null references classes (id) on delete cascade,
  user_id integer references users (id) on delete cascade,
  name varchar(30),
  password varchar(256),
  body text not null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

create table class_images (
  id serial primary key,
  class_id smallint not null references classes (id) on delete cascade,
  user_id integer not null references users (id) on delete cascade,
  raw_url text not null,       -- no compression and no scale-down
  fullsize_url text not null,  -- compression and no scale-down
  thumbnail_url text not null, -- compression and scale-down
  created_at timestamptz not null -- and no update
);

create type video_service as enum ('youtube');
create table class_external_videos (
  id serial primary key,
  class_id smallint not null references classes (id) on delete cascade,
  user_id integer not null references users (id) on delete cascade,
  url text not null,
  service video_service not null,
  video_id text not null, -- ID for each service
  start_position varchar(20),
  created_at timestamptz not null,
  updated_at timestamptz not null
);

create table class_articles (
  id serial primary key,
  class_id smallint not null references classes (id) on delete cascade,
  latest_revision_number smallint not null,
  status publishing_status not null,
  created_by integer references users (id) on delete set null,
  updated_by integer references users (id) on delete set null,
  created_at timestamp not null,
  updated_at timestamp not null
);

create table class_article_revisions (
  id serial primary key,
  article_id integer not null references class_articles (id) on delete cascade,
  revision_number smallint not null,
  user_id integer references users (id) on delete set null,
  title varchar(200) not null,
  body text not null,
  comment text not null,
  created_at timestamptz not null,
  unique (article_id, revision_number)
);

create table class_article_comments (
  id serial primary key,
  article_id integer not null references class_articles (id) on delete cascade,
  user_id integer references users (id) on delete cascade,
  name varchar(30),
  password varchar(256),
  body text not null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

create table class_resources (
  id serial primary key,
  class_id smallint not null references classes (id) on delete cascade,
  latest_revision_number smallint not null,
  status publishing_status not null,
  created_by integer references users (id) on delete set null,
  updated_by integer references users (id) on delete set null,
  created_at timestamp not null,
  updated_at timestamp not null
);

create table class_resource_revisions (
  id serial primary key,
  resource_id integer not null references class_resources (id) on delete cascade,
  revision_number smallint not null,
  user_id integer references users (id) on delete set null,
  title varchar(200) not null,
  description text not null,
  url text not null,
  comment text not null,
  created_at timestamptz not null,
  unique (resource_id, revision_number)
);

create table class_resource_tags (
  id serial primary key,
  resource_id integer not null references class_resources (id) on delete cascade,
  label varchar(50) not null,
  unique (resource_id, label)
);

create table class_resource_comments (
  id serial primary key,
  resource_id integer not null references class_resources (id) on delete cascade,
  user_id integer references users (id) on delete cascade,
  name varchar(30),
  password varchar(256),
  body text not null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

/**
 * article data
 */
create type editorial_right as enum ('selected', 'classmate', 'cohort', 'all');

create table articles (
  id serial primary key,
  owner_id integer not null references users (id) on delete cascade,
  latest_revision_number smallint not null,
  status publishing_status not null,
  editorial_right editorial_right not null,
  created_by integer references users (id) on delete set null,
  updated_by integer references users (id) on delete set null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

create table article_revisions (
  id serial primary key,
  article_id integer not null references articles (id) on delete cascade,
  revision_number smallint not null,
  user_id integer references users (id) on delete set null,
  title varchar(200) not null,
  body text not null,
  comment text not null,
  created_at timestamptz not null,
  unique (article_id, revision_number)
);

create table article_editor_rel (
  id serial primary key,
  article_id integer not null references articles (id) on delete cascade,
  user_id integer not null references users (id) on delete cascade,
  unique (article_id, user_id)
);

create table article_tags (
  id serial primary key,
  article_id integer not null references articles (id) on delete cascade,
  label varchar(50) not null,
  unique (article_id, label)
);

create table article_comments (
  id serial primary key,
  article_id integer not null references articles (id) on delete cascade,
  user_id integer references users (id) on delete cascade, -- null if anonymous
  name varchar(30),
  password varchar(256),
  body text not null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

/**
 * resource data
 */
create table resources (
  id serial primary key,
  latest_revision_number smallint not null,
  status publishing_status not null,
  owner_id integer not null references users (id) on delete cascade,
  editorial_right editorial_right not null,
  created_by integer references users (id) on delete set null,
  updated_by integer references users (id) on delete set null,
  created_at timestamp not null,
  updated_at timestamp not null
);

create table resource_revisions (
  id serial primary key,
  resource_id integer not null references resources (id) on delete cascade,
  revision_number smallint not null,
  user_id integer references users (id) on delete set null,
  title varchar(200) not null,
  description text not null,
  url text not null,
  comment text not null,
  created_at timestamptz not null,
  unique (resource_id, revision_number)
);

create table resource_editor_rel (
  id serial primary key,
  resource_id integer not null references resources (id) on delete cascade,
  user_id integer not null references users (id) on delete cascade,
  unique (resource_id, user_id)
);

create table resource_tags (
  id serial primary key,
  resource_id integer not null references resources (id) on delete set null,
  label varchar(50) not null,
  unique (resource_id, label)
);

create table resource_comments (
  id serial primary key,
  resource_id integer not null references resources (id) on delete cascade,
  user_id integer references users (id) on delete cascade,
  name varchar(30),
  password varchar(256),
  body text not null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

/**
 * news
 */
create table news (
  id serial primary key,
  owner_id integer not null references users (id) on delete cascade,
  latest_revision_number smallint not null,
  status publishing_status not null,
  created_by integer references users (id) on delete set null,
  updated_by integer references users (id) on delete set null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

create table news_revisions (
  id serial primary key,
  news_id integer not null references news (id) on delete cascade,
  revision_number smallint not null,
  user_id integer references users (id) on delete set null,
  title varchar(200) not null,
  body text not null,
  comment text not null,
  created_at timestamptz not null,
  unique (news_id, revision_number)
);

create table news_tags (
  id serial primary key,
  news_id integer not null references news (id) on delete cascade,
  label varchar(50) not null,
  unique (news_id, label)
);

create table news_comments (
  id serial primary key,
  news_id integer not null references news (id) on delete cascade,
  user_id integer references users (id) on delete cascade,
  name varchar(30),
  password varchar(256),
  body text not null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

/**
 * fixed pages
 */
create type fixed_content_type as enum ('news', 'about', 'contact');
create table fixed_contents (
  id serial primary key,
  type fixed_content_type not null unique,
  latest_revision_number smallint not null,
  updated_by integer references users (id) on delete set null,
  updated_at timestamptz not null
);

create table fixed_content_revisions (
  id serial primary key,
  content_id integer not null references fixed_contents (id) on delete cascade,
  revision_number smallint not null,
  user_id integer references users (id) on delete set null,
  body text not null,
  comment text not null,
  created_at timestamptz not null,
  unique (content_id, revision_number)
);
