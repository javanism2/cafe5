package com.mulcam.ai.web.controller;

import java.io.IOException;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.mulcam.ai.util.SessionList;
import com.mulcam.ai.util.SessionList2;

@Component
@ServerEndpoint("/WebSocket6")
public class WebSocket6 {
	
	java.util.List<Session>sessions = new CopyOnWriteArrayList<>();

    @OnMessage
    public void handleTextMessage(Session session, String message)
      throws InterruptedException, IOException {
    	//System.out.println(message);
    	System.out.println(sessions.size());
    	int i=1;
    	
        for (Session webSocketSession : sessions) {
        	
            if (webSocketSession.isOpen() && !session.getId().equals(webSocketSession.getId())) {
                webSocketSession.getBasicRemote().sendText(message);
                System.out.println(i++ +"번 전송");
            }
        }
    }

    @OnOpen
    public void afterConnectionEstablished(Session session) throws Exception {
    	System.out.println(session);
        sessions.add(session);
    }
	

}





