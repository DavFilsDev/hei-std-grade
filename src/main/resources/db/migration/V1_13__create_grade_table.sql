create table if not exists "grade"
(
    id         varchar primary key default uuid_generate_v4(),
    student_id varchar       not null references "app_user" (id) on delete cascade,
    exam_id    varchar       not null references "exam" (id) on delete cascade,
    score      numeric(4, 2) not null check (score between 0 and 20),
    unique (student_id, exam_id)
);
