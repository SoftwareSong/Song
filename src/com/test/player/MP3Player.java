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
	public static final int SORT_BY_TITLE = 0;//按标题排序
	public static final int SORT_BY_ARTIST = 1;//按作家排序
	public static final int SORT_BY_PLAY_COUNT = 2;//按播放次数排序
	public static AudioInputStream audioInputStream;//mp3数据流
	public static AudioFormat audioFormat;//格式
	public static SourceDataLine sourceDataLine;//混频器
	
	private static MP3Player player = new MP3Player();//mp3播放器
	private static boolean isStopPlay = true;//是否停止播放
	private static ArrayList<Song> songList;
	/**
	 *从文件中得到歌曲列表
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
					System.out.println("添加失败！");
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
	 * 创建一个MP3播放器
	 * @return new MP3Player()
	 */
	public static MP3Player createPlayer(){
		return player;
	}
	/**
	 * 构造函数，初始化歌曲列表
	 */
	private MP3Player(){
		songList = new ArrayList<Song>();
		getSongs();//获得歌曲列表
	}
	/**
	 * 选择歌曲
	 * @param id:歌曲序号
	 */
	private void select(int id){
		String filePath = "";
		try
		{
			Song song = songList.get(id);
			filePath = song.getPath();
			File file = new File(filePath);
			//取得文件输入流
			audioInputStream = AudioSystem.getAudioInputStream(file);
			System.out.println("取得文件输入流");
			System.out.println("文件大小：" + song.getLength());
			song.addPlayCount();
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("找不到指定文件：" + filePath);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	/**
	 * 检查音频文件格式
	 */
	private void checkAudioFormat(){
		//编码不符合默认要求
		if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED){
			//定义新的文件格式
			audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				audioFormat.getSampleRate(), 16,
				audioFormat.getChannels(),
				audioFormat.getChannels() * 2,
				audioFormat.getSampleRate(), false);
			//利用新格式和音频输入流构造新的音频输入流
			audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
		}
	}
	/**
	 * 根据序号播放歌曲
	 * @param id
	 */
	public void play(int id){
		if(id < 0 || id >= songList.size())//出错
		{
			System.out.println("超出列表范围！");
			return;
		}
		select(id);
		try 
		{
			System.out.println("开始播放");
			audioFormat = audioInputStream.getFormat();//转换MP3文件编码
			checkAudioFormat();
			//构造数据行
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
			//将数据行读入原数据行
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			startPlay();//开始播放
			Thread playThread = new Thread(new PlayThread());
			playThread.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	/**
	 * 开始播放
	 */
	public void startPlay(){
		isStopPlay = false;
	}
	/**
	 * 停止播放
	 */
	public void stopPlay(){
		isStopPlay = true;
	}
	/**
	 * 是否正在播放
	 * @return isStopPlay
	 */
	public boolean isStopPlay(){
		return isStopPlay;
	}
	/**
	 * 打印歌曲列表
	 */
	public void printSongsList(){
		Iterator<Song> it = songList.iterator();
		while(it.hasNext()){
			Song s = (Song)it.next();
			System.out.println(s);
		}
	}
	/**
	 * 取得歌曲列表
	 * @return ArrayList<Song>
	 */
	public ArrayList<Song> getSongsList(){
		return songList;
	}
	/**
	 * 根据序号删除歌曲，包含歌曲本身
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
			System.out.println("文件已删除！");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	/**
	 * 根据序号删除歌曲列表中的歌曲
	 * @param id
	 */
	public void removeSong(int id){
		songList.remove(id);
		saveSongs();
	}
	/**
	 * 移除列表中所有的歌曲
	 */
	public void removeAll(){
		songList.removeAll(songList);
		saveSongs();
	}
	/**
	 * 根据path添加歌曲到歌曲列表
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
	 * 根据文件夹目录dir添加歌曲文件到歌曲列表
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
	 * 保存歌曲列表
	 */
	public void saveSongs(){
		try
		{
			FileWriter writer = new FileWriter(SONG_LIST);
			System.out.println("正在保存文件");
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
	 * 根据orderBy排序，SORT_BY_TITLE/SORT_BY_ARTIST/SORT_BY_PLAY_COUNT
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