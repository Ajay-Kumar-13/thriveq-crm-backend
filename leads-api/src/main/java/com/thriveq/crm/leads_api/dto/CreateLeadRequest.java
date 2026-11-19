package com.thriveq.crm.leads_api.dto;

import java.util.UUID;

public record CreateLeadRequest (
    String name,
    String company,
    String email,
    String stage,
    UUID owner_id
){};