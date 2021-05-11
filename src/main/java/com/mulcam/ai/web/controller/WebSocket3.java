package com.mulcam.ai.web.controller;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.mulcam.ai.util.SessionList;

@Component
@ServerEndpoint("/WebSocket3")
public class WebSocket3 {
	
	@OnOpen
	public void open() {
		System.out.println("화상 접속 ok");
	}
	
	@OnMessage
	public void receiveBinary(ByteBuffer bb, boolean last,Session session) {
		try {			
			session.getBasicRemote().sendBinary(bb,last);
		} catch (IOException e) {
			try {
				session.close();
			} catch (IOException e1) {
				
			}
		}
	}

}





