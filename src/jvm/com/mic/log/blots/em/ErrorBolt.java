package com.mic.log.blots.em;

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

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
/**
 * Ӧ����־��ӡERRORʵ���ʼ�֪ͨ
 * @author Chen
 *
 */
public class ErrorBolt extends BaseRichBolt {
	private static final Logger LOGGER = Logger.getLogger(FatalBolt.class);
	private OutputCollector _collector;
	private static String _emailTitle = "";
	private Map<String, Map<String, String>> _alarmList;
	//��Ŀ��һ������
	private String _alarmMailChargeA = "";
	//����Ա�����һ�������ߵĳ�����Ҫ����ʼ��Ŀ�����
	private String _alarmAdministrator="";
	//�õ��������ʼ��б����ֻ�����
	private static String _getAlaramList="select * from fks_alarmlist where allowalarm=1";
	private int _count = 0;
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this._collector = collector;
		this._emailTitle = PropertiesUtils.getValue("log.alarm.title1");
		this._alarmMailChargeA = PropertiesUtils.getValue("mail.SMTPUsername");
		this._alarmAdministrator=PropertiesUtils.getValue("log.alarm.administrator");
		try {
			this._alarmList = MailGetList.resultSetToArrayList(MysqlUtils.select(_getAlaramList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void execute(Tuple input) {
		try {
			String level = input.getSourceStreamId();//�������ļ���
			String id = input.getStringByField("id");//id
			String projectName = input.getStringByField("projectname");//��Ŀ����
			String appName = input.getStringByField("appname");//Ӧ������
			String location = input.getStringByField("location");//����λ��
			String ip = input.getStringByField("ip");//ip��ַ
			String happentime = input.getStringByField("happentime");//����ʱ��
			String description = input.getStringByField("description");//����
			String context = input.getStringByField("context");
			//�ʼ�����
			String mailContent = StrUtils.format(
					PropertiesUtils.getValue("log.alarm.content1"), 
					projectName, appName,level, 
					new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z",Locale.ENGLISH).format(new Date()), 
					location, ip,description, context);
			//���巢���ʼ�������
			final MailMessage mail = new MailMessage(StrUtils.format(_emailTitle, level, projectName,appName),
					_alarmMailChargeA,
					_alarmList.get(appName).get("receiveEmailList").toString().split(","),// tos
					_alarmList.get(appName).get("ccEmailList").toString().split(","),// ccs
					new String[] {_alarmAdministrator},// bccs
					mailContent, new String[] {});
			EmailSendTask.sendMail(mail);//�����ʼ�
			LOGGER.info("Send Email;Serial Number is:"+(++_count)+"_Level:ERROR");
		} catch (Exception ex) {
			LOGGER.info("ERROR_Bolt:" + ex.toString());
		}	
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}
}
