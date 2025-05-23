-- Ensure UUID extension is enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Sample Data

-- Insert admin user if not exists
INSERT INTO users (id, username, password_hash, full_name, role, created_at)
VALUES (
  uuid_generate_v4(),
  'admin',
  '$2y$10$2WGq2rtCp/Q3u2WkD6z2Q.AWe6Y.iOvPMxISriPgw73WjmPX/PL3O',
  'Default Admin',
  'ADMIN',
  NOW()
)
ON CONFLICT (username) DO NOTHING;


-- Insert equipment categories if not exists
INSERT INTO equipment_categories (id, name, description, unit_of_measure)
SELECT gen_random_uuid(), name, description, unit
FROM (
  VALUES 
    ('Weapons', 'Firearms and Ammunition', 'units'),
    ('Communication Devices', 'Radios and Signal Equipments', 'pieces'),
    ('Medical Supplies', 'Bandages, First Aid Kits', 'packs')
) AS vals(name, description, unit)
WHERE NOT EXISTS (
  SELECT 1 FROM equipment_categories ec WHERE ec.name = vals.name
);


-- Insert equipments if not exists
INSERT INTO equipments (id, name, description, equipment_category_id)
SELECT 
  gen_random_uuid(), vals.name, vals.description, ec.id
FROM (
  VALUES 
    ('AK-47 Rifle', 'Automatic rifle used in combat', 'Weapons'),
    ('Motorola Radio', 'Portable radio device', 'Communication Devices'),
    ('First Aid Kit', 'Medical emergency supplies', 'Medical Supplies')
) AS vals(name, description, cat_name)
JOIN equipment_categories ec ON ec.name = vals.cat_name
WHERE NOT EXISTS (
  SELECT 1 FROM equipments e WHERE e.name = vals.name
);

-- Insert bases if not exists
INSERT INTO bases (id, name, location, created_at)
SELECT gen_random_uuid(), name, location, NOW()
FROM (
  VALUES 
    ('Base Alpha', 'New Delhi'),
    ('Base Bravo', 'Mumbai')
) AS vals(name, location)
WHERE NOT EXISTS (
  SELECT 1 FROM bases b WHERE b.name = vals.name
);

