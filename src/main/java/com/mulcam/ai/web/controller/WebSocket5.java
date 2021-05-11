package com.mulcam.ai.web.controller;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.StringTokenizer;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.mulcam.ai.util.SessionList;
import com.mulcam.ai.util.SessionList2;

@Component
@ServerEndpoint("/WebSocket5")
public class WebSocket5 {
	
	HashMap<String,Session> map;
	boolean isFirst=true;
	String id;
	
	@OnOpen
	public void open(Session session) {
		map=SessionList2.getInstance().map;		
		System.out.println("WebSocket5 접속 ok");
	}
	
	@OnMessage
	public void receiveMsg(String message,Session session) {				
		try {
			
			JSONParser parser=new JSONParser(message);
			LinkedHashMap<String, Object> call_map=parser.parseObject();
			//String caller=(String) call_map.get("caller");
			//String callee=(String) call_map.get("callee");			
			String msg=(String)call_map.get("msg");
			String to=(String)call_map.get("to");
			System.out.println(to+":"+map.get(to));
			
				Session to_session=map.get(to);
				to_session.getBasicRemote().sendText(msg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

}





