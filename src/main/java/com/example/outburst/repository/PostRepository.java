package com.example.outburst.repository;

import com.example.outburst.model.entity.Post;
import com.example.outburst.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Optional<Post> findPostByIdAndUser(Long id, User user);

    List<Post> findAllByUserOrderByCreatedTimeDesc(User user);

    List<Post> findAllByOrderByCreatedTimeDesc();

}
