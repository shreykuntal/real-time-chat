package com.chat.rtc.controller;

import com.chat.rtc.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
	private final List<WebSocketSession> sessions = new ArrayList<>();
	private final JwtService jwtService;

	private String getToken(WebSocketSession session){
		URI uri = session.getUri();
		String token = "";
		if (uri.getQuery() != null && uri.getQuery().startsWith("token=")){
			token = uri.getQuery().substring(6);
		}
		return token;
	}
	@Override
	public void afterConnectionEstablished(WebSocketSession webSocketSession){
		webSocketSession.getAttributes().put("username", jwtService.extractUsername(getToken(webSocketSession)));
		sessions.add(webSocketSession);
	}
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
		for (WebSocketSession s : sessions){
			if (s.isOpen()){
				String payload = message.getPayload();
				String username = (String)session.getAttributes().get("username");
				s.sendMessage(new TextMessage(username+": "+payload));
			}
		}
	}
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
		sessions.remove(session);
	}
}
