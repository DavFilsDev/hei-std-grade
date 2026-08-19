create table if not exists "app_user"
(
    id            varchar primary key default uuid_generate_v4(),
    ref           varchar(50)  not null unique,
    last_name     varchar(150) not null,
    first_name    varchar(150) not null,
    email         varchar(150) not null unique,
    password_hash varchar(255) not null,
    is_enabled    boolean      not null default true,
    entrance_date date         not null,
    track_id      varchar references "academic_track" (id) on delete restrict
);
