package com.audio;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;


  class RecordThread extends Thread  {
	
	// ��ʱ����
		byte tempBuffer[] = new byte[1000];
		Socket client;
		BufferedOutputStream captrueOutputStream;
		
		RecordThread(Socket s){
			this.client=s;
		}
		
		public void run() {
			audio.byteArrayOutputStream = new ByteArrayOutputStream();
			audio.totaldatasize = 0;
			audio.stopCapture = false;
			
			 try { 
		           captrueOutputStream=new BufferedOutputStream(client.getOutputStream());//��������� �˴����Լ���ѹ��������ѹ������ 
		         } 
		         catch (IOException ex) { 
		             return; 
		         }
			
			try {// ѭ��ִ�У�ֱ������ֹͣ¼����ť
				while (!audio.stopCapture) {
					// ��ȡ10000������
					int cnt = audio.targetDataLine.read(tempBuffer, 0,
							tempBuffer.length);
					//������д����������
					try { 
			               captrueOutputStream.write(tempBuffer, 0, cnt);//д�������� 
			               
			              // System.out.println("write data in socket");
			             //������д�뻺������
							if (cnt > 0) {
								// ���������
								audio.byteArrayOutputStream.write(tempBuffer, 0, cnt);
								audio.totaldatasize += cnt;
								
								//System.out.println("write data in buffer");
							}
			             } 
			             catch (Exception ex) { 
			                 break; 
			             } 
					
					
				}
				audio.byteArrayOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
			audio.targetDataLine.stop();
			audio.targetDataLine.close();
		}

}
