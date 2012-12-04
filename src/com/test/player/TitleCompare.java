package com.test.player;

import java.util.Comparator;
class TitleCompare implements Comparator<Song>
{
	public int compare(Song s1, Song s2){
		return s1.getTitle().compareTo(s2.getTitle());
	}
}