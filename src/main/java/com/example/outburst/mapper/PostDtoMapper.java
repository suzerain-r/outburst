package com.example.outburst.mapper;


import com.example.outburst.dto.PostDTO;
import com.example.outburst.model.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostDtoMapper {
    PostDTO postToPostDTO(Post post);
    Post postDTOToPost(PostDTO postDTO);
}
