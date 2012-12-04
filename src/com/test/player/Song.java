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
	 * ���캯��
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
			if(!file.exists()) {//�ļ�������
				System.out.println("�ļ�������");
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
	 * �ж����������Ƿ����
	 */
	public boolean equals(Object aSong){
		Song s = (Song)aSong;
		return path.equals(s.path);
	}
	public int hashCode(){
		return path.hashCode();
	}
	/**
	 * ȡ�ø�����
	 * @return title
	 */
	public String getTitle(){
		return title;
	}
	/**
	 * ���ø�������
	 * @param title
	 */
	public void setTitle(String title){
		this.title = title;
	}
	/**
	 * ȡ�ø���·��
	 * @return path
	 */
	public String getPath(){
		return path;
	}
	/**
	 * ȡ�ø�������
	 * @return artist
	 */
	public String getArtist(){
		return artist;
	}
	/**
	 * ���ø�������
	 * @param artist
	 */
	public void setArtist(String artist){
		this.artist = artist;
	}
	/**
	 * ȡ�ò��Ŵ���
	 * @return playCount
	 */
	public int getPlayCount(){
		return playCount;
	}
	/**
	 * ȡ�ø�����С
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
	 * ���Ŵ�����1
	 */
	public void addPlayCount(){
		++playCount;
	}
}