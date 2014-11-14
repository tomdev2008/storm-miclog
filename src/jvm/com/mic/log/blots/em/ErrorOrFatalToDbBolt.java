package com.mic.log.blots.em;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mic.log.util.MysqlUtils;
import com.mic.log.util.StrUtils;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

/**
 * ʵ�ֶ�Ӧ����־������Ϣ�ļ�¼�����ݿ�
 * Ȼ��ͨ������һ��AppLogAlarmMainʵ��
 * �Ա�����Ϣ�ı������ʼ�+���ţ�
 * @version 1.0
 * @author Chen
 *
 */
public class ErrorOrFatalToDbBolt extends BaseRichBolt {
	private static final Logger LOG = Logger.getLogger(ErrorOrFatalToDbBolt.class);
	private OutputCollector _collector;
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this._collector=collector;
	}
	@Override
	public void execute(Tuple input) {
		String insertSql="";
		try {
			//Thread.sleep(2000);
			//��ȡSpout������ֵ
			String level = input.getStringByField("level");//��������
			String projectName = input.getStringByField("projectname");//��Ŀ����
			String appName = input.getStringByField("appname");//Ӧ������
			String location = input.getStringByField("location");//λ����Ϣ
			String ip = input.getStringByField("ip");//ip��ַ
			String logdate = input.getStringByField("logdate");//��־ʱ��
			String threadname = input.getStringByField("threadname");//�߳�����
			String classfullname = input.getStringByField("classfullname");//��ȫ��
			String logexception = input.getStringByField("logexception");//��־�쳣��Ϣ
			String logexceptiondetail=input.getStringByField("logexceptiondetail");//��־�쳣��ϸ��Ϣ
			String fullInfoException=logdate+"--"+threadname+"--"+classfullname+"--"+logexception;
			String createTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
			insertSql="insert into fks_alarminfo(projectname,appname,location,ip,logtime,level,description,context,isalarm)values({0},{1},{2},{3},{4},{5},{6},{7},{8})";
			insertSql=StrUtils.format(insertSql, 
					"'"+projectName+"'","'"+appName+"'","'"
					   +location+"'","'"+ip+"'","'"+logdate+"'","'"
					   +level+"'","'"+fullInfoException.replaceAll("'","\\\\'")+"'","'"+logexceptiondetail.replaceAll("'","\\\\'")+"'","0");
		   //��ʽ��SQL�ַ��������ұ��浽���ݿ⣬������һ��spoutʵ�ֱ���
			MysqlUtils.insert(insertSql);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch(Exception ex)
		{
			LOG.error(ex.getMessage());
			LOG.error(insertSql);
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

}
