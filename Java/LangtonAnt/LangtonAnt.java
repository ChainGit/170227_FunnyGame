package com.chain.c003;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * ��������
 * 
 * ������ �����������ɿ���˹�з����������ϸ���Զ��������ӡ�
 * 
 * ���壺 ��ƽ���ϵ������θ����Ϻ�ɫ���ɫ��������һ����������һֻ�����ϡ�������ͷ������������������һ����
 * �������ںڸ���ת90�ȣ����ø��Ϊ�׸���ǰ��һ���� �������ڰ׸���ת90�ȣ����ø��Ϊ�ڸ���ǰ��һ����
 * �ܶ�ʱ�����ϸոտ�ʼʱ���µ�·�߶����нӽ��Գơ����ǻ��ظ��� <br>
 * ��������ʼ״̬��Σ����ϵ�·�߱�Ȼ�����޳��ģ��γ�һ��"���ٹ�·"��
 * 
 * @author Chain
 *
 */
public class LangtonAnt extends JFrame implements Runnable {

	private static final long serialVersionUID = -869692582958968663L;

	// ���ڻ�������
	private JPanel p;
	// �����ٶ�
	private final int SPEED = 10;
	// ����Ĵ�С
	private final int BLOCK_SIZE = 6;
	// ��Ϸ�ռ�����
	private final int rows = 100;
	// ��Ϸ�ռ�����
	private final int columns = 100;
	// ��Ϸ��ͼ���ӣ�ÿ�����ӱ���һ�����飬�����¼�����״̬
	private boolean map[][] = new boolean[rows][columns];
	// ��������Ƿ������
	private boolean status = true;
	// ������ֻ��һֻ����
	private int antx = rows >> 1;
	private int anty = columns >> 1;
	// 0 �� 1�� 2�� 3��
	private int direction;

	// ������ɫ
	private final Color colorBlack = Color.BLACK;
	private final Color colorWhite = Color.WHITE;

	public LangtonAnt() {
		// ����������ڵ�λ��
		direction = (int) (Math.random() * 4);
		antx += (int) (Math.random() * 20 - 10);
		anty += (int) (Math.random() * 20 - 10);

		// ��ʼ������
		init();
	}

	// ��ʼ������
	private void init() {
		this.setTitle("��������");
		this.setSize(columns * BLOCK_SIZE + 10, rows * BLOCK_SIZE + 60);
		this.setLayout(new BorderLayout());
		this.setLocation(300, 50);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					status = !status;
			}
		});

		p = new JPanel() {

			private static final long serialVersionUID = 6546531907131303600L;

			// ��дpaint����,���ڻ��Ƹ���
			// JPanel��˫����,ֱ�ӻ���JFrame�п���
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				for (int i = 0; i < rows; i++)
					for (int j = 0; j < columns; j++) {
						boolean now = map[i][j];
						if (now)
							g.setColor(colorBlack);
						else
							g.setColor(colorWhite);
						g.fillRoundRect(j * BLOCK_SIZE, i * BLOCK_SIZE + 25, BLOCK_SIZE - 1, BLOCK_SIZE - 1,
								BLOCK_SIZE / 5, BLOCK_SIZE / 5);
					}
			}
		};

		this.add(p, BorderLayout.CENTER);

		this.setVisible(true);

	}

	// LangtonAntʵ��runnable�ӿ�,��main�̷ֿ߳�,���߼�����ʾ����
	@Override
	public void run() {
		while (true) {
			if (status)
				change(direction);
			this.repaint();
			try {
				Thread.sleep(SPEED);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// �ı������ɫ�����ϵķ���
	public void change(int dir) {
		// ��ֹԽ��
		if (antx > rows - 1 || anty > columns - 1 || antx < 0 || anty < 0) {
			status = false;
			return;
		}

		boolean current = map[antx][anty];
		map[antx][anty] = !current;
		if (current)
			// �ڸ���,��ת
			switch (dir) {
			case 0:
				direction = 3;
				antx++;
				break;
			case 1:
				direction = 2;
				antx--;
				break;
			case 2:
				direction = 0;
				anty--;
				break;
			case 3:
				direction = 1;
				anty++;
				break;
			}
		else
			// �׸���,��ת
			switch (dir) {
			case 0:
				direction = 2;
				antx--;
				break;
			case 1:
				direction = 3;
				antx++;
				break;
			case 2:
				direction = 1;
				anty++;
				break;
			case 3:
				direction = 0;
				anty--;
				break;
			}
	}

	public static void main(String[] args) {
		new Thread(new LangtonAnt()).start();
	}

}
