package com.play;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.sound.sampled.AudioFileFormat;
import java.util.*;

public class MP3Player
{
	public static final String SONG_LIST = "SongList.txt";
	public static final String SPLIT_STRING = ";";
	public static final int SORT_BY_TITLE = 0;//����������
	public static final int SORT_BY_ARTIST = 1;//����������
	public static final int SORT_BY_PLAY_COUNT = 2;//�����Ŵ�������
	static String fileName = "δ����";//�ļ���
	static AudioInputStream audioInputStream;//mp3������
	static AudioFormat audioFormat;//��ʽ
	static SourceDataLine sourceDataLine;//��Ƶ��
	static TargetDataLine targetDataLine;//Ŀ��������
	static boolean isStopPlay = true;//�Ƿ�ֹͣ����
	static boolean isStopCapture = false;//�Ƿ�ֹͣ¼��
	static String filePath = "";
	public static ArrayList<Song> songList = new ArrayList<Song>();
	//���ļ��еõ������б�
	public void getSongs(){
		try
		{
			File file = new File(SONG_LIST);
			if(file.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null)
				{
					String[] tokens = line.split(SPLIT_STRING);
					int count = Integer.parseInt(tokens[2]);
					Song nextSong = new Song(tokens[0], tokens[1], count);
					songList.add(nextSong);
				}
			} else {
				System.out.println("�ļ��б����ڣ�");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public MP3Player(){
		new HandlerShutDownEvent();
		getSongs();
	}
	public MP3Player(String path){
		this();
		try
		{
			File file = new File(path);
			Song song = new Song(path, " ", 0);
			int index = songList.indexOf(song);
			if (index < 0)//ʹ�������ظ�
			{
				songList.add(song);
			}
			select(song);
			play();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	//ѡ�����
	public void select(Song song){
		try
		{
			fileName = song.getTitle();
			filePath = song.getPath();
			File file = new File(filePath);
			//ȡ���ļ�������
			audioInputStream = AudioSystem.getAudioInputStream(file);
			System.out.println("ȡ���ļ�������");
			System.out.println("�ļ���С��" + song.getLength());
			song.addPlayCount();
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("�Ҳ���ָ���ļ���" + filePath);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//�����Ƶ�ļ���ʽ
	private void checkAudioFormat(){
		//���벻����Ĭ��Ҫ��
		if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED){
			//�����µ��ļ���ʽ
			audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				audioFormat.getSampleRate(), 16,
				audioFormat.getChannels(),
				audioFormat.getChannels() * 2,
				audioFormat.getSampleRate(), false);
			//�����¸�ʽ����Ƶ�����������µ���Ƶ������
			audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
		}
	}
	//ȡ��AudioFormat
	private AudioFormat getAudioFormat() {
		float sampleRate = 16000.0F;
		//8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		//8,16
		int channels = 1;
		//1,2
		boolean signed = true;
		//true,false
		boolean bigEndian = false;
		//true,false
		return new AudioFormat(sampleRate, 
			sampleSizeInBits, channels, 
			signed,
			bigEndian);
	}
	//����
	public void play(){
		try 
		{
			System.out.println("��ʼ����");
			audioFormat = audioInputStream.getFormat();
			checkAudioFormat();
			//����������
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, audioFormat);
			//�������ж���ԭ������
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			isStopPlay = false;
			Thread playThread = new Thread(new PlayThread());
			playThread.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//¼��
	public void capture(){
		try
		{
			System.out.println("��ʼ¼��");
			audioFormat = getAudioFormat();
			//����������
			DataLine.Info dataLineInfo = new DataLine.Info(
					TargetDataLine.class, audioFormat);
			//�������ж���Ŀ��������
			targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
			isStopCapture = false;
			Thread captureThread = new Thread(new CaptureThread());
			captureThread.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	//����
	public void saveCaptureFile(String path){
		try
		{
			filePath = path;
			File file = new File(filePath + ".wav");		
			AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
			FileWriter writer = new FileWriter(SONG_LIST, true);//true��ʾд���ļ�ĩβ
			writer.write(filePath + SPLIT_STRING + " " + SPLIT_STRING +"0\n");
			writer.close();
			System.out.println(fileName + "�ѱ��棡");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	//ֹͣ����
	public void stopPlay(){
		isStopPlay = true;
	}
	//ֹͣ¼��
	public void stopCapture(){
		isStopCapture = true;
	}
	//ɾ������
	public void delete(Song song){
		try
		{
			File file = new File(song.getPath());
			file.delete();
			System.out.println("�ļ���ɾ����");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	public void sort(int orderBy){
		Comparator<Song> compare = null;
		switch (orderBy)
		{
		case SORT_BY_TITLE:
			compare = new TitleCompare();
			break;
		case SORT_BY_ARTIST:
			compare = new ArtistCompare();
			break;
		case SORT_BY_PLAY_COUNT:
			compare = new PlayCountCompare();
			break;
		default:
			break;
		}
		Collections.sort(songList, compare);
	}
}