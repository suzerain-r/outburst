package com.example.outburst.controller;

import com.example.outburst.dto.CommentDTO;
import com.example.outburst.facade.CommentFacade;
import com.example.outburst.model.entity.Comment;
import com.example.outburst.payload.response.MessageResponse;
import com.example.outburst.service.CommentService;
import com.example.outburst.validations.ResponseErrorValidator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/comment")
public class CommentController {

    private final CommentService commentService;
    private final CommentFacade commentFacade;
    private final ResponseErrorValidator responseErrorValidator;

    public CommentController(CommentService commentService, CommentFacade commentFacade, ResponseErrorValidator responseErrorValidator) {
        this.commentService = commentService;
        this.commentFacade = commentFacade;
        this.responseErrorValidator = responseErrorValidator;
    }


    @PostMapping("/{postId}/create")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDTO commentDTO,
                                                @PathVariable String postId,
                                                BindingResult bindingResult,
                                                Principal principal) {

        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        Comment comment = commentService.saveComment(Long.parseLong(postId), commentDTO, principal);
        CommentDTO createdComment = commentFacade.commentToCommentDTO(comment);
        return new ResponseEntity<>(createdComment, HttpStatus.OK);
    }

    @GetMapping("/{postId}/all")
    public ResponseEntity<List<CommentDTO>> getAllCommentsForPost(@PathVariable String postId) {
        List<CommentDTO> commentDTOList = commentService.getAllCommentForPost(Long.parseLong(postId))
                .stream()
                .map(commentFacade::commentToCommentDTO)
                .toList();

        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }


    @PostMapping("/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(Long.parseLong(commentId));
        return new ResponseEntity<>(new MessageResponse("Comment was deleted"), HttpStatus.OK);
    }

}
