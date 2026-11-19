package com.thriveq.crm.leads_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.thriveq.crm.leads_api.dto.CommentsResponse;
import com.thriveq.crm.leads_api.model.Comment;
import com.thriveq.crm.leads_api.repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service 
@RequiredArgsConstructor 
public class CommentService {
    private final CommentRepository commentRepository;

    public Mono<List<CommentsResponse>> getCommentsByLeadId(UUID leadId) {
        return commentRepository.findByLeadId(leadId)
        .map(comment -> new CommentsResponse(comment.getId(), comment.getLeadId(), comment.getAuthorId(), comment.getContent(), comment.getCreatedAt()))
        .collectList();
    }

    public Mono<CommentsResponse> addCommentForLead(UUID leadId, com.thriveq.crm.leads_api.dto.CreateCommentRequest req) {
        return commentRepository.save(
            new Comment() {{
                setLeadId(leadId);
                setContent(req.comment());
                setAuthorId(req.authorId());
            }}
        )
        .map(comment -> new CommentsResponse(comment.getId(), comment.getLeadId(), comment.getAuthorId(), comment.getContent(), comment.getCreatedAt()));
    }
}
