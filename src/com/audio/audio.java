package com.audio;

import java.io.*; 

import javax.sound.sampled.*; 

import java.net.*;

/**
 *    sound ��ʵ������ ¼��  ����     
 * 
 *¼�� Record();
*����Play();
*����Save(string path);
*����������SendStream(socket s);
*����������ReceiveStream(socket s);
*��ͣStop();
 * 
 * 
 */
public class audio {
	//�����׽��� ������������
	ServerSocket serSocket;
    Socket clientSocket;
 
	static boolean stopCapture = false; // ����¼����־
	public static AudioFormat audioFormat; // ¼����ʽ
	
	// ��ȡ���ݣ���TargetDataLineд��ByteArrayOutputStream¼��
	public static ByteArrayOutputStream byteArrayOutputStream;
	public static int totaldatasize = 0;
	static TargetDataLine targetDataLine;
	
	// �������ݣ���AudioInputStreamд��SourceDataLine����
	static AudioInputStream audioInputStream;
	static SourceDataLine sourceDataLine;
	
	
	// ȡ��AudioFormat ��Ƶ��ʽ
	private AudioFormat getAudioFormat() {
		float sampleRate = 16000.0F;
		// 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		// 8,16
		int channels = 1;
		// 1,2
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

/**
 * ����������SendStream(socket s)
 * @param s Ŀ��ͻ��˵��׽���
 */
	public void SendStream(Socket s){
		try{
		        // ��¼��
					audioFormat = getAudioFormat();
					DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
					targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
					targetDataLine.open(audioFormat);
					targetDataLine.start();
		}
		catch (Exception e) {
			    e.printStackTrace();
		    	System.exit(0);
		}
		
	}
	
    public void ReceiveStream(){
    	
    }
	
}
