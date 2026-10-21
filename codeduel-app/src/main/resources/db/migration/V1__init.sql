DROP TABLE IF EXISTS expected_outputs CASCADE;
DROP TABLE IF EXISTS submissions CASCADE;
DROP TABLE IF EXISTS match_players CASCADE;
DROP TABLE IF EXISTS matches CASCADE;
DROP TABLE IF EXISTS test_cases CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
        id serial PRIMARY KEY,
        username varchar(255) NOT NULL UNIQUE,
        email varchar(255) NOT NULL UNIQUE,
        role varchar(32) NOT NULL DEFAULT 'USER',
        password_hash varchar(255) NOT NULL,
        created_at timestamp with time zone NOT NULL DEFAULT now(),

        CONSTRAINT user_role_valid
            CHECK ( role IN ('USER', 'ADMIN') ),
        CONSTRAINT users_username_length
           CHECK (char_length(trim(username)) >= 3),
        CONSTRAINT users_email_format
           CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]{2,}$')
);

CREATE TABLE profiles (
        user_id integer PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
        rating integer NOT NULL DEFAULT 1000,
        wins integer NOT NULL DEFAULT 0,
        losses integer NOT NULL DEFAULT 0,

        CONSTRAINT profiles_rating_non_negative
          CHECK (rating >= 0),
        CONSTRAINT profiles_wins_non_negative
          CHECK (wins >= 0),
        CONSTRAINT profiles_losses_non_negative
          CHECK (losses >= 0)
);

CREATE TABLE tasks (
        id serial PRIMARY KEY,
        title varchar(255) NOT NULL,
        statement_md text NOT NULL,
        time_limit_ms bigint NOT NULL,
        active boolean NOT NULL DEFAULT true,

        CONSTRAINT tasks_title_not_blank
           CHECK (char_length(trim(title)) > 0),
        CONSTRAINT tasks_statement_not_blank
           CHECK (char_length(trim(statement_md)) > 0),
        CONSTRAINT tasks_time_limit_positive
           CHECK (time_limit_ms > 0)
);

CREATE TABLE test_cases (
        id serial PRIMARY KEY,
        task_id integer NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
        position integer NOT NULL,
        input text NOT NULL,
        is_example boolean NOT NULL DEFAULT false,

        CONSTRAINT test_cases_position_positive
            CHECK (position > 0),
        CONSTRAINT test_cases_task_position_unique
            UNIQUE (task_id, position)
);

CREATE TABLE expected_outputs (
        id serial PRIMARY KEY,
        test_case_id integer NOT NULL REFERENCES test_cases(id) ON DELETE CASCADE,
        expected_output text NOT NULL,

        CONSTRAINT expected_outputs_unique
          UNIQUE (test_case_id, expected_output)
);

CREATE TABLE matches (
        id          serial PRIMARY KEY,
        task_id     integer NOT NULL REFERENCES tasks(id) ON DELETE RESTRICT,
        status      varchar(255) NOT NULL,
        outcome     varchar(255),
        winner_id   integer REFERENCES users(id) ON DELETE SET NULL,
        created_at  timestamp with time zone NOT NULL DEFAULT now(),
        started_at  timestamp with time zone,
        finished_at timestamp with time zone,

        CONSTRAINT matches_status_valid
            CHECK (status IN ('ACTIVE', 'FINISHED', 'CANCELLED')),

        CONSTRAINT matches_outcome_valid
            CHECK (outcome IS NULL OR outcome IN ('WIN', 'DRAW', 'CANCELLED')),

        CONSTRAINT matches_started_after_created
            CHECK (started_at IS NULL OR started_at >= created_at),

        CONSTRAINT matches_finished_after_created
            CHECK (finished_at IS NULL OR finished_at >= created_at),

        CONSTRAINT matches_finished_after_started
            CHECK (finished_at IS NULL OR started_at IS NULL OR finished_at >= started_at),

        CONSTRAINT matches_active_has_started_at
            CHECK (status <> 'ACTIVE' OR started_at IS NOT NULL),

        CONSTRAINT matches_active_no_finished_at
            CHECK (status <> 'ACTIVE' OR finished_at IS NULL),
        CONSTRAINT matches_active_no_outcome
            CHECK (status <> 'ACTIVE' OR outcome IS NULL),
        CONSTRAINT matches_active_no_winner
            CHECK (status <> 'ACTIVE' OR winner_id IS NULL),

        CONSTRAINT matches_finished_has_finished_at
            CHECK (status <> 'FINISHED' OR finished_at IS NOT NULL),
        CONSTRAINT matches_finished_has_outcome
            CHECK (status <> 'FINISHED' OR outcome IN ('WIN', 'DRAW')),


        CONSTRAINT matches_finished_win_has_winner
            CHECK (status <> 'FINISHED' OR outcome <> 'WIN' OR winner_id IS NOT NULL),
        CONSTRAINT matches_finished_draw_no_winner
            CHECK (status <> 'FINISHED' OR outcome <> 'DRAW' OR winner_id IS NULL),

        CONSTRAINT matches_cancelled_has_outcome
            CHECK (status <> 'CANCELLED' OR outcome = 'CANCELLED'),
        CONSTRAINT matches_cancelled_no_winner
            CHECK (status <> 'CANCELLED' OR winner_id IS NULL),

        CONSTRAINT matches_winner_implies_win
            CHECK (winner_id IS NULL OR outcome = 'WIN'),

        CONSTRAINT matches_outcome_matches_status
            CHECK (
                (status = 'ACTIVE'    AND outcome IS NULL)               OR
                (status = 'FINISHED'  AND outcome IN ('WIN', 'DRAW'))    OR
                (status = 'CANCELLED' AND outcome = 'CANCELLED')
            )
);

CREATE TABLE match_players (
        id serial PRIMARY KEY,
        match_id integer NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
        user_id integer REFERENCES users(id) ON DELETE SET NULL,
        rating_at_start integer NOT NULL,
        rating_delta integer,

        CONSTRAINT match_players_rating_at_start_non_negative
           CHECK (rating_at_start >= 0),
        CONSTRAINT match_players_rating_delta_reasonable
           CHECK (rating_delta IS NULL OR rating_delta BETWEEN -10000 AND 10000)
);

CREATE UNIQUE INDEX match_players_match_user_unique
    ON match_players (match_id, user_id)
    WHERE user_id IS NOT NULL;

-- Если будем определять точное время, нужно добавить timeMs столбец
CREATE TABLE submissions (
        id serial PRIMARY KEY,
        match_id integer NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
        user_id integer REFERENCES users(id) ON DELETE SET NULL,
        code text NOT NULL,
        verdict varchar(255),
        failed_test integer,
        compile_error text,
        submitted_at timestamp with time zone NOT NULL DEFAULT now(),

        CONSTRAINT submissions_code_not_blank
            CHECK (char_length(trim(code)) > 0),
        CONSTRAINT submissions_code_size
            CHECK (char_length(code) <= 65536),

        CONSTRAINT submissions_verdict_valid
            CHECK (
                verdict IS NULL OR verdict IN (
                    'ACCEPTED',
                    'WRONG_ANSWER',
                    'COMPILATION_ERROR',
                    'RUNTIME_ERROR',
                    'TIME_LIMIT_EXCEEDED',
                    'MEMORY_LIMIT_EXCEEDED',
                    'INTERNAL_ERROR'
                )
            ),

        CONSTRAINT submissions_failed_test_positive
            CHECK (failed_test IS NULL OR failed_test > 0),
        CONSTRAINT submissions_accepted_no_failed_test
            CHECK (verdict <> 'ACCEPTED' OR failed_test IS NULL),
        CONSTRAINT submissions_wa_has_failed_test
            CHECK (verdict <> 'WRONG_ANSWER' OR failed_test IS NOT NULL),

        CONSTRAINT submissions_compile_error_only_on_ce
            CHECK (compile_error IS NULL OR verdict = 'COMPILATION_ERROR'),
        CONSTRAINT submissions_compile_error_required_on_ce
            CHECK (verdict <> 'COMPILATION_ERROR' OR compile_error IS NOT NULL)
);

CREATE INDEX idx_matches_task_id            ON matches(task_id);
CREATE INDEX idx_matches_status             ON matches(status);
CREATE INDEX idx_matches_winner_id          ON matches(winner_id);
CREATE INDEX idx_match_players_user_id      ON match_players(user_id);
CREATE INDEX idx_match_players_match_id     ON match_players(match_id);
CREATE INDEX idx_submissions_match_id       ON submissions(match_id);
CREATE INDEX idx_submissions_user_id        ON submissions(user_id);
CREATE INDEX idx_submissions_verdict        ON submissions(verdict);
CREATE INDEX idx_test_cases_task_id         ON test_cases(task_id);
CREATE INDEX idx_expected_outputs_test_case ON expected_outputs(test_case_id);
CREATE INDEX idx_tasks_active               ON tasks(active) WHERE active = true;