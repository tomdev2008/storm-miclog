package com.mic.log.spouts;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mic.log.util.LogUtils;
import com.mic.log.util.MysqlUtils;
import com.mic.log.util.PropertiesUtils;
import com.mic.log.util.StrUtils;
import com.mic.log.util.storm.RegUtils;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@SuppressWarnings("all")
/**
 * ����Ӧ����־�ı���spout
 * @author Chen
 *
 */
public class AppEnReaderSpout extends BaseRichSpout {
	public static Logger LOG = LoggerFactory.getLogger(AppEnReaderSpout.class);
	public static String topicName = "";
	private SpoutOutputCollector _collector;
	private ConsumerIterator<byte[], byte[]> it = null;
	private Map<String, String> contentMap = null;
	private String[] result;
	private String logDate = "";
	private Map<String, Map<String, String>> mapAlarmInfo;//��Ҫ�������ʼ��б�
	private long _lastTime;
	private long _currentTime;
	private List<String> _lsExceptionDetail;//�쳣��ϲ
	private boolean _isNeedWait=false;//�Ƿ���Ҫ�ȴ��쳣��Ϣ
	
	
	private Map<String, Map<String, String>> mapContent;
	private Map<String, String> mapLocation;
	
	private boolean _isFirstAlarm;//�Ƿ��Ǳ�����Ϣ���е���������쳣��Ϣ��ӡ���������汨��������־
	private boolean _isNormal=false;//���ܵ�������־Ϊֹ
	private boolean _isInfoDebug=false;
	private String logdate;
	private String loglevel;
	private String threadname;
	private String classfullname;
	private String logexception;
	private String projectname;
	private String appname;
	private String ip;
	private String location;
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this._collector = collector;
		this._lastTime = System.currentTimeMillis(); //��ʼ���쳣��Ϣ�����ʱ���
		this._lsExceptionDetail=new ArrayList<String>();
		this._isFirstAlarm=false;
		
		
		topicName = PropertiesUtils.getValue("topic-App");// ���Ҫ�����topic���ƣ���config.properties�ļ���
		Properties props = new Properties();
		props.put("zookeeper.connect",
				PropertiesUtils.getValue("zookeeper.connect"));// zookeeper�������ַ���
		props.put("group.id", PropertiesUtils.getValue("group-app"));// ����topic��Ϣ��������
		// ָ��kafka�ȴ����zookeeper�ظ���ms���Ա�������������ѡ�
		props.put("zookeeper.session.timeout.ms",
				PropertiesUtils.getValue("zookeeper.session.timeout.ms"));
		// ָ��zookeeperͬ����ӳٶ���ٲ����쳣
		props.put("zookeeper.sync.time.ms",
				PropertiesUtils.getValue("zookeeper.sync.time.ms"));
		// ָ����������߸���offset��zookeeper�С�
		// ע��offset����ʱ����time������ÿ�λ�õ���Ϣ��
		// һ���ڸ���zookeeper�����쳣���������������õ����õ�������Ϣ
		props.put("auto.commit.interval.ms",
				PropertiesUtils.getValue("auto.commit.interval.ms"));

		ConsumerConfig consumerConfig = new ConsumerConfig(props);// �����������ļ�
		ConsumerConnector consumer = Consumer
				.createJavaConsumerConnector(consumerConfig);
		Map topicCountMap = new HashMap();
		// ����Ҫ����kafka�ý��̻��ж��ٸ��߳��������Ӧ��topic
		topicCountMap.put(topicName, new Integer(1));
		Map consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream stream = (KafkaStream) ((List) consumerMap.get(topicName))
				.get(0);// ��ȡkafka stream����Ϣ
		this.it = stream.iterator();
	}

	@Override
	public void nextTuple() {
		try {
			if (this.it.hasNext()) {
				String msg = new String((byte[]) this.it.next().message());// ����һ����Ϣ��ת��Ϊ�ַ���
				Map<String, String> exceptionLog = new HashMap<String, String>();
				// �����Ϣ����20��ͷ��˵��Ӧ����һ��������Ϣ�����Ǵ����쳣����Ϊ��־�ĸ�ʽ���Ƕ���ͳһ��
				if (msg.substring(1, 3).toString().equals("20")) {
					_isNormal=true;
					if(_lsExceptionDetail.size()>0)
					{
						LOG.info(StrUtils.format("��־������Ϣ��" + "��־����:{0}---"
								+ "�߳�����:{1}---" + "��־����:{2}---" + "����ȫ��:{3}---"
								+ "�쳣��Ϣ:{4}---" + "��Ŀ����:{5}---" + "Ӧ������:{6}---"
								+ "IP��ַ��{7}---" + "������ַ:{8}---"+"�쳣��Ϣ:{9}", logdate,
								threadname, loglevel, classfullname, logexception,
								projectname, appname, ip, location,_lsExceptionDetail.toString()));
						_collector.emit(new Values(projectname, appname,location, ip, logdate, threadname, loglevel,classfullname, logexception,_lsExceptionDetail.toString()));
						_lsExceptionDetail.clear();
						_isNeedWait=false;//����Ҫ�ȴ��ˣ����¼����־
						_isFirstAlarm=false;//�������쳣����־���쳣��Ϣ����������Ϊfalse
					}
					// ���������Ϣ����ת��Ϊmap������ȡֵ
					mapContent = RegUtils.dealAppLog(msg);
					//String level = mapContent.get("logInfo").get("logLevel");// ��ñ����ļ�����ERROR,FATAL,WARN��

					mapLocation = LogUtils.splitLog(msg);
					logdate = mapContent.get("logInfo").get("logDate");//��־ʱ��
					threadname = mapContent.get("logInfo").get("threadName");//�߳�����
					loglevel = mapContent.get("logInfo").get("logLevel");//��־���
					classfullname = mapContent.get("logInfo").get("classFullName");//��ȫ��
					logexception = mapContent.get("logInfo").get("logException");//��־�쳣
					projectname = mapLocation.get("projectname");//��Ŀ����
					appname = mapLocation.get("appname");//Ӧ������
					ip = mapLocation.get("ip");//ip��ַ
					location = mapLocation.get("location");//������Ϣ
					
					// �������WARN,ERROR,FATAL��������Ҫ��¼
					// WARN:������;������ϢҪ���浽���ݿ�
					// ERROR:�ʼ�֪ͨ
					// FATAL:�ʼ�+����
					if (loglevel.toUpperCase().toString().equals("ERROR")|| loglevel.toUpperCase().toString().equals("FATAL")) {
						// ���ش�����־��map
						exceptionLog.put("mainExceptionInfo",mapContent.get("logInfo").toString());
						// ���ش�����־��λ����Ϣ
						exceptionLog.put("locationInfo",mapContent.get("locationInfo").toString());
						_isNeedWait=true;//����쳣������ERROR����FATAL,����������쳣��Ϣ
						_isFirstAlarm=true;
						_isInfoDebug=false;
					}
					else
					{
						_isNeedWait=false;//����;��һ�����쳣�������־����Ҫ�ȴ���
						_isInfoDebug=true;
					}
				}
				else
				{
					_isNormal=false;//�������쳣��־
					if(_isFirstAlarm)
					{
						_lsExceptionDetail.add((LogUtils.getLogContent(msg).toString().replace("[", ""))+"<br/>");
					}
					
				}
				long _currentTime = System.currentTimeMillis();
				//��Ҫ�ȴ���ϸ���쳣��Ϣ
				//���ߵȴ�4����
				//��������������־�����������һ�������������������Ϣϵͳ���в��Ϸ���Ϣ
			/*	if(_isNormal&&!_isNeedWait)
					{
											LOG.info(StrUtils.format("��־������Ϣ��" + "��־����:{0}---"
													+ "�߳�����:{1}---" + "��־����:{2}---" + "����ȫ��:{3}---"
													+ "�쳣��Ϣ:{4}---" + "��Ŀ����:{5}---" + "Ӧ������:{6}---"
													+ "IP��ַ��{7}---" + "������ַ:{8}---"+"�쳣��Ϣ:{9}", logdate,
													threadname, loglevel, classfullname, logexception,
													projectname, appname, ip, location,_lsExceptionDetail.toString()));
											_collector.emit(new Values(projectname, appname,location, ip, logdate, threadname, loglevel,classfullname, logexception));
											_lsExceptionDetail.clear();
											_lastTime = _currentTime;//�ȴ�4��
											_isNeedWait=false;//����Ҫ�ȴ��ˣ����¼����־
											_isFirstAlarm=false;//�������쳣����־���쳣��Ϣ����������Ϊfalse
					}*/
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// ��������еĶ���
		// ��Ŀ���ƣ�Ӧ�����ƣ�λ�ã�Ip��ַ����־ʱ�䣬�߳����ƣ�����������ȫ�̣��쳣��Ϣ
		declarer.declare(new Fields("projectname", "appname", "location", "ip",
				"logdate", "threadname", "level", "classfullname",
				"logexception","logexceptiondetail"));

	}
}
