create table if not exists "teaching_unit"
(
    id          varchar primary key default uuid_generate_v4(),
    code        varchar(50)  not null unique,
    title       varchar(150) not null,
    semester_id varchar      not null references "semester" (id) on delete restrict,
    credits     int          not null
);
