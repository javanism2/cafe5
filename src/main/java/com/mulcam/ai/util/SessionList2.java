package com.mulcam.ai.util;

import java.util.HashMap;

import javax.websocket.Session;

public class SessionList2 {
	
	
	public HashMap<String,Session> map=new HashMap<String,Session>();
	
	static private SessionList2 instance;
	
	private SessionList2() {}
	
	public static SessionList2 getInstance() {
		if(instance==null) {
			instance=new SessionList2();
		}
		return instance;
	}
	
	

}
