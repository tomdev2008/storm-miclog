package com.mic.log.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClientRoute1 {
	private static Map<String,String> mapPercent=new HashMap<String,String>();
	/**
	 * 1:�����������Ϊ30%��70%
	 * 2:����Ȩ��������������Ĳ�����
	 * 3��example:getRandomNum(new int[]{3,7}, new int[]{30,70});
	 * 4������������������ʿ�����0.001����֤�����û������ݱ걾ֵ�ӽ�׼ȷ��                       
	 * @param args
	 */
	public static void main(String[] args) {
		int val;
		int aVersionPercent=3;//A����ռ��Ϊ30%,������3
		int bVersionPercent=10-aVersionPercent;//B������ռ��Ϊ70%��������7
		int aVersionCount=0;//����A����������
		int bVersionCount=0;//����B����������
		int visitTotal=0;//��������
		int executeTime=5000;//����
		long currentTime=System.currentTimeMillis();
		while(true)
		{
			if(System.currentTimeMillis()-currentTime>executeTime)
			{
				break;
			}
			visitTotal++;
			val=getRandomNum(new int[]{aVersionPercent,bVersionPercent}, new int[]{50,50});
			if(val==aVersionPercent)
			{
				mapPercent.put(String.valueOf(aVersionPercent),  String.valueOf(++aVersionCount));
			}
			else
			{
				mapPercent.put(String.valueOf(bVersionPercent), String.valueOf(++bVersionCount));
			}
		}
		 System.out.println("ռ��"+(aVersionPercent*10)+"%�����ĸ���Ϊ:"+mapPercent.get(String.valueOf(aVersionPercent)));
		 System.out.println("ռ��"+(bVersionPercent*10)+"%�����ĸ���Ϊ:"+mapPercent.get(String.valueOf(bVersionPercent)));
		 System.out.println("�ܲ���������ĸ���Ϊ:"+visitTotal);
		 System.out.println("ִ��ʱ��:"+String.valueOf(executeTime)+"����");
		 
	}
	//probability �� arr һһ��Ӧ�ı�ʾ arr �и������ĸ��ʣ������� probability ��Ԫ�غͲ��ܳ��� 100��
    public static int getRandomNum(int[] arr, int[] probability){
    	//3,7  30,70
        if(arr.length != probability.length) return Integer.MIN_VALUE;
        Random ran = new Random();
        int ran_num = ran.nextInt(100);
        int temp = 0;
        for (int i = 0; i < arr.length; i++) {
            temp += probability[i];
            if(ran_num < temp)
                return arr[i];
        }
        return Integer.MIN_VALUE;
    }
}


