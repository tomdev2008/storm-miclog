package com.mic.log.service;


/*һ���м���An�Ƿ��ܹ��ϲ�����������һ��task�ڶ�An�Ĵ�������У�
����An�Ƿ��Ǻϲ����Ľ��������Ӱ���task����An���̵���ȷ�ԡ�
�ڡ�ӫ�⡱��Ŀ�Ŀ����У�������Ƴ���һ��CombiningBolt���ԶԿ�
����͸���ؽ����м����ϲ���ֻҪ�������Լ����м�����̳������µĽӿڣ�*/
public interface Combinable {
	public String ident();

	public Combinable combine(Combinable another);

	public int getCombinedCount();
}
