-- db/migration/V1__schema.sql

CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS crm;

-- ─────────── auth: owned by auth-service + authz-service ───────────

CREATE TABLE auth.users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    failed_count  INT NOT NULL DEFAULT 0,
    locked_until  TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE auth.roles (
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE auth.permissions (
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE          -- lead:read, lead:delete, rbac:manage
);

CREATE TABLE auth.user_roles (
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    role_id INT  NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE auth.role_permissions (
    role_id       INT NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
    permission_id INT NOT NULL REFERENCES auth.permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- ─────────── crm: owned by leads-api ───────────

CREATE TABLE crm.leads (
    id         BIGSERIAL PRIMARY KEY,
    name       TEXT NOT NULL,
    company    TEXT,
    email      TEXT,
    stage      TEXT NOT NULL DEFAULT 'NEW',
    owner_id   UUID NOT NULL,          -- deliberately NOT a FK across schemas
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_leads_owner ON crm.leads(owner_id);