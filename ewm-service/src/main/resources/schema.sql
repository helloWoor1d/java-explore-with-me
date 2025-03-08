CREATE TABLE IF NOT EXISTS users (
     id    BIGINT GENERATED ALWAYS AS IDENTITY,
     name  CHARACTER VARYING (250)  NOT NULL,
     email CHARACTER VARYING (254) NOT NULL,

     CONSTRAINT pk_users
         PRIMARY KEY (id),
     CONSTRAINT uniq_users_email
         UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
    id   BIGINT GENERATED ALWAYS AS IDENTITY,
    name CHARACTER VARYING (50)  NOT NULL,

    CONSTRAINT pk_categories
        PRIMARY KEY (id),
    CONSTRAINT uniq_categories_name
        UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY,
    title              CHARACTER VARYING (120)     NOT NULL,
    category_id        BIGINT                      NOT NULL,
    annotation         CHARACTER VARYING (2000)    NOT NULL,
    description        CHARACTER VARYING (7000)    NOT NULL,
    initiator_id       BIGINT                      NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    latitude           FLOAT                       NOT NULL,
    longitude          FLOAT                       NOT NULL,
    paid               BOOLEAN                     NOT NULL,
    state              CHARACTER VARYING(50)       NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    participant_limit  INTEGER DEFAULT (0)         NOT NULL,
    request_moderation BOOLEAN DEFAULT (TRUE)      NOT NULL,

    CONSTRAINT pk_events
        PRIMARY KEY (id),
    CONSTRAINT fk_events_category_id
        FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_events_initiator_id
        FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_requests (
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    event_id     BIGINT                      NOT NULL,
    requester_id BIGINT                      NOT NULL,
    status       VARCHAR(50)                 NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_event_requests
        PRIMARY KEY (id),
    CONSTRAINT fk_event_requests_event_id
        FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_event_requests_requester_id
        FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations (
    id     BIGINT GENERATED ALWAYS AS IDENTITY,
    title  CHARACTER VARYING(50) NOT NULL,
    pinned BOOLEAN               NOT NULL,
    CONSTRAINT pk_compilations
        PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events_compilations (
    id             BIGINT GENERATED ALWAYS AS IDENTITY,
    event_id       BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT pk_events_compilations
        PRIMARY KEY (id),
    CONSTRAINT fk_events_compilations_event_id
        FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_events_compilations_compilation_id
        FOREIGN KEY (compilation_id) REFERENCES compilations (id)
);
