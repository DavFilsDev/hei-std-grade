create table if not exists "student_group_assignment"
(
    student_id       varchar not null references "app_user" (id) on delete cascade,
    academic_year_id varchar not null references "academic_year" (id) on delete restrict,
    group_id         varchar not null references "class_group" (id) on delete restrict,
    primary key (student_id, academic_year_id)
);
