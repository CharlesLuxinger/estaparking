CREATE TABLE garages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sector VARCHAR(64) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    max_capacity INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_garages_sector UNIQUE (sector)
);

CREATE TABLE spots (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sector VARCHAR(64) NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    status VARCHAR(32) NOT NULL,
    occupied_by_plate VARCHAR(16),
    PRIMARY KEY (id)
);

CREATE TABLE parking_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    parking_id VARCHAR(128) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_parking_sessions_parking_id UNIQUE (parking_id)
);

CREATE TABLE parking_spot_snapshots (
    id BIGINT NOT NULL AUTO_INCREMENT,
    parking_session_id BIGINT NOT NULL,
    spot_id BIGINT NOT NULL,
    sector VARCHAR(64) NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    status VARCHAR(32) NOT NULL,
    occupied_by_plate VARCHAR(16),
    PRIMARY KEY (id),
    CONSTRAINT fk_parking_spot_snapshots_parking_session
        FOREIGN KEY (parking_session_id) REFERENCES parking_sessions(id)
        ON DELETE CASCADE
);

CREATE TABLE parking_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    event_id VARCHAR(128) NOT NULL,
    parking_id VARCHAR(128) NOT NULL,
    license_plate VARCHAR(16) NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_parking_events_event_id UNIQUE (event_id)
);

CREATE TABLE billing_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    billing_id VARCHAR(128) NOT NULL,
    license_plate VARCHAR(16) NOT NULL,
    sector VARCHAR(64) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    exit_time DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_billing_transactions_billing_id UNIQUE (billing_id)
);

CREATE TABLE pricing_snapshots (
    id BIGINT NOT NULL AUTO_INCREMENT,
    parking_id VARCHAR(128) NOT NULL,
    license_plate VARCHAR(16) NOT NULL,
    sector VARCHAR(64) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    occupancy_percentage_at_entry DECIMAL(5,2) NOT NULL,
    multiplier_at_entry DECIMAL(5,2) NOT NULL,
    entry_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE billing_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    parking_id VARCHAR(128) NOT NULL,
    license_plate VARCHAR(16) NOT NULL,
    sector VARCHAR(64) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    parked_minutes BIGINT NOT NULL,
    billed_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_billing_records_parking_plate UNIQUE (parking_id, license_plate)
);

CREATE INDEX idx_spots_sector ON spots (sector);
CREATE INDEX idx_parking_sessions_parking_id ON parking_sessions (parking_id);
CREATE INDEX idx_parking_spot_snapshots_spot_id ON parking_spot_snapshots (spot_id);
CREATE INDEX idx_parking_spot_snapshots_sector ON parking_spot_snapshots (sector);
CREATE INDEX idx_parking_spot_snapshots_parking_session_id ON parking_spot_snapshots (parking_session_id);
CREATE INDEX idx_parking_events_event_id ON parking_events (event_id);
CREATE INDEX idx_parking_events_parking_id ON parking_events (parking_id);
CREATE INDEX idx_parking_events_parking_id_id ON parking_events (parking_id, id);
CREATE INDEX idx_billing_transactions_sector_exit_time ON billing_transactions (sector, exit_time);
CREATE INDEX idx_pricing_snapshots_parking_plate_entry ON pricing_snapshots (parking_id, license_plate, entry_at);
CREATE INDEX idx_billing_records_parking_plate ON billing_records (parking_id, license_plate);
