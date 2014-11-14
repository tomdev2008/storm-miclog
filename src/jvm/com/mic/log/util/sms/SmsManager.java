package com.mic.log.util.sms;

import com.mic.log.util.PropertiesUtils;



public class SmsManager {
	public static void main(String[] args)
	{
		String wsAddress=PropertiesUtils.getValue("smsUrl");
		try
		{
			WebServiceClient wsc=new WebServiceClient();
			wsc.init(wsAddress, PropertiesUtils.getValue("smsNamespaceURI"));
			Integer iRet=new Integer(0);
			String[] paraArray=new String[10];
			paraArray[0]="mictest";
			paraArray[1]="focustech1+11";
			paraArray[2]="18913984951";
			paraArray[3]="sms/mt";
			paraArray[4]="��Ŀ����";
			paraArray[5]="��ĿEN-����Error";
			paraArray[6]=null;
			paraArray[7]="414";
			paraArray[8]="";
			paraArray[9]="";
			iRet=(Integer)wsc.invokeForObject("sendMessageX", paraArray, new Class[]{Integer.class});
			System.out.println(getSmsStatusMsg(iRet));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public static String getSmsStatusMsg(int iRet)
	{
		if(iRet==-1)
		{
			return "�û�����������";
		}
		else if(iRet==-2)
		{
			return "IP��֤����";
			
		}
		else if(iRet==-3)
		{
			return "��ʱ���ڴ���";
			
		}
		else if(iRet==-101)
		{
			return "��������userIdΪ��";
		}
		else if(iRet==-102)
		{
			return "��������Ŀ�����Ϊ��";
		}
		else if(iRet==-103)
		{
			return "������������Ϊ��";
		}
		else if(iRet==200)
		{
			return "Ŀ��������";
		}
		else if(iRet==201)
		{
			return "Ŀ������ں�������";
			
		}
		else if(iRet==202)
		{
			return "���ݰ������д�";
		}
		else if(iRet==203)
		{
			return "�ط�����δ����";
		}
		else if(iRet==204)
		{
			return "����ͨ������";
		}
		else if(iRet==999)
		{
			return "�������ζ���ʱ";
		}
		else if(iRet==-10)
		{
			return "����";
		}
		else 
		{
			return "δ֪����";
		}
	}
}
