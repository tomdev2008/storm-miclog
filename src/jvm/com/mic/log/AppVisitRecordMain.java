package com.mic.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mic.log.blots.en.AppVisitLogAnalyseBolt;
import com.mic.log.blots.en.AppVisitLogStatBolt;
import com.mic.log.blots.en.AppVisitLogAlarmToDbBolt;
import com.mic.log.spouts.AppVisitLogReaderSpout;
import com.mic.log.util.MysqlUtils;
import com.mic.log.util.PropertiesUtils;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class AppVisitRecordMain {
	private static String _isAllowCountTotal;
	private static String _startTime;
	/**
	 * ������־����ʵ������һ���flume������nginx���ڷ�����
	 * ʵ�ֶ�50x,404״̬�ļ��
	 * @param args
	 * @throws InvalidTopologyException 
	 * @throws AlreadyAliveException 
	 */
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		
		TopologyBuilder builder=new TopologyBuilder();
		/*����һ���Ӧkafka����������������ExcutorΪ10*/
		builder.setSpout("Log-EN-MainSpout", new AppVisitLogReaderSpout(),10);
		//����������־
		builder.setBolt("Log-En-ReaderBolt-1",new AppVisitLogAnalyseBolt(PropertiesUtils.getIntValue("itupleExcuteTime")), 20)
						.fieldsGrouping("Log-EN-MainSpout","log",new Fields("appname"));
		//��֯������־
		builder.setBolt("Log-En-StatBolt-2",new AppVisitLogStatBolt(), 20)
					.fieldsGrouping("Log-En-ReaderBolt-1", "log",new Fields("appname"))
					.allGrouping("Log-En-ReaderBolt-1", "stop");
		//��¼������־�����ݵ����ݿ⣬����Ҫ��������־
		builder.setBolt("Log-En-StoreBolt-3", new AppVisitLogAlarmToDbBolt(),10)
						.shuffleGrouping("Log-En-StatBolt-2");
		init();//�������³�ʼ��
		Config conf=new Config();
		conf.setDebug(false);
		conf.setNumWorkers(10);
		// ��Ⱥģʽ
		if (args != null && args.length > 0) {
			StormSubmitter.submitTopology(args[0], conf,
					builder.createTopology());
		} else {
			// ����ģʽ
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("logRecord", conf,
					builder.createTopology());
		}
	}
	public static void init()
	{
		_isAllowCountTotal=PropertiesUtils.getValue("isAllowCountTotal");
		_startTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		if(_isAllowCountTotal.equals("1"))//�Ƿ��������
		{
			try {
				MysqlUtils.update("update fks_tuple set count=0,starttime='"+_startTime+"' where id=1 or id=5");
				MysqlUtils.update("update fks_tuple set count=0,starttime='"+_startTime+"' where id=3");
				MysqlUtils.update("update fks_tuple set count=0,starttime='"+_startTime+"' where id=2");
				MysqlUtils.update("update fks_tuple set count=0,starttime='"+ _startTime + "' where id=4");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
