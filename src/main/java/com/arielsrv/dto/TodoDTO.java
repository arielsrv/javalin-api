package com.arielsrv.dto;

import java.time.ZonedDateTime;

public record TodoDTO(Long id, String title, String body, ZonedDateTime dueOn) {
}
