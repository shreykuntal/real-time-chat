package com.chat.rtc.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MessageDto {
	private String username;
	private String message;
}
