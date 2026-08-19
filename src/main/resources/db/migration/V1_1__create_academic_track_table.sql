create extension if not exists "uuid-ossp";

create table if not exists "academic_track"
(
    id   varchar primary key default uuid_generate_v4(),
    code varchar(10)  not null unique check (code in ('TN', 'EL')),
    name varchar(100) not null
);
