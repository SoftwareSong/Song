package com.test.player;

import java.io.File;
import java.io.FilenameFilter;

public class OnlyExt implements FilenameFilter{

	String ext;
	public OnlyExt(){}
	public OnlyExt(String str){
		ext = "." + str;
	}
	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		return name.endsWith(ext);
	}

}
