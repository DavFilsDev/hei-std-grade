create table if not exists "semester"
(
    id            varchar primary key default uuid_generate_v4(),
    code          varchar(10) not null unique check (code in ('S1', 'S2', 'S3', 'S4', 'S5', 'S6')),
    "position"    int         not null unique check ("position" between 1 and 6),
    total_credits int         not null default 30
);
