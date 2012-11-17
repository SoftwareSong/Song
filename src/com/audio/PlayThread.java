package com.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.*;

import javax.sound.sampled.AudioInputStream;

public class PlayThread extends Thread  {
      
	 private Socket s;
	 
	
	
	 PlayThread(Socket s){
		 this.s=s;
	 }
	 
	 public void run(){
		 
		 BufferedInputStream playbackInputStream;
		 audio.stopPlay=false;
		 
		 try { 
	           playbackInputStream=new BufferedInputStream(new AudioInputStream(s.getInputStream(),audio.audioFormat,2147483647));//��װ����Ƶ�����������������Ǿ���ѹ�������ڴ˼��׽�ѹ�� 
	         } 
	         catch (IOException ex) { 
	             return; 
	         }
		 
		 byte[] data = new byte[100000];//�˴�����Ĵ�С��ʵʱ�Թ�ϵ���󣬿ɸ���������е��� 
         int numBytesRead = 0; 
         
         while (!audio.stopPlay) { 
             try{ 
                numBytesRead = playbackInputStream.read(data); 
                audio.sourceDataLine.write(data, 0,numBytesRead); 
                
               // System.out.println("read data from socket");
             } catch (IOException e) { 
                  break; 
              } 
          }
         audio.sourceDataLine.stop();
         audio.sourceDataLine.close();
         
	 }
	
}
