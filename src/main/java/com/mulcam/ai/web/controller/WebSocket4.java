package com.mulcam.ai.web.controller;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.mulcam.ai.util.SessionList;
import com.mulcam.ai.util.SessionList2;

@Component
@ServerEndpoint("/WebSocket4")
public class WebSocket4 {
	
	HashMap<String,Session> map;
	boolean isFirst=true;
	String chat_id;
	
	@OnOpen
	public void open(Session session) {
		map=SessionList2.getInstance().map;
		
		System.out.println("WebSocket4 접속 ok");
	}
	
	@OnMessage
	public void receiveMsg(String msg,Session session) {
		if(isFirst) {
			isFirst=false;
			chat_id=msg;
			//System.out.println(id);
			map.put(chat_id, session);			
			
			Set<String> ids=map.keySet();
			Iterator<String> it=ids.iterator();
			String map_ids="<select  id=\"calleeId\">\r\n" + 
					"<option value=\"\">--호출 할 상대방 이름을 선택하세요--</option>";
			
		
			while(it.hasNext()) {
				String id=it.next();
				map_ids += "<option value='"+id+"'>"+id+"</option>\n";
			}
			map_ids+="</select>";
			//broadcast
			for(Session s:map.values()) {
				try {
					s.getBasicRemote().sendText(map_ids);
				} catch (IOException ex) {
					
				}
			}	
			
			msg=chat_id+"님이 입장하셨습니다";
		}
		System.out.println("받은 메세지:"+msg +" : "+map.size()+"명에게 전송합니다");
		
		//broadcast
		for(Session s:map.values()) {
			try {
				s.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	@OnClose
	public void close(Session session) {
		System.out.println(session.getId()+" 접속 end");
		synchronized (map) {			
			map.remove(chat_id);
		}		
	}

}





