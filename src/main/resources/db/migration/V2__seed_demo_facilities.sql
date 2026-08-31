-- 데모 시연용 시설물과 센서. 총관리자 계정은 애플리케이션 기동 시 AdminSeeder 가 만든다.
insert into facility (id, name, type, location, description, created_at, updated_at) values
    (1, '하남 검단산 배수교량', 'BRIDGE', '경기 하남시 검단산로', '노후 교량, 상판 기울기·진동 상시 모니터링', now(), now()),
    (2, '창우동 3구역 옹벽', 'RETAINING_WALL', '경기 하남시 창우동', '집중호우 시 붕괴 위험 구간', now(), now()),
    (3, '검단산 등산로 배수터널', 'TUNNEL', '경기 하남시 검단산', '우기 침수 이력 있음', now(), now());

select setval('facility_id_seq', (select max(id) from facility));

insert into sensor (id, facility_id, type, name, unit, threshold_min, threshold_max, status, created_at, updated_at) values
    (1, 1, 'TILT',       '교량 상판 기울기 센서 1',  'deg',  null, 5.0,   'ACTIVE', now(), now()),
    (2, 1, 'VIBRATION',  '교량 상판 진동 센서 1',    'mm/s', null, 10.0,  'ACTIVE', now(), now()),
    (3, 2, 'CRACK',      '옹벽 균열 센서 1',         'mm',   null, 3.0,   'ACTIVE', now(), now()),
    (4, 2, 'HUMIDITY',   '옹벽 배면 습도 센서 1',    '%',    null, 80.0,  'ACTIVE', now(), now()),
    (5, 3, 'FLOOD',      '터널 침수 수위 센서 1',    'cm',   null, 30.0,  'ACTIVE', now(), now()),
    (6, 3, 'HUMIDITY',   '터널 내부 습도 센서 1',    '%',    null, 85.0,  'ACTIVE', now(), now());

select setval('sensor_id_seq', (select max(id) from sensor));
