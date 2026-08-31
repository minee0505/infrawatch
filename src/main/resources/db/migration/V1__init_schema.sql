create table member (
    id          bigserial primary key,
    email       varchar(100) not null unique,
    password    varchar(100) not null,
    name        varchar(50)  not null,
    role        varchar(20)  not null,
    created_at  timestamp    not null,
    updated_at  timestamp    not null
);

create table facility (
    id          bigserial primary key,
    name        varchar(100) not null,
    type        varchar(20)  not null,
    location    varchar(200),
    description varchar(500),
    created_at  timestamp    not null,
    updated_at  timestamp    not null
);

create table sensor (
    id             bigserial primary key,
    facility_id    bigint       not null references facility (id) on delete cascade,
    type           varchar(20)  not null,
    name           varchar(100) not null,
    unit           varchar(20)  not null,
    threshold_min  double precision,
    threshold_max  double precision,
    status         varchar(20)  not null,
    created_at     timestamp    not null,
    updated_at     timestamp    not null
);
create index idx_sensor_facility_id on sensor (facility_id);

create table sensor_reading (
    id           bigserial primary key,
    sensor_id    bigint           not null references sensor (id) on delete cascade,
    value        double precision not null,
    measured_at  timestamp        not null,
    created_at   timestamp        not null
);
create index idx_sensor_reading_sensor_id_measured_at on sensor_reading (sensor_id, measured_at desc);

create table alert (
    id           bigserial primary key,
    sensor_id    bigint       not null references sensor (id) on delete cascade,
    reading_id   bigint references sensor_reading (id) on delete set null,
    level        varchar(20)  not null,
    message      varchar(300) not null,
    status       varchar(20)  not null,
    resolved_at  timestamp,
    resolved_by  bigint references member (id) on delete set null,
    created_at   timestamp    not null,
    updated_at   timestamp    not null
);
create index idx_alert_status on alert (status);
create index idx_alert_sensor_id_status on alert (sensor_id, status);

create table audit_log (
    id            bigserial primary key,
    actor_id      bigint references member (id) on delete set null,
    action        varchar(50)  not null,
    resource_type varchar(30)  not null,
    resource_id   varchar(50),
    detail        varchar(500),
    created_at    timestamp    not null
);
create index idx_audit_log_created_at on audit_log (created_at desc);
