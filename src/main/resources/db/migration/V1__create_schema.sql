-- Initial schema creation

-- Create base tables first (no dependencies)
CREATE TABLE IF NOT EXISTS problem (
  problem_id int NOT NULL AUTO_INCREMENT,
  public_problem_id varchar(50) NOT NULL DEFAULT '',
  question_title varchar(255) NOT NULL DEFAULT '',
  difficulty_level enum('Easy','Medium','Hard') NOT NULL,
  page_url varchar(500) NOT NULL DEFAULT '',
  PRIMARY KEY (problem_id),
  UNIQUE KEY public_problem_id (public_problem_id)
);

CREATE TABLE IF NOT EXISTS tag (
    tag_id INT NOT NULL AUTO_INCREMENT,
    tag_name VARCHAR(50) NOT NULL,
    PRIMARY KEY(tag_id)
);

CREATE TABLE IF NOT EXISTS topic (
	topic_id INT NOT NULL AUTO_INCREMENT,
	topic_name VARCHAR(50) NOT NULL UNIQUE,
	topic_rank TINYINT NOT NULL,
	PRIMARY KEY(topic_id)
);

CREATE TABLE IF NOT EXISTS weekdays (
    weekday_id INT NOT NULL AUTO_INCREMENT,
    weekday_name ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    target_problems INT NOT NULL,
    PRIMARY KEY (weekday_id)
);

-- Create user-related tables that need to be available before app_user
CREATE TABLE IF NOT EXISTS user_schedules (
    user_schedule_id INT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (user_schedule_id)
);

CREATE TABLE IF NOT EXISTS user_topics (
    user_topic_id INT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (user_topic_id)
);

-- Create app_user with references to user_schedules and user_topics
CREATE TABLE IF NOT EXISTS app_user (
  user_id int NOT NULL AUTO_INCREMENT,
  email varchar(255) NOT NULL UNIQUE,
  first_name varchar(255) DEFAULT NULL,
  last_name varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  public_id varchar(36) NOT NULL,
  role enum('ADMIN','USER') DEFAULT NULL,
  user_name varchar(255) NOT NULL UNIQUE,
  user_schedule_id INT,
  user_topic_id INT,
  token_version INT DEFAULT 0,
  token_version_updated_at DATETIME,
  PRIMARY KEY (user_id),
  FOREIGN KEY (user_schedule_id) REFERENCES user_schedules(user_schedule_id) ON DELETE SET NULL,
  FOREIGN KEY (user_topic_id) REFERENCES user_topics(user_topic_id) ON DELETE SET NULL
);

-- Create join tables and dependent tables
CREATE TABLE IF NOT EXISTS tag_problems (
  tag_problem_id INT NOT NULL AUTO_INCREMENT,
  problem_id INT NOT NULL,
  tag_id INT NOT NULL,
  PRIMARY KEY (tag_problem_id),
  FOREIGN KEY (tag_id) REFERENCES tag (tag_id) ON DELETE CASCADE,
  FOREIGN KEY (problem_id) REFERENCES problem (problem_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS topic_problems (
	topic_problem_id INT NOT NULL AUTO_INCREMENT,
	problem_id INT NOT NULL,
	topic_id INT NOT NULL,
	PRIMARY KEY (topic_problem_id),
	FOREIGN KEY (problem_id) REFERENCES problem (problem_id),
	FOREIGN KEY (topic_id) REFERENCES topic (topic_id)
);

CREATE TABLE IF NOT EXISTS user_problem_attempts (
  attempt_id int NOT NULL AUTO_INCREMENT,
  user_id int NOT NULL,
  problem_id int NOT NULL,
  start_time datetime NOT NULL,
  end_time datetime NOT NULL,
  difficulty_rating tinyint unsigned NOT NULL,
  notes text,
  PRIMARY KEY (attempt_id),
  KEY user_id (user_id),
  KEY problem_id (problem_id),
  FOREIGN KEY (user_id) REFERENCES app_user (user_id),
  FOREIGN KEY (problem_id) REFERENCES problem (problem_id)
);

CREATE TABLE IF NOT EXISTS user_problem_priorities (
	priority_id INT NOT NULL AUTO_INCREMENT, 
	priority_score DOUBLE NOT NULL, 
	last_calculation DATETIME NOT NULL, 
	last_attempted DATETIME, 
	user_id INT NOT NULL, 
	problem_id INT NOT NULL, 
	PRIMARY KEY(priority_id), 
	KEY user_id (user_id), 
	KEY problem_id (problem_id), 
	FOREIGN KEY (user_id) REFERENCES app_user (user_id), 
	FOREIGN KEY (problem_id) REFERENCES problem (problem_id)
);

CREATE TABLE IF NOT EXISTS user_problem_completion (
    completion_id INT NOT NULL AUTO_INCREMENT,
    completion_date DATETIME NOT NULL,
    user_id INT NOT NULL,
    problem_id INT NOT NULL,
    PRIMARY KEY (completion_id),
    KEY idx_user_completion (user_id),
    KEY idx_problem_completion (problem_id),
    FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (problem_id) REFERENCES problem(problem_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS problem_history (
    problem_history_id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    problem_id INT NOT NULL,
    view_timestamp DATETIME,
    PRIMARY KEY (problem_history_id),
    KEY idx_user_history (user_id),
    KEY idx_problem_history (problem_id),
    FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (problem_id) REFERENCES problem(problem_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_completion_predictions (
	prediction_id INT NOT NULL AUTO_INCREMENT,
	prediction_date DATETIME NOT NULL,
	predicted_completion_date DATETIME NOT NULL,
	user_id INT NOT NULL,
	PRIMARY KEY (prediction_id),
	FOREIGN KEY (user_id) REFERENCES app_user (user_id)
);

-- ElementCollection tables for user preferences
CREATE TABLE IF NOT EXISTS schedule_details (
    user_schedule_id INT NOT NULL,
    day_position INT NOT NULL,
    target_problem_count INT DEFAULT 0,
    PRIMARY KEY (user_schedule_id, day_position),
    FOREIGN KEY (user_schedule_id) REFERENCES user_schedules(user_schedule_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS topic_details (
    user_topic_id INT NOT NULL,
    topic_position INT NOT NULL,
    topic_rank TINYINT,
    PRIMARY KEY (user_topic_id, topic_position),
    FOREIGN KEY (user_topic_id) REFERENCES user_topics(user_topic_id) ON DELETE CASCADE
);

-- Seed data is loaded by SeedDataLoader Spring component on application startup
-- See: com.hackertracker.config.SeedDataLoader
