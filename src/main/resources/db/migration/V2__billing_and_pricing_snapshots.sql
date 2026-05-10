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

CREATE INDEX idx_pricing_snapshots_parking_plate_entry
    ON pricing_snapshots (parking_id, license_plate, entry_at);

CREATE INDEX idx_billing_records_parking_plate
    ON billing_records (parking_id, license_plate);
