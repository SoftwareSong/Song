package com.test.player;

class PlayThread extends Thread
{
	public void run(){
		byte tempBuffer[] = new byte[320];
		MP3Player mp3 = MP3Player.createPlayer();
		try{
			System.out.println("���ڲ��Ÿ�����");
			int cnt;
			//��ȡ���ݵ�������
			while((cnt = MP3Player.audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1){
				while(mp3.isStopPlay()){
					System.out.println("ֹͣ����");
				}
				if(cnt > 0){
					//���������Ƶ����д���Ƶ��
					MP3Player.sourceDataLine.write(tempBuffer, 0, cnt);
				}
			}
			//Block�ȴ���ʱ���ݱ����Ϊ��
			MP3Player.sourceDataLine.drain();
			MP3Player.sourceDataLine.close();
			mp3.startPlay();
		} catch (Exception e) {
			System.out.println("���Ź����г�����");
			e.printStackTrace();
			System.exit(0);
		}
	}
}