CREATE TABLE requests (
    uuid varchar(40) primary key,
    url varchar(200) not null,
    request_time timestamp not null,
    response_code int not null,
    ip_address varchar(16),
    country_code char(2),
    isp varchar(200),
    time_lapsed int not null
);