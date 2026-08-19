create table if not exists "exam"
(
    id                varchar primary key default uuid_generate_v4(),
    exam_date         timestamptz  not null,
    coefficient       numeric(4, 3) check (coefficient > 0 and coefficient <= 1),
    session_type      varchar(10)  not null check (session_type in ('REGULAR', 'RETAKE')),
    course_id         varchar      not null references "course" (id) on delete cascade,
    academic_year_id  varchar      not null references "academic_year" (id) on delete restrict,
    constraint chk_exam_coefficient_required_for_regular
        check (session_type <> 'REGULAR' or coefficient is not null)
);
