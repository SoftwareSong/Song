package com.test.player;

class PlayThread extends Thread
{
	public void run(){
		byte tempBuffer[] = new byte[320];
		MP3Player mp3 = MP3Player.createPlayer();
		try{
			System.out.println("正在播放歌曲：");
			int cnt;
			//读取数据到缓存区
			while((cnt = MP3Player.audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1){
				while(mp3.isStopPlay()){
					System.out.println("停止播放");
				}
				if(cnt > 0){
					//将缓存的音频数据写入混频器
					MP3Player.sourceDataLine.write(tempBuffer, 0, cnt);
				}
			}
			//Block等待临时数据被输出为空
			MP3Player.sourceDataLine.drain();
			MP3Player.sourceDataLine.close();
			mp3.startPlay();
		} catch (Exception e) {
			System.out.println("播放过程中出错了");
			e.printStackTrace();
			System.exit(0);
		}
	}
}