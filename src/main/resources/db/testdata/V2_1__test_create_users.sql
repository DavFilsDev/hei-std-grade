-- Dev/test-only seed data (loaded via the "dev" Spring profile, never in prod).
-- Password for every seeded user is "AdminPass123!" (Argon2, same hash as cine-app's testdata).

insert into "academic_track" (id, code, name)
values ('track-tn', 'TN', 'Transformation Numérique'),
       ('track-el', 'EL', 'Écosystème Logiciel');

insert into "semester" (id, code, "position", total_credits)
values ('semester-s1', 'S1', 1, 30),
       ('semester-s2', 'S2', 2, 30),
       ('semester-s3', 'S3', 3, 30),
       ('semester-s4', 'S4', 4, 30),
       ('semester-s5', 'S5', 5, 30),
       ('semester-s6', 'S6', 6, 30);

insert into "academic_year" (id, label, start_year)
values ('academic-year-2024', '2024-2025', 2024);

insert into "teaching_unit" (id, code, title, semester_id, credits)
values ('teaching-unit-donnees1', 'PROG4-DONNEES1', 'Données 1', 'semester-s1', 5);

insert into "course" (id, ref, title, credits, teaching_unit_id, track_id)
values ('course-donnees1', 'donnees1', 'Données 1', 5, 'teaching-unit-donnees1', null);

insert into "role" (id, name)
values ('role-student', 'ROLE_STUDENT'),
       ('role-teacher', 'ROLE_TEACHER'),
       ('role-admin', 'ROLE_ADMIN');

insert into "app_user" (id, ref, last_name, first_name, email, password_hash, is_enabled, entrance_date, track_id)
values ('user-admin', 'STD24001', 'Admin', 'Ada', 'admin@hei-std-grade.test',
        '$argon2id$v=19$m=16384,t=2,p=1$tPKcjip3t+ET2/GsS+svlg$biamX1sYCDi7ixq+BhCKavnPjt/sXvzkpLsAVgb+yU4',
        true, '2024-09-01', null),
       ('user-teacher-a', 'STD24002', 'TeacherA', 'Théo', 'teacher-a@hei-std-grade.test',
        '$argon2id$v=19$m=16384,t=2,p=1$tPKcjip3t+ET2/GsS+svlg$biamX1sYCDi7ixq+BhCKavnPjt/sXvzkpLsAVgb+yU4',
        true, '2024-09-01', null),
       ('user-teacher-b', 'STD24003', 'TeacherB', 'Tia', 'teacher-b@hei-std-grade.test',
        '$argon2id$v=19$m=16384,t=2,p=1$tPKcjip3t+ET2/GsS+svlg$biamX1sYCDi7ixq+BhCKavnPjt/sXvzkpLsAVgb+yU4',
        true, '2024-09-01', null),
       ('user-student-a', 'STD24190', 'StudentA', 'Sofia', 'student-a@hei-std-grade.test',
        '$argon2id$v=19$m=16384,t=2,p=1$tPKcjip3t+ET2/GsS+svlg$biamX1sYCDi7ixq+BhCKavnPjt/sXvzkpLsAVgb+yU4',
        true, '2024-09-01', 'track-tn'),
       ('user-student-b', 'STD24191', 'StudentB', 'Bao', 'student-b@hei-std-grade.test',
        '$argon2id$v=19$m=16384,t=2,p=1$tPKcjip3t+ET2/GsS+svlg$biamX1sYCDi7ixq+BhCKavnPjt/sXvzkpLsAVgb+yU4',
        true, '2024-09-01', null),
       ('user-student-c', 'STD24192', 'StudentC', 'Chris', 'student-c@hei-std-grade.test',
        '$argon2id$v=19$m=16384,t=2,p=1$tPKcjip3t+ET2/GsS+svlg$biamX1sYCDi7ixq+BhCKavnPjt/sXvzkpLsAVgb+yU4',
        true, '2024-09-01', null);

insert into "user_role" (user_id, role_id)
values ('user-admin', 'role-admin'),
       ('user-teacher-a', 'role-teacher'),
       ('user-teacher-b', 'role-teacher'),
       ('user-student-a', 'role-student'),
       ('user-student-b', 'role-student'),
       ('user-student-c', 'role-student');
