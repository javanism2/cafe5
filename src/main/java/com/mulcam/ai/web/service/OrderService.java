package com.mulcam.ai.web.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mulcam.ai.web.dao.OrderDAO;
import com.mulcam.ai.web.dao.OrderDAOImpl;
import com.mulcam.ai.web.vo.OrderVO;

@Service
public class OrderService {
	
	@Autowired
	OrderDAOImpl orderDAO;
	ServerSocket ss;
	ArrayList<ObjectOutputStream> kitchenList;
	
	public OrderService() {
		try {
			ss=new ServerSocket(9999);
			kitchenList=new ArrayList<ObjectOutputStream>();			
			new Thread(()-> {
					while(true) {
						try {
							Socket s=ss.accept();
							ObjectOutputStream out=new ObjectOutputStream(s.getOutputStream());
							ObjectInputStream in=new ObjectInputStream(s.getInputStream());
							kitchenList.add(out);
							new KitchenThread(s,in,out).start();
						}catch(IOException e) {
							e.printStackTrace();
						}
					}
				}
			).start();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//출고
	public void update(long order_group_no) {
		orderDAO.update(order_group_no);
		pushOrders();
	}
	
	
	//주문입력
	public long insert(ArrayList<OrderVO> list) {
		long order_group_no=orderDAO.insert(list);
		//주방으로 주문 통보
		pushOrders();
		return order_group_no;
	}
	
	//모든 미출고 주문 조회
	public List<OrderVO> ordersSelect(){
		List<OrderVO> list=orderDAO.ordersSelect();		
		return list;
	}

	//주방 스크린으로 주문 상황 전송
	public void pushOrders() {
		System.out.println("pushOrders");
		List<OrderVO> all_list=ordersSelect();	
		for(ObjectOutputStream out:kitchenList) {
			try {
				out.writeObject(all_list);
			} catch (IOException e) {				
				System.out.println(4+":"+e.getMessage());
			}
		}
	}

	//주방스크린으로 부터 오는 모든 요청 읽기
	private class KitchenThread extends Thread{
		Socket s; 
		ObjectInputStream in;
		ObjectOutputStream out;
		
		public KitchenThread(Socket s, ObjectInputStream in, ObjectOutputStream out) {
			this.s=s;
			this.in=in;
			this.out=out;
		}

		@Override
		public void run() {
			try {
				while(true) {				
					String req=(String)in.readObject();	
					if(req.equals("ordersSelect")) {
						pushOrders();
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}



	 

}//end OrderService











