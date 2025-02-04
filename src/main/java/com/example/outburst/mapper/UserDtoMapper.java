package com.example.outburst.mapper;

import com.example.outburst.dto.UserDTO;
import com.example.outburst.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);
}
