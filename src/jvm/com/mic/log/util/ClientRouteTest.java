package com.mic.log.util;

public class ClientRouteTest {
	public static void main(String[] args) {
		//����û��ǵ�һ�η��ʣ���ִ���������������Ĵ���;������ǵ�һ�η��ʲ���Ҫ���������
		String randomAB=ClientRoute.getRandomNum(new String[]{"A","B"}, new int[]{50,50});
		//��һ�����жϿͻ����Ƿ����AB���Է�����ʶΪinquery��ֵ
		if(randomAB=="A")
		{
			System.out.println("����A����;");//de=inquery,0;������������;0����A����
		}
		else
		{
			System.out.println("����B����;");//de=inquery,1;������������;1����B����
		}
	}

}
