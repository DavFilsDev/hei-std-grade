create table if not exists "class_group"
(
    id  varchar primary key default uuid_generate_v4(),
    ref varchar(50) not null unique
);
