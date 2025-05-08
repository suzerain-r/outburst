package com.example.outburst.controller;

import com.example.outburst.model.entity.Image;
import com.example.outburst.payload.response.MessageResponse;
import com.example.outburst.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("api/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException {
        imageService.uploadImageToUser(file, principal);
        return new ResponseEntity<>(new MessageResponse("Image uploaded successfully"), HttpStatus.OK);
    }


    @PostMapping("/{postId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@PathVariable String postId, @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException {
        imageService.uploadImageToPost(file, principal, Long.parseLong(postId));
        return new ResponseEntity<>(new MessageResponse("Image uploaded successfully"), HttpStatus.OK);
    }

    @GetMapping("/profileImage")
    public ResponseEntity<Image> getImageForUser(Principal principal) {
        Image userImage = imageService.getImageToUser(principal);
        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }
    @GetMapping("/{postId}/image")
    public ResponseEntity<Image> getPostImages(@PathVariable String postId) {
        Image postImage = imageService.getImageToPost(Long.parseLong(postId));
        return new ResponseEntity<>(postImage, HttpStatus.OK);
    }
}

