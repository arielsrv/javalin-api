package com.arielsrv.clients.responses;

import java.time.ZonedDateTime;

public record TodoResponse(Long id, String title, String body, ZonedDateTime dueOn) {
}
