-- db/migration/V2__seed.sql

INSERT INTO auth.roles (name) VALUES ('ADMIN'), ('MANAGER'), ('AGENT');

INSERT INTO auth.permissions (name) VALUES
    ('lead:read'), ('lead:write'), ('lead:delete'), ('rbac:manage');

-- ADMIN: everything
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permissions p WHERE r.name = 'ADMIN';

-- MANAGER: read, write, delete — no rbac:manage
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permissions p
WHERE r.name = 'MANAGER' AND p.name IN ('lead:read', 'lead:write', 'lead:delete');

-- AGENT: read, write
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permissions p
WHERE r.name = 'AGENT' AND p.name IN ('lead:read', 'lead:write');

-- bootstrap admin — password: admin123 (bcrypt cost 10)
-- CHANGE THIS. Generate your own with BCryptPasswordEncoder.
INSERT INTO auth.users (id, email, password_hash)
VALUES ('11111111-1111-1111-1111-111111111111',
        'admin@crm.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

INSERT INTO auth.user_roles (user_id, role_id)
SELECT '11111111-1111-1111-1111-111111111111', id FROM auth.roles WHERE name = 'ADMIN';