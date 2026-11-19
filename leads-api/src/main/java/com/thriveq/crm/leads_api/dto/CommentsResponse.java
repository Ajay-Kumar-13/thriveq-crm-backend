package com.thriveq.crm.leads_api.dto;

import java.util.UUID;

public record CommentsResponse(
    UUID id,
    UUID leadId,
    UUID authorId,
    String content,
    String createdAt
){};