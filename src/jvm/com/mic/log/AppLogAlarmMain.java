package com.mic.log;

import com.mic.log.blots.em.ErrorBolt;
import com.mic.log.blots.em.FatalBolt;
import com.mic.log.blots.em.WarnBolt;
import com.mic.log.spouts.AppAlarmSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
/**
 * ʵ�ֶ�Ӧ����־�ı���
 * @author Chen
 *
 */
public class AppLogAlarmMain {

	/**
	 * Get data from db and send message and email for someone or group.
	 * @param args
	 * @throws InvalidTopologyException 
	 * @throws AlreadyAliveException 
	 */
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		TopologyBuilder builder=new TopologyBuilder();
		/*����һ���Ӧkafka����������*/
		builder.setSpout("Log-Alarm-Spout", new AppAlarmSpout(),10);
		//��������
		builder.setBolt("Log-Alarm-Fatal-Bolt",new FatalBolt(), 10)
						.fieldsGrouping("Log-Alarm-Spout","FATAL",new Fields("appname"));
		//���ش���
		builder.setBolt("Log-Alarm-ERROR-Bolt",new ErrorBolt(), 10)
						.fieldsGrouping("Log-Alarm-Spout","ERROR",new Fields("appname"));
		builder.setBolt("Log-Alarm-WARN-Bolt",new WarnBolt(), 10)
		//�������
						.fieldsGrouping("Log-Alarm-Spout","WARN",new Fields("appname"));
		Config conf=new Config();
		conf.setDebug(false);
		conf.setNumWorkers(3);
		if (args != null && args.length > 0) {
			StormSubmitter.submitTopology(args[0], conf,builder.createTopology());
		} else {
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("AlarmEM", conf,
			builder.createTopology());
		}
	}
}
