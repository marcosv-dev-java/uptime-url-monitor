CREATE TABLE tb_check_result(
    id BIGSERIAL PRIMARY KEY,
    monitor_id BIGINT REFERENCES tb_monitor(id),
    checked_at TIMESTAMP WITH TIME ZONE NOT NULL,
    http_status INTEGER,
    response_time_ms BIGINT NOT NULL,
    success BOOLEAN NOT NULL,
    error_message VARCHAR(255)

)