package com.mic.log.spouts;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mic.log.util.MysqlUtils;
import com.mic.log.util.StrUtils;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * ��ʱ��ѯ���ݿ���Ҫ�����ʼ����߶��ŵı�����Ϣ
 * ������־
 * @author Chen
 *
 */
public class AppAlarmSpout extends BaseRichSpout {
	private static final Logger LOGGER = Logger.getLogger(AppAlarmSpout.class);
	private ResultSetMetaData m=null;//�������ݿ�����meta��Ϣ
	private Map<String,String> mapEmailChargeA=new HashMap<String,String>();//��Ҫ������
	private ResultSet rsAlarmList;//֪ͨ���б�
	private ResultSet rsAlarmInfoList;
	private SpoutOutputCollector _collector;
	private int FATAL=0;
	//�����Ѿ�����֪ͨ��״̬
	private static String updateFksAlarmInfo="update fks_alarminfo set isalarm=1 where id={0}";
	//��ȡ��Ҫ�����ļ�¼��Ĭ��һ��ֻ��ȡһ��������ķ�ʽ�ǰ���Щ��¼����kafka��
	private static String selectFksAlarmInfo="select * from fks_alarminfo where isalarm=0 order by id desc LIMIT 1";
	//֪ͨ�˵��ʼ����绰��¼��
	private static String selectAlarmList="select * from fks_alarmlist where allowalarm=1";
	@Override
	public void open(Map conf, TopologyContext context,SpoutOutputCollector collector) {
		this._collector=collector;
		try {
			//���֪ͨ�˵��б�
			rsAlarmList=MysqlUtils.select(selectAlarmList);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void nextTuple() {
		try {
			Thread.sleep(1000);
			rsAlarmInfoList=MysqlUtils.select(selectFksAlarmInfo);
			ResultSetMetaData md = rsAlarmInfoList.getMetaData();
			while (rsAlarmInfoList.next()) {
				String id=rsAlarmInfoList.getString("id");
				String projectName=rsAlarmInfoList.getString("projectname");//��Ŀ����
				String appName=rsAlarmInfoList.getString("appname");//Ӧ������
				String location=rsAlarmInfoList.getString("location");//λ����Ϣ
				String ip=rsAlarmInfoList.getString("ip");//Ip��ַ
				String happentime=rsAlarmInfoList.getString("logtime");//��־ʱ��
				String description=rsAlarmInfoList.getString("description");//����
				String context=rsAlarmInfoList.getString("context");//��������Ϣ
				String level=rsAlarmInfoList.getString("level");//��������
				_collector.emit(level.toUpperCase(), new Values(id,projectName,appName,location,ip,happentime,description,context,level));
				//�����˾͸��£�������û�з����ʼ����߶��ųɹ���
				MysqlUtils.update(StrUtils.format(updateFksAlarmInfo, id));
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static int random(int min, int max) {
		return (int) (Math.random() * (max + 1 - min) + min);
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("FATAL",new Fields("id","projectname","appname","location","ip","happentime","description","context","level"));
		declarer.declareStream("ERROR",new Fields("id","projectname","appname","location","ip","happentime","description","context","level"));
		declarer.declareStream("WARN",new Fields("id","projectname","appname","location","ip","happentime","description","context","level"));
		declarer.declareStream("INFO",new Fields("id","projectname","appname","location","ip","happentime","description","context","level"));
		declarer.declareStream("DEBUG",new Fields("id","projectname","appname","location","ip","happentime","description","context","level"));
	}

}
