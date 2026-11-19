package com.thriveq.crm.leads_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thriveq.crm.leads_api.dto.CommentsResponse;
import com.thriveq.crm.leads_api.dto.CreateCommentRequest;
import com.thriveq.crm.leads_api.service.CommentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController 
@RequestMapping("api/leads/{leadId}/comments")
@RequiredArgsConstructor 

public class CommentController {

    private final CommentService commentService;

    @GetMapping("")
    public Mono<List<CommentsResponse>> getComments(@PathVariable UUID leadId) {
        return commentService.getCommentsByLeadId(leadId);
    }
    
    @PostMapping("")
    public Mono<CommentsResponse> addComment(@PathVariable UUID leadId, @RequestBody CreateCommentRequest req) {
        return commentService.addCommentForLead(leadId, req);
    }
    
}
