create table if not exists "role"
(
    id   varchar primary key default uuid_generate_v4(),
    name varchar(20) not null unique check (name in ('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN'))
);
