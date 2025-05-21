-- Ensure UUID extension is enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

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

