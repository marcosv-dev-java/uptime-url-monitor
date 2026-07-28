CREATE TABLE tb_monitor(
   id BIGSERIAL PRIMARY KEY,
   user_id UUID NOT NULL REFERENCES tb_user(id),
   name VARCHAR(50),
   url VARCHAR(255) NOT NULL,
   interval_seconds INTEGER,
   last_checked_at TIMESTAMP WITH TIME ZONE,
   next_check_due TIMESTAMP WITH TIME ZONE,
   current_status VARCHAR(20) NOT NULL,
   active BOOLEAN NOT NULL
)