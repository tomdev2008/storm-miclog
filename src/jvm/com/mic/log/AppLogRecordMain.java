package com.mic.log;

import com.mic.log.blots.em.ErrorOrFatalToDbBolt;
import com.mic.log.spouts.AppEnReaderSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
/**
 * ����Ӧ����־,ERROR/FATAL/WARN
 * @author Chen
 *
 */
public class AppLogRecordMain {

	/**��ȡENӦ����־������̨
	 * @param args
	 * @throws InvalidTopologyException 
	 * @throws AlreadyAliveException 
	 */
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		TopologyBuilder builder=new TopologyBuilder();
		//�����ȡkafka��Ϣ��spout
		builder.setSpout("Log-EN-AppMainSpout", new AppEnReaderSpout(),10);
		//������ϱ�����������������ݺ�λ����Ϣ���浽���ݿ�
		builder.setBolt("Log-SendEx-ToDb-Bolt",new ErrorOrFatalToDbBolt(), 10)
						.shuffleGrouping("Log-EN-AppMainSpout");
		
		Config conf=new Config();
		conf.setDebug(false);
		conf.setNumWorkers(2);
		// ��Ⱥģʽ
		if (args != null && args.length > 0) {
			StormSubmitter.submitTopology(args[0], conf,
					builder.createTopology());
		} else {
			// ����ģʽ
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("logAppEnRecord", conf,
					builder.createTopology());
		}
	}

}
