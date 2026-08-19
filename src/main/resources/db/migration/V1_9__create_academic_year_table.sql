create table if not exists "academic_year"
(
    id         varchar primary key default uuid_generate_v4(),
    label      varchar(20) not null unique,
    start_year int         not null unique
);
