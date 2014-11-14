package com.mic.log.blots.em;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mic.log.util.MysqlUtils;
import com.mic.log.util.PropertiesUtils;
import com.mic.log.util.StrUtils;
import com.mic.log.util.email.EmailSendTask;
import com.mic.log.util.email.MailGetList;
import com.mic.log.util.email.MailMessage;
import com.mic.log.util.sms.Call;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
/**
 * ��������ı�����Ӧ����־
 * @author Chen
 *
 */
public class FatalBolt extends BaseRichBolt {
	
	private static final Logger LOGGER = Logger.getLogger(FatalBolt.class);
	private OutputCollector _collector;
	private static String _emailTitle = "";
	private Map<String, Map<String, String>> _alarmList;
	//��һ������
	private String _alarmMailChargeA = "";
	//����Ա�����һ�������ߵĳ�����Ҫ����ʼ��Ŀ�����
	private String _alarmAdministrator="";
	//�õ��������ʼ��б����ֻ�����
	private static String _getAlaramList="select * from fks_alarmlist where allowalarm=1";
	private int _count = 0;
	private String _smsUserName="";
	private String _smsPassWord="";
	private String _smsType="";
	private String _smsExt="414";
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {	
		this._collector = collector;
		this._emailTitle = PropertiesUtils.getValue("log.alarm.title1");
		this._alarmMailChargeA = PropertiesUtils.getValue("mail.SMTPUsername");
		this._alarmAdministrator=PropertiesUtils.getValue("log.alarm.administrator");
		this._smsPassWord=PropertiesUtils.getValue("smsPassWord");
		this._smsUserName=PropertiesUtils.getValue("smsUserName");
		this._smsType=PropertiesUtils.getValue("smsType");
		this._smsExt=PropertiesUtils.getValue("smsExt");
		try {
			//��ȡ��Ҫ�������ʼ��б���껰
			this._alarmList = MailGetList.resultSetToArrayList(MysqlUtils.select(_getAlaramList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(Tuple input) {
		try {
			String level = input.getSourceStreamId();//�������ļ���
			String id = input.getStringByField("id");//����id
			String projectName = input.getStringByField("projectname");//��Ŀ����
			String appName = input.getStringByField("appname");//Ӧ������
			String location = input.getStringByField("location");//λ����Ϣ
			String ip = input.getStringByField("ip");//ip��ַ
			String happentime = input.getStringByField("happentime");//����ʱ��
			String description = input.getStringByField("description");//����
			String context = input.getStringByField("context");//��������Ϣ
			String alarmTitle=StrUtils.format(_emailTitle, level, projectName,appName);
			String mailContent = StrUtils.format(PropertiesUtils.getValue("log.alarm.content1"), projectName, appName,level, new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z",Locale.ENGLISH).format(new Date()), location, ip,description);
			final MailMessage mail = new MailMessage(alarmTitle,
					_alarmMailChargeA,//�ʼ�������
					_alarmList.get(appName).get("receiveEmailList").toString().split(","),// tos������
					_alarmList.get(appName).get("ccEmailList").toString().split(","),// ccs������
					new String[] {_alarmAdministrator},// bccs����
					mailContent, new String[] {});//�ʼ�����
			String[] paraArray=new String[10];
			paraArray[0]=_smsUserName;
			paraArray[1]=_smsPassWord;
			paraArray[2]=_alarmList.get(appName).get("phonenumber");
			paraArray[3]=_smsType;
			paraArray[4]="";
			paraArray[5]=alarmTitle;
			paraArray[6]=null;
			paraArray[7]=_smsExt;
			paraArray[8]="";
			paraArray[9]="";
			String Rv=Call.SendSms(paraArray);//�����ֻ�����
			LOGGER.info("Send Sms;Serial Number is:"+(++_count)+"_Level:FATAL");//��¼����
			EmailSendTask.sendMail(mail);//�����ʼ�
			LOGGER.info("Send Email;Serial Number is:"+(++_count)+"_Level:FATAL");//��¼�ʼ�
		} catch (Exception ex) {
			LOGGER.info("FATAL_Bolt:" + ex.toString());
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

}
