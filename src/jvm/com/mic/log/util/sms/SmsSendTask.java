package com.mic.log.util.sms;

/*
 * �����첽����
 */
public class SmsSendTask {
	public static void sendSms(final String[] args) {
		Runnable thread = new Runnable() {
			public void run() {
				try {
					Call.SendSms(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(thread).start();
	}
}
