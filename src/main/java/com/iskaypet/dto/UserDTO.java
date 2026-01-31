package com.arielsrv.dto;

import java.util.List;

public class UserDTO {
	public Long userId;
	public String name;
	public String email;
	public List<PostDTO> posts;
	public List<TodoDTO> todos;
}
