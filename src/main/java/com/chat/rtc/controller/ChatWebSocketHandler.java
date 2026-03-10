package com.chat.rtc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
	private final List<WebSocketSession> sessions = new ArrayList<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession webSocketSession){
		sessions.add(webSocketSession);
	}
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
		for (WebSocketSession s : sessions){
			if (s.isOpen()){
				s.sendMessage(message);
			}
		}
	}
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
		sessions.remove(session);
	}
}
