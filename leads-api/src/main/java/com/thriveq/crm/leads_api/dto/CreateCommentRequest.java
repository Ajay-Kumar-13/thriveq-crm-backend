package com.thriveq.crm.leads_api.dto;

import java.util.UUID;

public record CreateCommentRequest (
    String comment,
    UUID authorId
){}
