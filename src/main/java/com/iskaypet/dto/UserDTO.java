package com.iskaypet.dto;

import java.util.List;

/**
 * Data Transfer Object for a user, including their posts and todos.
 */
public class UserDTO {
	/** User ID */
	public Long userId;
	/** User name */
	public String name;
	/** User email */
	public String email;
	/** List of posts associated with the user */
	public List<PostDTO> posts;
	/** List of todos associated with the user */
	public List<TodoDTO> todos;
}
