package com.mulcam.ai.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

// WebSocket의 호스트 주소 설정
@Component 
@ServerEndpoint("/websocket")
public class WebSocket {
// WebSocket으로 브라우저가 접속하면 요청되는 함수
	ArrayList<Session> sessions=new ArrayList<Session>();
	@OnOpen
	public void handleOpen(Session session) {
// 콘솔에 접속 로그를 출력한다.
		sessions.add(session);
		System.out.println("client is now connected...");
	}

//// WebSocket으로 메시지가 오면 요청되는 함수
//	@OnMessage
//	public String handleMessage(String message, Session userSession) {
//// 메시지 내용을 콘솔에 출력한다.
//		System.out.println("receive from client : " + message+" : "+userSession);
//// 에코 메시지를 작성한다.
//		String replymessage = "echo " + message;
//// 에코 메시지를 콘솔에 출력한다.
//		System.out.println("send to client : " + replymessage);
//// 에코 메시지를 브라우저에 보낸다.
//		return replymessage;
//	}
	
	// 바이너리 데이터를 수신할 때 호출됨

    @OnMessage

    public void echoBinaryMessage(ByteBuffer bb, boolean last, Session session) {

        try {
        	//System.out.println(bb);

            for (Session se:sessions) {

                se.getBasicRemote().sendBinary(bb, last);

            	//session.getBasicRemote().sendText("파일이 서버에 도착했어요~", last);

               // File file = new File("D:/temp/sample");



            	// Set to true if the bytes should be appended to the file;

            	// set to false if the bytes should replace current bytes

            	// (if the file exists)

            	//boolean append = false;



            	//try {

            	    // Create a writable file channel

            	  //  FileChannel wChannel = new FileOutputStream(file, append).getChannel();



            	    // Write the ByteBuffer contents; the bytes between the ByteBuffer's

            	    // position and the limit is written to the file


            	  //  wChannel.write(bb);



            	    // Close the file

            	 //   wChannel.close();

            	//} catch (IOException e) {
            	//	e.printStackTrace();
            	//}

            }

        } catch (IOException e) {

            try {

                session.close();

            } catch (IOException e1) {

                // Ignore

            }

        }

    }



// WebSocket과 브라우저가 접속이 끊기면 요청되는 함수
	@OnClose
	public void handleClose() {
// 콘솔에 접속 끊김 로그를 출력한다.
		System.out.println("client is now disconnected...");
	}

// WebSocket과 브라우저 간에 통신 에러가 발생하면 요청되는 함수.
	@OnError
	public void handleError(Throwable t) {
// 콘솔에 에러를 표시한다.
		t.printStackTrace();
	}
}
