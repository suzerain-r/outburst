package com.example.outburst.controller;

import com.example.outburst.dto.PostDTO;
import com.example.outburst.facade.PostFacade;
import com.example.outburst.model.entity.Post;
import com.example.outburst.payload.response.MessageResponse;
import com.example.outburst.service.PostService;
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
@RequestMapping("api/post")

public class PostController {
    private final PostService postService;
    private final PostFacade postFacade;
    private final ResponseErrorValidator responseErrorValidator;

    public PostController(PostService postService, PostFacade postFacade, ResponseErrorValidator responseErrorValidator) {
        this.postService = postService;
        this.postFacade = postFacade;
        this.responseErrorValidator = responseErrorValidator;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDTO postDTO, BindingResult bindingResult, Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        Post post = postService.createPost(postDTO, principal);
        PostDTO createdPost = postFacade.postToPostDTO(post);
        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts(){
        List<PostDTO> postDTOList = postService.getAllPosts()
                .stream()
                .map(postFacade::postToPostDTO)
                .toList();
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }

    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDTO>> getAllPostsForUser (Principal principal){
        List<PostDTO> postDTOList = postService.getAllPostsForUser(principal)
                .stream()
                .map(postFacade::postToPostDTO)
                .toList();

        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }


    @PostMapping("/{postId}/{username}/like")
    public ResponseEntity<Object> likePost(@PathVariable String postId, @PathVariable String username){
        Post post = postService.likePost(Long.parseLong(postId), username);

        PostDTO postDTO = postFacade.postToPostDTO(post);
        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @PostMapping("/{postId}/delete")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable String postId, Principal principal){
        postService.deletePost(Long.parseLong(postId), principal);
        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }
}
