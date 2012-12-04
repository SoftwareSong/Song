package com.test.player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.util.*;

public class MP3Player
{
	public static final String SONG_LIST = "SongList.txt";
	public static final String SPLIT_STRING = ";";
	public static final int SORT_BY_TITLE = 0;//����������
	public static final int SORT_BY_ARTIST = 1;//����������
	public static final int SORT_BY_PLAY_COUNT = 2;//�����Ŵ�������
	public static AudioInputStream audioInputStream;//mp3������
	public static AudioFormat audioFormat;//��ʽ
	public static SourceDataLine sourceDataLine;//��Ƶ��
	
	private static MP3Player player = new MP3Player();//mp3������
	private static boolean isStopPlay = true;//�Ƿ�ֹͣ����
	private static ArrayList<Song> songList;
	/**
	 *���ļ��еõ������б�
	 */
	private void getSongs(){
		try
		{
			File file = new File(SONG_LIST);
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null)
			{
				String[] tokens = line.split(SPLIT_STRING);
				int count = Integer.parseInt(tokens[2]);
				Song nextSong = new Song(tokens[0], tokens[1], count);
				if(!songList.add(nextSong)){
					System.out.println("���ʧ�ܣ�");
				}
			}
			reader.close();
			printSongsList();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * ����һ��MP3������
	 * @return new MP3Player()
	 */
	public static MP3Player createPlayer(){
		return player;
	}
	/**
	 * ���캯������ʼ�������б�
	 */
	private MP3Player(){
		songList = new ArrayList<Song>();
		getSongs();//��ø����б�
	}
	/**
	 * ѡ�����
	 * @param id:�������
	 */
	private void select(int id){
		String filePath = "";
		try
		{
			Song song = songList.get(id);
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
	/**
	 * �����Ƶ�ļ���ʽ
	 */
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
	/**
	 * ������Ų��Ÿ���
	 * @param id
	 */
	public void play(int id){
		if(id < 0 || id >= songList.size())//����
		{
			System.out.println("�����б�Χ��");
			return;
		}
		select(id);
		try 
		{
			System.out.println("��ʼ����");
			audioFormat = audioInputStream.getFormat();//ת��MP3�ļ�����
			checkAudioFormat();
			//����������
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
			//�������ж���ԭ������
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			startPlay();//��ʼ����
			Thread playThread = new Thread(new PlayThread());
			playThread.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	/**
	 * ��ʼ����
	 */
	public void startPlay(){
		isStopPlay = false;
	}
	/**
	 * ֹͣ����
	 */
	public void stopPlay(){
		isStopPlay = true;
	}
	/**
	 * �Ƿ����ڲ���
	 * @return isStopPlay
	 */
	public boolean isStopPlay(){
		return isStopPlay;
	}
	/**
	 * ��ӡ�����б�
	 */
	public void printSongsList(){
		Iterator<Song> it = songList.iterator();
		while(it.hasNext()){
			Song s = (Song)it.next();
			System.out.println(s);
		}
	}
	/**
	 * ȡ�ø����б�
	 * @return ArrayList<Song>
	 */
	public ArrayList<Song> getSongsList(){
		return songList;
	}
	/**
	 * �������ɾ��������������������
	 * @param id
	 */
	public void delete(int id){
		try
		{
			Song song = songList.get(id);
			songList.remove(id);
			saveSongs();
			File file = new File(song.getPath());
			file.delete();
			System.out.println("�ļ���ɾ����");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	/**
	 * �������ɾ�������б��еĸ���
	 * @param id
	 */
	public void removeSong(int id){
		songList.remove(id);
		saveSongs();
	}
	/**
	 * �Ƴ��б������еĸ���
	 */
	public void removeAll(){
		songList.removeAll(songList);
		saveSongs();
	}
	/**
	 * ����path��Ӹ����������б�
	 * @param path
	 */
	public void addSong(String path){
		Song song = new Song(path, "", 0);
		int index = songList.indexOf(song);
		if(index < 0){
			songList.add(song);
			saveSongs();
		}
	}
	/**
	 * �����ļ���Ŀ¼dir��Ӹ����ļ��������б�
	 * @param dir
	 */
	public void addSongs(String dir){
		try {
			File file = new File(dir);
			OnlyExt oe = new OnlyExt("mp3");
			String s[] = file.list(oe);
			for(String str : s){
				addSong(dir + "\\" + str);
			}
			saveSongs();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��������б�
	 */
	public void saveSongs(){
		try
		{
			FileWriter writer = new FileWriter(SONG_LIST);
			System.out.println("���ڱ����ļ�");
			Iterator<Song> it = songList.iterator();
			while (it.hasNext())
			{
				Song s = (Song)it.next();
				writer.write(s.getPath()
					+ SPLIT_STRING + s.getArtist()
					+ SPLIT_STRING + s.getPlayCount() + "\n");
			}
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * ����orderBy����SORT_BY_TITLE/SORT_BY_ARTIST/SORT_BY_PLAY_COUNT
	 * @param orderBy
	 */
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