package com.example.outburst.repository;

import com.example.outburst.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image,Long> {

    Optional<Image> findByUserId(Long userId);
    Optional<Image> findByPostId(Long postId);
}
