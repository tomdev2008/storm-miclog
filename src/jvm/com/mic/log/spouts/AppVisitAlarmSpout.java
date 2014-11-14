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
 * ʵ�ֶԷ�����־�ı�����ҵ���߼��Ƚϼ򵥣�������Ҫ����ģ����ʵ�ֽ�Ϊ������㷨��
 * ��������켣��������ʵȵ�
 * @author Chen
 *
 */
public class AppVisitAlarmSpout extends BaseRichSpout {
	private static final Logger LOGGER = Logger.getLogger(AppVisitAlarmSpout.class);
	private ResultSetMetaData m=null;
	private Map<String,String> mapEmailChargeA=new HashMap<String,String>();//��Ҫ������
	private ResultSet rsAlarmList;
	private ResultSet rsAlarmInfoList;
	private ResultSet rsAlarmInfoVisitList;
	private SpoutOutputCollector _collector;
	//���·����ʼ���ϵ�״̬
	private static String updateFksAlarmInfo="update fks_applog set isalarm=1 where id={0}";
	//�����Ҫ�������ʼ����ֻ��б�
	private static String selectFksAlarmLit="select * from fks_alarmlist where allowalarm=1";
	@Override
	public void open(Map conf, TopologyContext context,SpoutOutputCollector collector) {
		this._collector=collector;
		try {
			rsAlarmList=MysqlUtils.select(selectFksAlarmLit);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	@Override
	public void nextTuple() {
		try {
			Thread.sleep(3000);
			rsAlarmInfoList=MysqlUtils.select("select * from fks_applog where isalarm=0 order by id desc LIMIT 1");
			ResultSetMetaData md = rsAlarmInfoList.getMetaData();
			while (rsAlarmInfoList.next()) {
				String id=rsAlarmInfoList.getString("id");
				String projectName=rsAlarmInfoList.getString("projectname");
				String appName=rsAlarmInfoList.getString("appname");
				String location=rsAlarmInfoList.getString("location");
				String ip=rsAlarmInfoList.getString("ip");
				String statuscode=rsAlarmInfoList.getString("statuscode");
				String recordip=rsAlarmInfoList.getString("recordip");
				String refrereurl=rsAlarmInfoList.getString("refrereurl");
				String targeturl=rsAlarmInfoList.getString("targeturl");
				String logdate=rsAlarmInfoList.getString("logdate");
				MysqlUtils.update(StrUtils.format(updateFksAlarmInfo, id));
				_collector.emit(new Values(id,projectName,appName,location,ip,statuscode,recordip,refrereurl,targeturl,logdate));
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
		declarer.declare(new Fields("id","projectname","appname","location","ip","statuscode","recordip","refrereurl","targeturl","logdate"));
	}

}
