package com.thriveq.crm.leads_api.dto;

import java.util.List;
import java.util.UUID;

public record LeadsResponse(
    UUID id,
    String name,
    String company,
    String email,
    String stage,
    String owner_id,
    String created_at,
    List<CommentsResponse> comments
) {
    
}
