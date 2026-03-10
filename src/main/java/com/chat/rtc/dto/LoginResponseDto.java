package com.chat.rtc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDto {
	private boolean success;
	private String message;
}
