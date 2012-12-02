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
	static boolean stopPlay=false;         //���ſ��Ʊ�־
	
	public static AudioFormat audioFormat; // ¼����ʽ
	
	// ��ȡ���ݣ���TargetDataLineд��ByteArrayOutputStream¼��
	public static ByteArrayOutputStream byteArrayOutputStream;
	public static int totaldatasize = 0;
	static TargetDataLine targetDataLine;
	
	// �������ݣ���AudioInputStreamд��SourceDataLine����
	static AudioInputStream audioInputStream;
	static SourceDataLine sourceDataLine;
	
	Thread Record;
	PlayThread Play;
	
	
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
			
					
					 Record=new Thread(new RecordThread(s));
					//RecordThread Record=new RecordThread(s;)
					Record.start();
		}
		catch (Exception e) {
			    e.printStackTrace();
		    	System.exit(0);
		}
		
	}
	
/**
 * ���������л�ȡ���ݲ�����
 * @param s ��Է��������׽���
 */
    public void ReceiveStream(Socket s){
    	
    	try{
    		audioFormat = getAudioFormat();
    		
    		//��ò�������Դ
    		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			
		   Play=new PlayThread(s);
		   Play.start();
			
			
    		
    	}catch(Exception e)
    	{
    		  e.printStackTrace();
		    	System.exit(0);
    	}
    	
    }

   public void StopRecord()
   {
	   stopCapture=true;
   }
   public void StopPlay(){
	   stopPlay=true;
   }
    
/**
 * ��ͣ¼��
 */
   public void PauseRecord(){
	   
	   try{
		     if(Record.isAlive()){
		    	 Record.wait();
		     }
		    	 
	   }catch(Exception e){
		   System.out.println("error in PauseRecord"); 
	   }
   }
    
    
 /**
  * ��ͣ����  
  */
   public void PausePlay(){
	   try{
		     if(Play.isAlive()){
		    	 Play.wait();
		     }
		    	 
	   }catch(Exception e){
		   System.out.println("error in PausePlay"); 
	   }  
   }
   
   /**
    * �ٴβ���
    */
   public void AgainPlay(){
	   try{
		    
		    Play.notify();	 
	   }catch(Exception e){
		   System.out.println("error in AgainPlay"); 
	   }  
   }
   
   /**
    * �ٴ�¼��
    */
   public void AgainRecord()
   {
	   try{
		    
		    Record.notify();	 
	   }catch(Exception e){
		   System.out.println("error in AgainRecord"); 
	   }  
	   
   }
   
  /**
   *  �����ļ�
   * @param path �ļ�·��
   */
   public void Save(String path){
	// ȡ��¼��������
			AudioFormat audioFormat = getAudioFormat();
			byte audioData[] = byteArrayOutputStream.toByteArray();
			InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
			audioInputStream = new AudioInputStream(byteArrayInputStream,audioFormat, audioData.length / audioFormat.getFrameSize());
	   
			try {
				File file = new File(path);
				AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
			} catch (Exception e) {
				e.printStackTrace();
			}
   }
   
 /**
  *   
  */
   public void PlayBuff(){
	 
   }
   
}
