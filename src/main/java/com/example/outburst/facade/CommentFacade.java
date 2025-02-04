package com.example.outburst.facade;

import com.example.outburst.dto.CommentDTO;
import com.example.outburst.model.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentFacade {
    public CommentDTO commentToCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setUsername(comment.getUsername());
        commentDTO.setMessage(comment.getMessage());
        return commentDTO;
    }
}
