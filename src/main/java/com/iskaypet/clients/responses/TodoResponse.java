package com.iskaypet.clients.responses;

import java.time.ZonedDateTime;

/**
 * API response model for a todo item.
 */
public class TodoResponse {

	/** Todo ID */
	public Long id;
	/** Todo title */
	public String title;
	/** Todo body content */
	public String body;
	/** Due date and time */
	public ZonedDateTime dueOn;
}
