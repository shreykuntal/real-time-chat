package com.chat.rtc.controller;

import com.chat.rtc.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
	static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private final JwtService jwtService;

	private String getToken(WebSocketSession session){
		URI uri = session.getUri();
		String token = "";
		if (uri.getQuery() != null && uri.getQuery().startsWith("token=")){
			token = uri.getQuery().substring(6);
		}
		return token;
	}

	private String parseReceipent(String message){
		if (!message.contains("?")) return null;
		return message.substring(0, message.indexOf('?'));
	}
	@Override
	public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception{
		String username = jwtService.extractUsername(getToken(webSocketSession));
		for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()){
			if (entry.getValue().isOpen()){
				entry.getValue().sendMessage(new TextMessage("ADD"+username));
			}
			if (webSocketSession.isOpen()){
				webSocketSession.sendMessage(new TextMessage("ADD"+entry.getKey()));
			}
		}
		webSocketSession.getAttributes().put("username", username);
		sessions.put(username, webSocketSession);
	}
	@Override
	protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) throws Exception{

		String receipent = parseReceipent(message.getPayload());
		String username = (String)webSocketSession.getAttributes().get("username");
		if (receipent == null || (!receipent.equals("GC") && !sessions.containsKey(receipent))) return;
		String forward = username+": "+message.getPayload().substring(message.getPayload().indexOf('?')+1);
		if (!receipent.equals("GC")){
			sessions.get(receipent).sendMessage(new TextMessage("DSP"+forward));
		}else{
			for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()){
				if (!entry.getKey().equals(username)) {
					entry.getValue().sendMessage(new TextMessage("GCH" + forward));
				}
			}
		}
	}
	@Override
	public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception{
		String username = (String)webSocketSession.getAttributes().get("username");
		sessions.remove(username);
		for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()){
			if (entry.getValue().isOpen()){
				entry.getValue().sendMessage(new TextMessage("DEL"+username));
			}
		}
	}
}
