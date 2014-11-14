package com.mic.log.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClientRoute {
	private static Map<String,String> mapPercent=new HashMap<String,String>();
	/**
	 * 1:�����������Ϊ50%��50%
	 * 2:����Ȩ��������������Ĳ�����
	 * 3��example:getRandomNum(new String[]{"A","B"}, new int[]{50,50});
	 * 4������������������ʿ�����0.001����֤�����û������ݱ걾ֵ�ӽ�׼ȷ��                       
	 * @param args
	 */
	public static void main(String[] args) {
		String val;
		String aVersionDesc="A";//��������
		String bVersionDesc="B";//��������
		int aVersionCount=0;//����A����������
		int bVersionCount=0;//����B����������
		int aWeight=50;//Ȩ������
		int bWeight=50;//Ȩ������
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
			val=getRandomNum(new String[]{aVersionDesc,bVersionDesc}, new int[]{aWeight,bWeight});
			if(val=="A")
			{
				mapPercent.put(String.valueOf(aVersionDesc),  String.valueOf(++aVersionCount));
			}
			else
			{
				mapPercent.put(String.valueOf(bVersionDesc), String.valueOf(++bVersionCount));
			}
		 //System.out.println("�����������Ϊ:"+val);
		}
		 System.out.println(aVersionDesc+"Ȩ��Ϊ��"+(aWeight)+"%���ܼƲ����ĸ���Ϊ:"+mapPercent.get(String.valueOf(aVersionDesc)));
		 System.out.println(bVersionDesc+"Ȩ��Ϊ��"+(bWeight)+"%���ܼƲ����ĸ���Ϊ:"+mapPercent.get(String.valueOf(bVersionDesc)));
		 System.out.println("�ܲ���������ĸ���Ϊ:"+visitTotal);
		 System.out.println("ִ��ʱ��:"+String.valueOf(executeTime)+"����");
		 
	}
    public static String getRandomNum(String[] arr, int[] probability){
        if(arr.length != probability.length) return arr[0];
        Random ran = new Random();
        int ran_num = ran.nextInt(100);
        int temp = 0;
        for (int i = 0; i < arr.length; i++) {
            temp += probability[i];
            if(ran_num < temp)
                return arr[i];
        }
        return arr[1];
    }
}
