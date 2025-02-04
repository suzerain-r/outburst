package com.example.outburst.service;


import com.example.outburst.dto.CommentDTO;
import com.example.outburst.exceptions.PostNotFoundException;
import com.example.outburst.mapper.CommentDtoMapper;
import com.example.outburst.model.entity.Comment;
import com.example.outburst.model.entity.Post;
import com.example.outburst.model.entity.User;
import com.example.outburst.repository.CommentRepository;
import com.example.outburst.repository.PostRepository;
import com.example.outburst.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Comment saveComment(Long postId, CommentDTO commentDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found for username: " + user.getUsername()));

        Comment comment = new Comment();

        comment.setPost(post);
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        comment.setMessage(commentDTO.getMessage());

        LOG.info("Saving Comment to Post {}", post.getId());

        return commentRepository.save(comment);

    }

    public List<Comment> getAllCommentForPost(Long postId) {
        Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));

        return commentRepository.findAllCommentByPost(post);
    }


    public void deleteComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.ifPresent(commentRepository::delete);

    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user " + username + " does not exist."));
    }
}
