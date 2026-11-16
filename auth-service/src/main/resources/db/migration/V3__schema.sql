
CREATE TABLE crm.comments (
    id         BIGSERIAL PRIMARY KEY,
    lead_id    BIGINT NOT NULL REFERENCES crm.leads(id) ON DELETE CASCADE,
    author_id  UUID NOT NULL,          -- deliberately NOT a FK across schemas
    content    TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
)