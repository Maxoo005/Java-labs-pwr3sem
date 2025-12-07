PRAGMA foreign_keys = ON;


-- shop

CREATE TABLE IF NOT EXISTS shop (
                                    id               INTEGER PRIMARY KEY AUTOINCREMENT,
                                    name             TEXT NOT NULL,
                                    chairs           INTEGER NOT NULL DEFAULT 3,
                                    owner_client_id  INTEGER,
                                    FOREIGN KEY (owner_client_id) REFERENCES client(id)
);


-- client
-- role: CLIENT | EMPLOYEE | CASHIER | OWNER

CREATE TABLE IF NOT EXISTS client (
                                      id      INTEGER PRIMARY KEY AUTOINCREMENT,
                                      shop_id INTEGER,
                                      name    TEXT NOT NULL,
                                      role    TEXT NOT NULL,           -- CLIENT/EMPLOYEE/CASHIER/OWNER
                                      FOREIGN KEY (shop_id) REFERENCES shop(id)
);


-- (globalny cennik)

CREATE TABLE IF NOT EXISTS service_price (
                                             id          INTEGER PRIMARY KEY AUTOINCREMENT,
                                             name        TEXT NOT NULL,
                                             price_cents INTEGER NOT NULL
);


-- reservation

CREATE TABLE IF NOT EXISTS reservation (
                                           id              INTEGER PRIMARY KEY AUTOINCREMENT,
                                           shop_id         INTEGER NOT NULL,
                                           service_name    TEXT NOT NULL,
                                           client_id       INTEGER NOT NULL,
                                           employee_id     INTEGER,
                                           date_iso        TEXT NOT NULL,
                                           time_iso        TEXT NOT NULL,
                                           status          TEXT NOT NULL,
                                           created_at_utc  TEXT NOT NULL,
                                           updated_at_utc  TEXT NOT NULL,
                                           price_cents     INTEGER NOT NULL,
                                           FOREIGN KEY (shop_id)     REFERENCES shop(id),
                                           FOREIGN KEY (client_id)   REFERENCES client(id),
                                           FOREIGN KEY (employee_id) REFERENCES client(id)
);


-- time_state

CREATE TABLE IF NOT EXISTS time_state(
                                         id INTEGER PRIMARY KEY CHECK (id=1),
                                         current_utc TEXT NOT NULL
);
INSERT OR IGNORE INTO time_state(id, current_utc) VALUES (1, datetime('now'));


-- Indeksy

CREATE INDEX IF NOT EXISTS idx_client_shop_id        ON client(shop_id);
CREATE INDEX IF NOT EXISTS idx_reservation_shop_id   ON reservation(shop_id);
CREATE INDEX IF NOT EXISTS idx_reservation_client_id ON reservation(client_id);
CREATE INDEX IF NOT EXISTS idx_reservation_employee  ON reservation(employee_id);
CREATE INDEX IF NOT EXISTS idx_service_price_name    ON service_price(name);
