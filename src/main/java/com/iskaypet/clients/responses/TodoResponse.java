package com.iskaypet.clients.responses;

import java.time.ZonedDateTime;

public class TodoResponse {

	public Long id;
	public String title;
	public String body;
	//@JsonProperty("due_on")
	public ZonedDateTime dueOn;
}
