create database likequest;
use likequest;
--  Creacion de tablas 
create table users (
username varchar(30) primary key 
);

create table videos (
id int primary key auto_increment,
url varchar(300) ,
username varchar(30) NOT NULL,
FOREIGN KEY (username) REFERENCES users(username)
	ON DELETE CASCADE -- si borro el usuario se borran todos sus videos en cascada 
);

CREATE TABLE party (
    pin VARCHAR(10) PRIMARY KEY,
    creator_username VARCHAR(30) NOT NULL,
    estado ENUM('esperando','jugando','terminada') DEFAULT 'esperando',
    FOREIGN KEY (creator_username) REFERENCES users(username)
        ON DELETE CASCADE
);

CREATE TABLE party_conf (
  pin VARCHAR(10) primary key,
  number_rounds int default 10,
  choice_time int default 30,
  max_users int default 5,
  join_later boolean default false,
  FOREIGN KEY (pin) REFERENCES party (pin) ON DELETE CASCADE
);

CREATE TABLE usersParty (
  username VARCHAR(30),
  pin VARCHAR(10),
  PRIMARY KEY (username, pin),
  FOREIGN KEY (username) REFERENCES users(username),
  FOREIGN KEY (pin) REFERENCES party(pin) ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS game_rounds (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    pin        VARCHAR(10)  NOT NULL,
    round      INT          NOT NULL,
    started    BOOLEAN      NOT NULL DEFAULT FALSE,
    started_at DATETIME     DEFAULT NULL,
    UNIQUE KEY uq_round (pin, round),
    FOREIGN KEY (pin) REFERENCES party(pin) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS game_answers (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    pin         VARCHAR(10)  NOT NULL,
    username    VARCHAR(30)  NOT NULL,
    round       INT          NOT NULL,
    answer      VARCHAR(30)  NOT NULL,
    is_correct  BOOLEAN      NOT NULL,
    answered_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_answer (pin, username, round),
    FOREIGN KEY (pin)      REFERENCES party(pin)    ON DELETE CASCADE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

-- Tabla para guardar las puntuaciones de cada partida
CREATE TABLE IF NOT EXISTS game_scores (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    pin           VARCHAR(20)  NOT NULL,
    username      VARCHAR(100) NOT NULL,
    points        INT          NOT NULL DEFAULT 0,
    correct_answers INT        NOT NULL DEFAULT 0,
    total_rounds  INT          NOT NULL DEFAULT 0,
    avg_response_time_ms DOUBLE NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_pin_username (pin, username),
	FOREIGN KEY (pin)      REFERENCES party(pin)    ON DELETE CASCADE,
	FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);









