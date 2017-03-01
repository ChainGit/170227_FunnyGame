package com.chain.game.tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * ����˹���飺��̰���߲�ͬ���任ͼ����������Ӧ������
 * 
 * BUG����������Ļ����������
 * 
 * @author Chain
 *
 */
public class Tetris extends JFrame implements Runnable {

	private static final long serialVersionUID = 7261054718032677058L;

	// ����״̬
	enum State {
		// ��������ķ���
		ACTIVE,
		// �Ѿ��䵽�ײ��ķ���
		STOPPED,
		// �հ�����
		BLANK,
		// ��ת����
		ACTIVE_MID,
	}

	// ��Ϸ�ٶ�
	private final int SLOW = 500;
	// ���ٷ�������
	private final int FAST = 50;
	// ����
	private final int WAIT = 200;

	// ÿ������ı߳�
	private final int BLOCK_SIZE = 20;
	// ��Ϸ�ռ�����(4��3�ı���)
	private int rows = 24;
	// ��Ϸ�ռ�����(4��3�Ĺ�����)
	private int columns = 16;
	// ��Ϸ��ͼ���ӣ�ÿ�����ӱ���һ�����飬�����¼�����״̬
	private State map[][] = new State[rows][columns];
	// ����Ƿ�������Ϸ
	private boolean status = true;
	// ����Ƿ���ͣ����
	private boolean pause = false;
	// ������ɵ�ͼ���Ƿ���������
	private boolean fall = true;
	// ���ɵ�ͼ������һ���ڵ�ͼ����������������
	private int xbottom = 0;
	// ���ɵ�ͼ������
	private int xrows = 0;
	// ������ɵ�ͼ���Ƿ�����½�
	private boolean fast = false;
	// ��ǰ���ɵ�ͼ����״
	private int shape;
	// �÷�
	private int score;

	// ���ڻ�������
	private JPanel p;

	// ���ַ������ɫ
	private final Color COLOR_ACTIVE = Color.BLUE;
	private final Color COLOR_STOPPED = Color.GRAY;
	private final Color COLOR_SCORE = Color.GRAY;
	private final Color COLOR_BLANK = Color.WHITE;

	public Tetris() {
		// ��ʼ��������Ϣ
		this.setTitle("����˹����");
		this.setSize(columns * BLOCK_SIZE + 10, rows * BLOCK_SIZE + 60);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		// ��ʼ�����еķ���Ϊ��
		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map[i].length; j++)
				map[i][j] = State.BLANK;

		// ��������
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_SPACE) // ��������ͣ
					space();
				// ��Ϸδ��ͣʱ��Ӧ
				else if (!pause) {
					if (keyCode == KeyEvent.VK_LEFT) // ���� ����
						left();
					else if (keyCode == KeyEvent.VK_RIGHT) // ���� ����
						right();
					else if (keyCode == KeyEvent.VK_DOWN) // ���� ����
						accelerate();
					else if (keyCode == KeyEvent.VK_UP) // �� �� ��ת
						rotate();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_DOWN) // �ͷ����¼���ȡ�������½�
					fast = false;
			}
		});

		p = new JPanel() {

			private static final long serialVersionUID = -4394303302152969801L;

			// ��дJPanel��paint����
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				for (int i = 0; i < rows; i++)
					for (int j = 0; j < columns; j++) {
						if (map[i][j] == State.ACTIVE || map[i][j] == State.ACTIVE_MID) // ���ƻ��
							g.setColor(COLOR_ACTIVE);
						else if (map[i][j] == State.STOPPED) // ���ƾ�ֹ��
							g.setColor(COLOR_STOPPED);
						else
							g.setColor(COLOR_BLANK);
						g.fillRoundRect(j * BLOCK_SIZE, i * BLOCK_SIZE + 25, BLOCK_SIZE - 1, BLOCK_SIZE - 1,
								BLOCK_SIZE / 5, BLOCK_SIZE / 5);
					}

				// ��ӡ�÷�
				g.setColor(COLOR_SCORE);
				g.setFont(new Font("Times New Roman", Font.BOLD, 20));
				g.drawString("SCORE: " + score, 5, 20);

				// ��ӡ����/��ͣ
				g.setColor(COLOR_SCORE);
				g.setFont(new Font("Times New Roman", Font.BOLD, 20));
				g.drawString("STATUS: " + (pause ? "PAUSE" : "RUN"), this.getWidth() - 160, 20);

				// ��Ϸ����
				if (!status) {
					g.setColor(Color.RED);
					g.setFont(new Font("Times New Roman", Font.BOLD, 30));
					g.drawString("GAME OVER", this.getWidth() / 2 - 90, this.getHeight() / 2);
				}

			}

		};

		this.add(p);

		this.setVisible(true);
	}

	// ��ͣ�����
	private void space() {
		pause = !pause;
		this.repaint();
	}

	// ��������ķ���ͼ��
	private void generate() {
		Random rand = new Random();
		// ���������״
		shape = rand.nextInt(28);
		int colmid = columns >> 1;
		// ����������ȸŵĲ�����ͬ��ͼ�Σ��Ӷ�����ʼ
		// System.out.println(shape);
		switch (shape) {
		case 0: // һ��
		case 1:
			map[0][colmid] = map[0][colmid - 1] = map[0][colmid + 1] = map[0][colmid + 2] = State.ACTIVE;
			xrows = 1;
			break;
		case 2: // |��
		case 3:
			map[0][colmid] = map[1][colmid] = map[2][colmid] = map[3][colmid] = State.ACTIVE;
			xrows = 4;
			break;
		case 4: // L��1
			map[0][colmid] = map[1][colmid] = map[2][colmid] = map[2][colmid + 1] = State.ACTIVE;
			xrows = 3;
			break;
		case 5: // L��2
			map[0][colmid] = map[0][colmid - 1] = map[1][colmid - 1] = map[0][colmid + 1] = State.ACTIVE;
			xrows = 2;
			break;
		case 6: // L��3
			map[0][colmid] = map[1][colmid] = map[2][colmid] = map[0][colmid - 1] = State.ACTIVE;
			xrows = 3;
			break;
		case 7: // L��4
			map[0][colmid + 1] = map[1][colmid - 1] = map[1][colmid] = map[1][colmid + 1] = State.ACTIVE;
			xrows = 2;
			break;
		case 8: // ��L��1
			map[0][colmid] = map[1][colmid] = map[2][colmid] = map[2][colmid - 1] = State.ACTIVE;
			xrows = 3;
			break;
		case 9: // ��L��2
			map[0][colmid - 1] = map[1][colmid] = map[1][colmid - 1] = map[1][colmid + 1] = State.ACTIVE;
			xrows = 2;
			break;
		case 10: // ��L��3
			map[0][colmid] = map[1][colmid] = map[2][colmid] = map[0][colmid + 1] = State.ACTIVE;
			xrows = 3;
			break;
		case 11: // ��L��4
			map[0][colmid] = map[0][colmid - 1] = map[0][colmid + 1] = map[1][colmid + 1] = State.ACTIVE;
			xrows = 2;
			break;
		case 12: // T��1
			map[1][colmid] = map[1][colmid - 1] = map[1][colmid + 1] = map[0][colmid] = State.ACTIVE;
			xrows = 2;
			break;
		case 13: // T��2
			map[0][colmid] = map[1][colmid + 1] = map[2][colmid] = map[1][colmid] = State.ACTIVE;
			xrows = 3;
			break;
		case 14: // T��3
			map[0][colmid] = map[0][colmid - 1] = map[0][colmid + 1] = map[1][colmid] = State.ACTIVE;
			xrows = 2;
			break;
		case 15: // T��4
			map[1][colmid] = map[2][colmid] = map[1][colmid - 1] = map[0][colmid] = State.ACTIVE;
			xrows = 3;
			break;
		case 16: // ����
		case 17: // ����
		case 18: // ����
		case 19: // ����
			map[0][colmid] = map[0][colmid + 1] = map[1][colmid] = map[1][colmid + 1] = State.ACTIVE;
			xrows = 2;
			break;
		case 20: // Z��1
			map[0][colmid - 1] = map[1][colmid] = map[1][colmid + 1] = State.ACTIVE;
			map[0][colmid] = State.ACTIVE_MID;
			xrows = 2;
			break;
		case 21: // Z��2
			map[1][colmid - 1] = map[0][colmid] = map[2][colmid - 1] = State.ACTIVE;
			map[1][colmid] = State.ACTIVE_MID;
			xrows = 3;
			break;
		case 22: // Z��3
			map[0][colmid - 1] = map[0][colmid] = map[1][colmid + 1] = State.ACTIVE;
			map[1][colmid] = State.ACTIVE_MID;
			xrows = 2;
			break;
		case 23: // Z��4
			map[1][colmid] = map[0][colmid] = map[2][colmid - 1] = State.ACTIVE;
			map[1][colmid - 1] = State.ACTIVE_MID;
			xrows = 3;
			break;
		case 24: // ��Z��1
			map[0][colmid + 1] = map[1][colmid] = map[1][colmid - 1] = State.ACTIVE;
			map[0][colmid] = State.ACTIVE_MID;
			xrows = 2;
			break;
		case 25: // ��Z��2
			map[1][colmid - 1] = map[0][colmid - 1] = map[2][colmid] = State.ACTIVE;
			map[1][colmid] = State.ACTIVE_MID;
			xrows = 3;
			break;
		case 26: // ��Z��3
			map[0][colmid + 1] = map[0][colmid] = map[1][colmid - 1] = State.ACTIVE;
			map[1][colmid] = State.ACTIVE_MID;
			xrows = 2;
			break;
		case 27: // ��Z��4
			map[1][colmid] = map[0][colmid - 1] = map[2][colmid] = State.ACTIVE;
			map[1][colmid - 1] = State.ACTIVE_MID;
			xrows = 3;
			break;
		}
		// ��õײ���������
		xbottom = xrows - 1;
	}

	// ����
	private void fall() {
		// �Ƿ��ܹ�����
		fall = true;
		// �ӵ�ǰ�м�飬��������谭����ֹͣ����
		for (int i = 0; i < xrows; i++) {
			for (int j = 0; j < columns; j++)
				// ���������п�Ϊ��飬����һ�п�Ϊ��ֹ�飬�������谭
				if ((map[xbottom - i][j] == State.ACTIVE || map[xbottom - i][j] == State.ACTIVE_MID)
						&& map[xbottom - i + 1][j] == State.STOPPED) {
					// ��΢ͣ���£������������������
					if (!fast) {
						try {
							Thread.sleep(WAIT);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// �ٴ��ж�
						if ((map[xbottom - i][j] == State.ACTIVE || map[xbottom - i][j] == State.ACTIVE_MID)
								&& map[xbottom - i + 1][j] == State.STOPPED) {
							fall = false; // ֹͣ����
							break;
						}
					} else {
						fall = false; // ֹͣ����
						break;
					}
				}
			if (!fall)
				break;
		}

		// ��������
		if (fall) {
			// ͼ������һ��
			for (int i = 0; i < xrows; i++)
				for (int j = 0; j < columns; j++) {
					if (map[xbottom - i][j] == State.ACTIVE) { // ��������ƶ�һ��
						map[xbottom - i][j] = State.BLANK; // ԭ����ɿտ�
						map[xbottom - i + 1][j] = State.ACTIVE; // ��һ�п��ɻ��
					}
					if (map[xbottom - i][j] == State.ACTIVE_MID) { // ��������ƶ�һ��
						map[xbottom - i][j] = State.BLANK; // ԭ����ɿտ�
						map[xbottom - i + 1][j] = State.ACTIVE_MID; // ��һ�п��ɻ��
					}
				}

			// �ػ�
			this.repaint();
		} else if (xbottom < xrows)
			// ������С�����ɵ�ͼ��������˵��ͼ�θճ��־������谭�����Ѿ�������ͼ���Ϸ��ˣ���Ϸ����
			status = false;
	}

	// ������
	private void left() {
		// �������Ƿ����谭
		boolean hasBlock = false;

		// �ж��Ƿ�������谭
		for (int i = 0; i < xrows; i++)
			if ((map[xbottom - i][0] == State.ACTIVE) || (map[xbottom - i][0] == State.ACTIVE_MID)) { // �ж�����Ƿ�Ϊǽ
				hasBlock = true;
				break; // ���谭��������ѭ���ж���
			} else {
				for (int j = 1; j < columns; j++) // �ж�����Ƿ���������
					if ((map[xbottom - i][j] == State.ACTIVE || map[xbottom - i][j] == State.ACTIVE_MID)
							&& map[xbottom - i][j - 1] == State.STOPPED) {
						hasBlock = true;
						break; // ���谭��������ѭ���ж���
					}
				if (hasBlock)
					break; // ���谭��������ѭ���ж���
			}

		// ���û���谭����ͼ�������ƶ�һ����ľ���
		if (!hasBlock) {
			for (int i = 0; i < xrows; i++)
				for (int j = 1; j < columns; j++) {
					if (map[xbottom - i][j] == State.ACTIVE) {
						map[xbottom - i][j] = State.BLANK;
						map[xbottom - i][j - 1] = State.ACTIVE;
					}
					if (map[xbottom - i][j] == State.ACTIVE_MID) {
						map[xbottom - i][j] = State.BLANK;
						map[xbottom - i][j - 1] = State.ACTIVE_MID;
					}
				}

			// �ػ�
			this.repaint();
		}
	}

	// �����ߣ��������߲��
	private void right() {
		// ����ұ��Ƿ����谭
		boolean hasBlock = false;

		// �ж��Ƿ��ұ����谭
		for (int i = 0; i < xrows; i++)
			if ((map[xbottom - i][columns - 1] == State.ACTIVE) || map[xbottom - i][columns - 1] == State.ACTIVE_MID) { // �ж��ұ��Ƿ�Ϊǽ
				hasBlock = true;
				break; // ���谭��������ѭ���ж���
			} else {
				for (int j = 0; j < columns - 1; j++) // �ж��ұ��Ƿ���������
					if ((map[xbottom - i][j] == State.ACTIVE || map[xbottom - i][j] == State.ACTIVE_MID)
							&& map[xbottom - i][j + 1] == State.STOPPED) {
						hasBlock = true;
						break; // ���谭��������ѭ���ж���
					}
				if (hasBlock)
					break; // ���谭��������ѭ���ж���
			}

		// �ұ�û���谭����ͼ�������ƶ�һ����ľ���
		if (!hasBlock) {
			for (int i = 0; i < xrows; i++)
				for (int j = columns - 2; j >= 0; j--) {
					if (map[xbottom - i][j] == State.ACTIVE) {
						map[xbottom - i][j] = State.BLANK;
						map[xbottom - i][j + 1] = State.ACTIVE;
					}
					if (map[xbottom - i][j] == State.ACTIVE_MID) {
						map[xbottom - i][j] = State.BLANK;
						map[xbottom - i][j + 1] = State.ACTIVE_MID;
					}
				}

			// �ػ�
			this.repaint();
		}
	}

	// ��������
	private void accelerate() {
		// ��ǿ��Լ�������
		fast = true;
	}

	// ��ת����ת�������SRS(һ�ͺ�Z������)
	private void rotate() {
		try {
			// ��ת����
			int centerx = -1;
			int centery = -1;

			// ������ʱ�洢�任���ͼ��������ֱ�Ӳ���map��������
			int tmpx = -1;
			int tmpy = -1;
			State[][] tmp = null;
			// �������Ͻ�
			int startx = -1;
			int starty = -1;

			if (shape > 15 && shape < 20) { // ����
				return;
			} else if (shape < 4) { // һ��
				tmpx = 4;
				tmpy = 4;
				tmp = new State[4][4];

				// �ж��Ǻ��ŵĻ������ŵ�
				boolean kind = false;
				for (int i = 0; i < columns; i++)
					// �ҵ���飬����һ���ж�
					if (map[xbottom][i] == State.ACTIVE) {
						if (map[xbottom - 1][i] == State.ACTIVE)
							// ���ŵ�
							kind = true;
						// ���ŵĻ���ڶ��������ģ���ʱ����ת
						// ���ŵĻ�������������ģ�˳ʱ����ת
						if (kind) {
							centerx = xbottom - 2;
							centery = i;
						} else {
							centerx = xbottom;
							centery = i + 2;
						}
						break;
					}

				// ��ͼ�η�����������
				startx = centerx - 1;
				starty = centery - 2;
				for (int i = 0; i < tmpx; i++)
					for (int j = 0; j < tmpy; j++)
						if (map[startx + i][starty + j] != State.STOPPED)
							tmp[i][j] = map[startx + i][starty + j];

				// ��ת
				if (kind) {
					for (int i = 0; i < tmpx; i++)
						tmp[i][2] = State.BLANK;

					for (int i = 0; i < tmpy; i++)
						tmp[1][i] = State.ACTIVE;
				} else {
					for (int i = 0; i < tmpy; i++)
						tmp[1][i] = State.BLANK;

					for (int i = 0; i < tmpx; i++)
						tmp[i][2] = State.ACTIVE;
				}

				// ����Ƿ��ͻ,����ת���Ƿ�����STOPPED
				for (int i = 0; i < tmpx; i++)
					for (int j = 0; j < tmpy; j++) {
						State fact = map[i + startx][j + starty];
						State now = tmp[i][j];
						if ((now == State.ACTIVE || now == State.ACTIVE_MID) && fact == State.STOPPED)
							return;
					}

				if (kind) {
					xrows = 1;
					xbottom -= 2;
				} else {
					xrows = 4;
					xbottom += 2;
				}

			} else if (shape > 3 && shape < 16) { // (��)L�͡�T��
				tmpx = 3;
				tmpy = 3;
				tmp = new State[tmpx][tmpy];

				// ������
				// �ж��Ƿ��Ǻ��ŵ�
				int many = 0;
				boolean kind = false;
				for (int i = 0; i < xrows; i++) {
					many = 0;
					for (int j = 0; j < columns; j++) {
						if (map[xbottom - i][j] == State.ACTIVE)
							many++;
						// �ҵ����ŵ������������ŵ�
						if (many == 3) {
							kind = true;
							centerx = xbottom - i;
							centery = j - 1;
							break;
						}
					}
					if (many == 3)
						break;
				}

				// ���Ǻ��ŵģ����ж��Ƿ������ŵ�
				if (many != 3)
					for (int i = 0; i < columns; i++) {
						many = 0;
						for (int j = 0; j < xrows; j++) {
							if (map[xbottom - j][i] == State.ACTIVE)
								many++;
							// �ҵ����ŵ������������ŵ�
							if (many == 3) {
								kind = false;
								centerx = xbottom - 1;
								centery = i;
								break;
							}
						}
						if (many == 3)
							break;
					}

				// ������˳ʱ����ת90��
				startx = centerx - 1;
				starty = centery - 1;
				for (int i = 0; i < tmpx; i++)
					for (int j = 0; j < tmpy; j++)
						if (map[startx + i][starty + j] != State.STOPPED)
							tmp[j][tmpy - 1 - i] = map[startx + i][starty + j];

				// ����Ƿ��ͻ,����ת���Ƿ�����STOPPED
				for (int i = 0; i < tmpx; i++)
					for (int j = 0; j < tmpy; j++) {
						State fact = map[i + startx][j + starty];
						State now = tmp[i][j];
						if ((now == State.ACTIVE || now == State.ACTIVE_MID) && fact == State.STOPPED)
							return;
					}

				// �����������Ҫxbottem+1��xbottem-1
				for (int i = 0, j = 0; i < tmpy; i++) {
					if (map[centerx + 1][i + starty] == State.BLANK)
						j++;
					if (j == 3)
						xbottom++;
				}

				for (int i = 0, j = 0; i < tmpy; i++) {
					if (tmp[tmpx - 1][i] == State.BLANK)
						j++;
					if (j == 3)
						xbottom--;
				}

				if (kind)
					xrows = 3;
				else
					xrows = 2;

			} else if (shape > 19 && shape < 28) { // (��)Z��
				tmpx = 3;
				tmpy = 3;
				tmp = new State[tmpx][tmpy];

				// ������
				// �ж��Ƿ��Ǻ��ŵ�,3���зǿ��еĸ���
				boolean kind = false;
				boolean[] have = new boolean[tmpx];
				for (int i = 0; i < columns; i++)
					for (int j = 0; j < tmpx; j++)
						if (map[xbottom - j][i] == State.ACTIVE || map[xbottom - j][i] == State.ACTIVE_MID) {
							// ȷ������
							if (map[xbottom - j][i] == State.ACTIVE_MID) {
								centerx = xbottom - j;
								centery = i;
							}
							if (!have[tmpx - 1 - j])
								have[tmpx - 1 - j] = true;
						}

				for (int i = 0; i < tmpx; i++)
					if (!have[i]) {
						// ���ŵ�
						kind = true;
						break;
					}

				// ������˳ʱ����ת90��
				startx = centerx - 1;
				starty = centery - 1;
				for (int i = 0; i < tmpx; i++)
					for (int j = 0; j < tmpy; j++)
						if (map[startx + i][starty + j] != State.STOPPED)
							tmp[j][tmpy - 1 - i] = map[startx + i][starty + j];

				// ����Ƿ��ͻ,����ת���Ƿ�����STOPPED
				for (int i = 0; i < tmpx; i++)
					for (int j = 0; j < tmpy; j++) {
						State fact = map[i + startx][j + starty];
						State now = tmp[i][j];
						if ((now == State.ACTIVE || now == State.ACTIVE_MID) && fact == State.STOPPED)
							return;
					}

				// �����������Ҫxbottem+1��xbottem-1
				for (int i = 0, j = 0; i < tmpy; i++) {
					if (map[centerx + 1][i + starty] == State.BLANK)
						j++;
					if (j == 3)
						xbottom++;
				}

				for (int i = 0, j = 0; i < tmpy; i++) {
					if (tmp[tmpx - 1][i] == State.BLANK)
						j++;
					if (j == 3)
						xbottom--;
				}

				if (kind)
					xrows = 3;
				else
					xrows = 2;

			}

			// ������д��map
			// �����һ��ͼ��״̬
			for (int i = 0; i < tmpx; i++)
				for (int j = 0; j < tmpy; j++) {
					State fact = map[i + startx][j + starty];
					if (fact == State.ACTIVE || fact == State.ACTIVE_MID)
						map[i + startx][j + starty] = State.BLANK;
				}

			// д���µ�ͼ��״̬
			for (int i = 0; i < tmpx; i++)
				for (int j = 0; j < tmpy; j++) {
					State fact = map[i + startx][j + starty];
					if (fact == State.STOPPED)
						continue;
					map[i + startx][j + starty] = tmp[i][j];
				}

			this.repaint();
		} catch (Exception e) {
			// �����Ǳ任��Խ�����⣬ֱ���쳣�׳��Ҳ�������Ч�ʻή��
		}
	}

	// �ж��Ƿ���������
	private void judge() {
		int[] blocksCount = new int[rows]; // ��¼ÿ���з��������
		int eliminateRows = 0; // ����������
		// ����ÿ�з�������
		for (int i = 0; i < rows; i++) {
			blocksCount[i] = 0;
			for (int j = 0; j < columns; j++)
				if (map[i][j] == State.STOPPED)
					blocksCount[i]++;
		}

		// ʵ�������еķ�����������
		for (int i = 0; i < rows; i++)
			if (blocksCount[i] == columns) {
				// ���һ��
				for (int m = i; m >= 0; m--)
					for (int n = 0; n < columns; n++)
						map[m][n] = (m == 0) ? State.BLANK : map[m - 1][n];
				eliminateRows++; // ��¼��������
			}

		// �������
		score += eliminateRows;
		// �ػ淽��
		this.repaint();
	}

	// ���
	private void land() {
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				if (map[i][j] == State.ACTIVE || map[i][j] == State.ACTIVE_MID) // ���״̬��Ϊ��ֹ״̬
					map[i][j] = State.STOPPED;
		this.repaint();
	}

	@Override
	public void run() {
		while (true) { // ������Ϸ
			// ��Ϸ��������ͣ
			if (!status || pause) {
				try {
					Thread.sleep(SLOW);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			// ���ɷ���ͼ��
			generate();
			// ͼ��ѭ������
			while (xbottom < rows - 1) {
				// ��Ϸ��ͣ
				if (pause) {
					try {
						Thread.sleep(SLOW);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				fall(); // �½�
				if (!fall)
					break;
				xbottom++; // ÿ�½�һ�У�ָ�������ƶ�һ��
				// ���ߣ���������һ���룬δ��������500����
				try {
					Thread.sleep(fast ? FAST : SLOW);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			fast = false; // �����¼����٣�Ĭ�ϲ�����
			// ���䵽�����谭Ϊֹ���޸�ͼ�η���״̬
			land();
			// �ж��Ƿ��������
			judge();
		}
	}

	public static void main(String[] args) {
		new Thread(new Tetris()).start(); // ������Ϸ
	}
}
