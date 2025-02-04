package com.example.outburst.service;

import com.example.outburst.dto.PostDTO;
import com.example.outburst.exceptions.PostNotFoundException;
import com.example.outburst.model.entity.Image;
import com.example.outburst.model.entity.Post;
import com.example.outburst.model.entity.User;
import com.example.outburst.repository.ImageRepository;
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
public class PostService {

    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;


    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }


    public Post createPost(PostDTO postDTO, Principal principal){
        User user = getUserByPrincipal(principal);
        Post post = new Post();
        post.setUser(user);
        post.setTitle(postDTO.getTitle());
        post.setCaption(postDTO.getCaption());
        post.setLikes(0);
        LOG.info("Saving post for User: {}", user.getUsername());
        return postRepository.save(post);
    }


    public List<Post> getAllPosts(){
        return postRepository.findAllByOrderByCreatedTimeDesc();
    }

    public Post getPostById(Long postId, Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findPostByIdAndUser(postId, user)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found for username: " + user.getUsername()));
    }

    public List<Post> getAllPostsForUser(Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findAllByUserOrderByCreatedTimeDesc(user);
    }

    public Post likePost(Long postId, String username){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
        Optional<String> userLiked = post.getLikedUsers()
                .stream()
                .filter(u -> u.equals(username)).findAny();

        if(userLiked.isPresent()){
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(username);
        }
        else{
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(username);
        }

        return postRepository.save(post);
    }

    public void deletePost(Long postId, Principal principal){
        Post post = getPostById(postId, principal);
        Optional<Image> image = imageRepository.findByPostId(post.getId());
        postRepository.delete(post);
        image.ifPresent(imageRepository::delete);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user " + username + " does not exist."));
    }
}
