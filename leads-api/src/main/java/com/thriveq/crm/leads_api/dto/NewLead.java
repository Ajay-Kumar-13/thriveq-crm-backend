package com.thriveq.crm.leads_api.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class NewLead {
    private String name;
    private String company;
    private String email;
    private String stage;
    private UUID owner_id;
}