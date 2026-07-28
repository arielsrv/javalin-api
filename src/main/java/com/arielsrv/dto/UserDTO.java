package com.arielsrv.dto;

import java.util.List;

public record UserDTO(Long userId, String name, String email, List<PostDTO> posts, List<TodoDTO> todos) {
}
