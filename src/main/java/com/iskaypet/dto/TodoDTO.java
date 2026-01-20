package com.iskaypet.dto;

import java.time.ZonedDateTime;

/**
 * Data Transfer Object for a todo item.
 */
public class TodoDTO {

	/** Todo ID */
	public Long id;
	/** Todo title */
	public String title;
	/** Todo body content */
	public String body;
	/** Due date and time */
	public ZonedDateTime dueOn;
}
