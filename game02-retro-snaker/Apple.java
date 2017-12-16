package com.chain.game.retrosnaker;

import java.util.Random;

/**
 * ƻ���ࣺһ�β���һ��ƻ��,��ֻ��һ��ƻ��
 * 
 * @author Chain
 *
 */
public class Apple {

	// ƻ����λ��
	private int x;
	private int y;
	// �������
	private State[][] sat;
	private int rows;
	private int columns;
	// ƻ��
	private static Apple ap;

	private Apple() {

	}

	private Apple(State[][] s) {
		init(s);
	}

	// ��ʼ��
	private void init(State[][] s) {
		if (sat == null) {
			sat = s;
			rows = sat[0].length;
			columns = sat.length;
		}
	}

	// ���һ��ƻ�����󲢲���һ��ƻ��λ�ã��������ģʽ
	public static Apple getApple(State[][] s) {
		if (ap == null)
			ap = new Apple(s);
		if (ap != null)
			ap.make();
		return ap;
	}

	// �����µ�ƻ��(ֻ��һ��ƻ��,ֻҪ��������λ�þ���)
	public void make() {
		if (ap == null)
			throw new RuntimeException("apple is null");

		if (sat[x][y] == State.APPLE)
			sat[x][y] = State.BLANK;

		Random r = new Random();
		while (true) {
			x = r.nextInt(columns);
			y = r.nextInt(rows);
			State t = sat[x][y];
			if (t == State.BLANK) {
				sat[x][y] = State.APPLE;
				break;
			}
		}
	}

}
