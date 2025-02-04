package com.example.outburst.mapper;

import com.example.outburst.dto.CommentDTO;
import com.example.outburst.model.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentDtoMapper {

    CommentDTO commentToCommentDTO(Comment comment);
    Comment commentDTOToComment(CommentDTO commentDTO);
}
