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
    spot_id VARCHAR(128) NOT NULL,
    sector VARCHAR(64) NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    status VARCHAR(32) NOT NULL,
    occupied_by_plate VARCHAR(16),
    PRIMARY KEY (id),
    CONSTRAINT uk_spots_spot_id UNIQUE (spot_id)
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
    spot_id VARCHAR(128) NOT NULL,
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
    PRIMARY KEY (id),
    CONSTRAINT uk_parking_events_event_id UNIQUE (event_id)
);

CREATE INDEX idx_spots_sector ON spots (sector);
CREATE INDEX idx_parking_sessions_parking_id ON parking_sessions (parking_id);
CREATE INDEX idx_parking_spot_snapshots_spot_id ON parking_spot_snapshots (spot_id);
CREATE INDEX idx_parking_spot_snapshots_sector ON parking_spot_snapshots (sector);
CREATE INDEX idx_parking_spot_snapshots_parking_session_id ON parking_spot_snapshots (parking_session_id);
CREATE INDEX idx_parking_events_event_id ON parking_events (event_id);
CREATE INDEX idx_parking_events_parking_id ON parking_events (parking_id);
CREATE INDEX idx_parking_events_parking_id_id ON parking_events (parking_id, id);
