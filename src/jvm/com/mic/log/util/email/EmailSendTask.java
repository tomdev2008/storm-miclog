package com.mic.log.util.email;
/**
 * �ʼ��첽����
 * @author Chen
 *
 */
public class EmailSendTask {
	public static void sendMail(final MailMessage mail) {
		Runnable thread = new Runnable() {
			public void run() {
				MailUtils.sendEmail(mail);
			}
		};
		new Thread(thread).start();
	}
}
