
CREATE TABLE usr
(
  id          varchar(255) PRIMARY KEY,
  username       varchar(255) not null,
  password     varchar(255) not null,
  active boolean not null,
  role varchar(255)
);
