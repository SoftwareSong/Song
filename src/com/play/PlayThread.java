package com.play;

class PlayThread extends Thread
{
	public void run(){
		byte tempBuffer[] = new byte[320];
		try{
			System.out.println("���ڲ��Ÿ�����" + MP3Player.fileName);
			int cnt;
			MP3Player.isStopPlay = false;
			//��ȡ���ݵ�������
			while((cnt = MP3Player.audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1){
				if(MP3Player.isStopPlay)
					break;
				if(cnt > 0){
					//���������Ƶ����д���Ƶ��
					MP3Player.sourceDataLine.write(tempBuffer, 0, cnt);
				}
			}
			//Block�ȴ���ʱ���ݱ����Ϊ��
			MP3Player.sourceDataLine.drain();
			MP3Player.sourceDataLine.close();
			MP3Player.isStopPlay = true;
		} catch (Exception e) {
			System.out.println("���Ź����г�����");
			e.printStackTrace();
			System.exit(0);
		}
	}
}