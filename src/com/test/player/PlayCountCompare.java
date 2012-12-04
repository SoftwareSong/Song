package com.test.player;

import java.util.Comparator;
class PlayCountCompare implements Comparator<Song>
{
	public int compare(Song s1, Song s2){
		return s1.getPlayCount() - s2.getPlayCount();
	}
}