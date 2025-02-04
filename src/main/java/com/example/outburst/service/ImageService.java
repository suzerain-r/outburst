package com.example.outburst.service;


import com.example.outburst.exceptions.ImageNotFoundException;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageService {

    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    @Autowired
    public ImageService(ImageRepository imageRepository, UserRepository userRepository, PostRepository postRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }


    public Image uploadImageToUser(MultipartFile file, Principal principal) throws IOException{
        User user = getUserByPrincipal(principal);
        LOG.info("Upload image profile to User {}", user.getUsername());
        Image userProfileImage = imageRepository.findByUserId(user.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(userProfileImage)){
            imageRepository.delete(userProfileImage);
        }

        Image image = new Image();
        image.setUserId(user.getId());
        image.setImageBytes(compressBytes(file.getBytes()));
        image.setName(file.getOriginalFilename());
        return imageRepository.save(image);
    }

    public Image uploadImageToPost(MultipartFile file, Principal principal, Long postId) throws IOException{
        User user = getUserByPrincipal(principal);
        Post post = user.getPosts()
                        .stream()
                        .filter(p -> p.getId().equals(postId))
                        .collect(toSingleCollector());


        Image image = new Image();
        image.setPostId(post.getId());
        image.setImageBytes(compressBytes(file.getBytes()));
        image.setName(file.getOriginalFilename());

        LOG.info("Upload image to Post {}", post.getId());
        return imageRepository.save(image);
    }


    public Image getImageToUser(Principal principal){
        User user = getUserByPrincipal(principal);

        Image image = imageRepository.findByUserId(user.getId())
                .orElse(null);
        if(!ObjectUtils.isEmpty(image)){
            image.setImageBytes(decompressBytes(image.getImageBytes()));
        }

        return image;
    }

    public Image getImageToPost(Long postId){
        Image image = imageRepository.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Cannot find image to Post " + postId));

        if(!ObjectUtils.isEmpty(image)){
            image.setImageBytes(decompressBytes(image.getImageBytes()));
        }

        return image;

    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        }
        catch (IOException e){
            LOG.error("Cannot compress bytes");
        }
        System.out.println("Compressed Image Byte Size: " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        }
        catch (IOException | DataFormatException e){
            LOG.error("Cannot decompress bytes");
        }
        return outputStream.toByteArray();
    }


    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user " + username + " does not exist."));
    }


    private <T>Collector<T, ?, T>  toSingleCollector(){
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }

}
