create table if not exists "grade_history"
(
    id         varchar primary key default uuid_generate_v4(),
    grade_id   varchar       not null references "grade" (id) on delete cascade,
    old_score  numeric(4, 2) not null,
    new_score  numeric(4, 2) not null,
    reason     text          not null,
    updated_by varchar       not null references "app_user" (id) on delete restrict,
    updated_at timestamptz   not null default now()
);
