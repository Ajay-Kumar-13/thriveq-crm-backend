package com.thriveq.crm.leads_api.dto;

import java.util.List;

public record LeadsResponse(
    String name,
    String company,
    String email,
    String stage,
    String owner_id,
    String created_at,
    List<CommentsResponse> comments
) {
    
}
