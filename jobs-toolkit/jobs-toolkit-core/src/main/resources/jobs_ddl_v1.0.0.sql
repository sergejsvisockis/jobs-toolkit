CREATE TABLE job_definition
(
    job_name        VARCHAR(45) NOT NULL,
    job_description VARCHAR(255),
    CONSTRAINT job_definition_pk PRIMARY KEY (job_name)
);

CREATE TABLE job_execution
(
    execution_id VARCHAR(36)                                                    NOT NULL,
    job_name     VARCHAR(45)                                                    NOT NULL,
    last_run     TIMESTAMP DEFAULT CURRENT_TIMESTAMP                            NOT NULL,
    state        VARCHAR(9) CHECK (state IN ('RUNNING', 'COMPLETED', 'FAILED')) NOT NULL,
    CONSTRAINT job_execution_pk PRIMARY KEY (execution_id),
    CONSTRAINT job_definition_fk FOREIGN KEY (job_name)
        REFERENCES job_definition (job_name)
);

CREATE TABLE lock_metadata
(
    lock_id   VARCHAR(36)                         NOT NULL,
    job_name  VARCHAR(45)                         NOT NULL,
    locked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT lock_metadata_pk PRIMARY KEY (lock_id),
    CONSTRAINT job_definition_fk FOREIGN KEY (job_name)
        REFERENCES job_definition (job_name)
)