package com.chat.rtc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterResponseDto {
	private boolean success;
	private String message;
}
