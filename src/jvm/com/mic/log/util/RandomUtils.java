package com.mic.log.util;

import java.io.Serializable;
import java.util.Random;
/**
 * ���������
 * @author Chen
 *
 */
public class RandomUtils implements Serializable{

	public static int random(int min, int max) {
		return (int) (Math.random() * (max + 1 - min) + min);
	}
	/**
	 * ��� ���� �ַ���
	 * 
	 * @param length
	 *            �ַ����ĳ���
	 * @return
	 */
	public static String createCode(int length) {
		Random rd = new Random();
		String code = "";
		int iCode = 0;
		int countNum = 0;
		for (int i = 0; i < length; i++) {
			iCode = rd.nextInt(122);
			if ((iCode >= 65 && iCode <= 90) || (iCode >= 97 && iCode <= 122)) {
				code += (char) iCode;
			} else if (iCode >= 0 && iCode <= 9) {
				countNum++;
				if (countNum > 4)
					i--;
				else
					code += iCode;
			} else
				i--;
		}
		return code;
	}
}
