package com.test.player;

import java.io.*;
public class Song
{
	private String title;
	private String path;
	private String artist;
	private int playCount;
	private double length = 0f;
	/**
	 * 构造函数
	 * @param p
	 * @param a
	 * @param c
	 */
	public Song(String p, String a, int c){
		path = p;
		artist = a;
		playCount = c;
		try
		{
			File file = new File(path);
			if(!file.exists()) {//文件不存在
				System.out.println("文件不存在");
			} else {
				length = 1.0*file.length()/1024;
				title = file.getName();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 判断两个歌曲是否相等
	 */
	public boolean equals(Object aSong){
		Song s = (Song)aSong;
		return path.equals(s.path);
	}
	public int hashCode(){
		return path.hashCode();
	}
	/**
	 * 取得歌曲名
	 * @return title
	 */
	public String getTitle(){
		return title;
	}
	/**
	 * 设置歌曲名字
	 * @param title
	 */
	public void setTitle(String title){
		this.title = title;
	}
	/**
	 * 取得歌曲路径
	 * @return path
	 */
	public String getPath(){
		return path;
	}
	/**
	 * 取得歌曲作者
	 * @return artist
	 */
	public String getArtist(){
		return artist;
	}
	/**
	 * 设置歌曲作者
	 * @param artist
	 */
	public void setArtist(String artist){
		this.artist = artist;
	}
	/**
	 * 取得播放次数
	 * @return playCount
	 */
	public int getPlayCount(){
		return playCount;
	}
	/**
	 * 取得歌曲大小
	 * @return String
	 */
	public String getLength(){
		if(length/1024 > 0)
			return String.format("%.2fM", length/1024);
		return String.format("%.2fK", length);
	}
	public String toString(){
		return title;
	}
	/**
	 * 播放次数加1
	 */
	public void addPlayCount(){
		++playCount;
	}
}