create table if not exists "teacher_course_assignment"
(
    teacher_id        varchar not null references "app_user" (id) on delete cascade,
    course_id         varchar not null references "course" (id) on delete cascade,
    academic_year_id  varchar not null references "academic_year" (id) on delete restrict,
    primary key (teacher_id, course_id, academic_year_id)
);
