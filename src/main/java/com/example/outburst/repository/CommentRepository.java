package com.example.outburst.repository;

import com.example.outburst.model.entity.Comment;
import com.example.outburst.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllCommentByPost(Post post);

    Comment findCommentByIdAndUserId(Long id, Long userId);
}
