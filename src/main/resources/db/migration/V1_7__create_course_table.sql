create table if not exists "course"
(
    id                varchar primary key default uuid_generate_v4(),
    ref               varchar(50)  not null unique,
    title             varchar(150) not null,
    credits           int          not null check (credits > 0),
    teaching_unit_id  varchar      not null references "teaching_unit" (id) on delete cascade,
    track_id          varchar references "academic_track" (id) on delete restrict
);
