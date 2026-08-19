create table if not exists "user_role"
(
    user_id varchar not null references "app_user" (id) on delete cascade,
    role_id varchar not null references "role" (id) on delete cascade,
    primary key (user_id, role_id)
);
