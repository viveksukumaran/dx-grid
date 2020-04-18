create table country (
 id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
 code varchar(2) NOT NULL,
 name varchar(100) CHARACTER SET utf8 NOT NULL
);

create table region (
 id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
 name varchar(50) NOT NULL
);

create table country_region (
 id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
 country int NOT NULL,
 region int NOT NULL,
 foreign key (country) references country(id) on delete cascade,
 foreign key (region) references region(id) on delete cascade
);
