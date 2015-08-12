package edu.just.jpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JPanel;
/**
 * 
 * @author PSX
 *
 */
public class SnakePanel extends JPanel implements KeyListener {
	Integer body_length = 3;// 蛇身长度
	Map<Integer, Integer> body_state = new HashMap<>();// 蛇身状态,key---为蛇身节点的第几节;Value------为该节点的方向
	int head_direction;// 蛇头方向
	final int NORTH = 1, SOURTH = 2, EAST = 3, WEST = 4;
	int foodX = 200, foodY = 150;// 食物的坐标
	int food_width = 10, food_height = 10;// 食物大小
	int headX = 100, headY = 50;// 蛇头坐标
	int head_width = 20, head_height = 20;// 蛇头大小
	int body_width = 10, body_height = 10;// 蛇身节点大小
	ArrayList<Integer> bodyX = new ArrayList<Integer>();
	ArrayList<Integer> bodyY = new ArrayList<Integer>();
	LinkedList<Integer[]> pointOfChangeDirection;
	Map<Integer, Integer> usePoint = new HashMap<>();// 蛇身节点路过第几个转折点
	int flag;// 标志最后一个蛇节点转过一个路口
	boolean failFlag;// 标志失败

	public SnakePanel() {
		pointOfChangeDirection = new LinkedList<Integer[]>();
		for (int i = 0; i < body_length; i++) {
			bodyX.add(headX - body_width * (i + 1) + 1);
			bodyY.add(headY + (head_height - body_height) / 2);
		}
		head_direction = EAST;
		for (int i = 0; i < body_length; i++) {
			body_state.put(i, EAST);
		}
		for (int i = 0; i < body_length; i++) {
			usePoint.put(i, 0);
		}
		flag = 0;
		failFlag = false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		Graphics2D g2 = (Graphics2D) g;
		if (!failFlag) {
			// 画食物
			g2.setColor(Color.RED);
			g2.fillOval(foodX, foodY, 10, 10);
			// 画蛇头
			g2.setColor(Color.BLACK);
			g2.fillOval(headX, headY, head_width, head_height);
			// 画蛇身
			for (int i = 0; i < getLength(0); i++) {
				g2.fillOval(bodyX.get(i), bodyY.get(i), body_width, body_height);
			}
		} else {
			g2.setColor(Color.RED);
			g2.setFont(new Font("宋体",Font.PLAIN,30));
			g2.drawString("你输了，哈哈哈！",300, 200);
		}
	}

	// 增加蛇身长度
	public int getLength(int i) {
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		try {
			return this.body_length += i;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 控制蛇
	 */
	public void runSnake() {
		new Thread() {
			public void run() {
				while (true) {
					int direction = head_direction;
					// 蛇头运动
					switch (direction) {
					case NORTH:
						headY--;
						if (headY < -20) {
							headY = 600;
						}
						break;
					case SOURTH:
						headY++;
						if (headY > 600) {
							headY = -20;
						}
						break;
					case EAST:
						headX++;
						if (headX > 800) {
							headX = -20;
						}
						break;
					case WEST:
						headX--;
						if (headX < -20) {
							headX = 800;
						}
						break;
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 蛇身运动
					for (int i = 0; i < getLength(0); i++) {
						if (flag != pointOfChangeDirection.size() && pointOfChangeDirection.size() != 0) {
							int b = usePoint.get(i);
							if (b > pointOfChangeDirection.size() - 1) {
							} else {
								Integer[] a = pointOfChangeDirection.get(b);
								int x = a[0];
								int y = a[1];
								int m = bodyX.get(i);
								int n = bodyY.get(i);
								if (m == x && n == y) {
									if (i == 0) {
										body_state.replace(i, direction);
									} else {
										body_state.replace(i, body_state.get(i - 1));
									}
									usePoint.replace(i, b + 1);
									if (i == body_length - 1) {
										flag++;
									}
								}
							}
						}
						int body_direction = body_state.get(i);
						switch (body_direction) {
						case NORTH:
							bodyY.set(i, bodyY.get(i) - 1);
							if (bodyY.get(i) < -20) {
								bodyY.set(i, 600);
							}
							break;
						case SOURTH:
							bodyY.set(i, bodyY.get(i) + 1);
							if (bodyY.get(i) > 610) {
								bodyY.set(i, -10);
							}
							break;
						case EAST:
							bodyX.set(i, bodyX.get(i) + 1);
							if (bodyX.get(i) > 810) {
								bodyX.set(i, -10);
							}
							break;
						case WEST:
							bodyX.set(i, bodyX.get(i) - 1);
							if (bodyX.get(i) < -20) {
								bodyX.set(i, 800);
							}
							break;
						}
					}
					// 控制食物
					// 蛇头与食物中心点距离平方
					int dd = (foodX + 5 - headX - head_width / 2) * (foodX + 5 - headX - head_width / 2)
							+ (foodY + 5 - headY - head_height / 2) * (foodY + 5 - headY - head_height / 2);
					// 如果蛇头碰到食物，则食物要重定位，蛇长度加一
					if (dd <= 225) {
						// 食物重定位
						while (dd <= 225) {
							foodX = (int) (Math.random() * 750);
							foodY = (int) (Math.random() * 550);
							dd = (foodX + 5 - headX - head_width / 2) * (foodX + 5 - headX - head_width / 2)
									+ (foodY + 5 - headY - head_height / 2) * (foodY + 5 - headY - head_height / 2);
						}
						// 蛇长度加一
						getLength(1);
						// 定位新增蛇节点
						int lastDirection = body_state.get(bodyX.size() - 1);
						switch (lastDirection) {
						case NORTH:
							body_state.put(bodyX.size(), NORTH);
							bodyX.add(bodyX.get(bodyX.size() - 1));
							bodyY.add(bodyY.get(bodyY.size() - 1) + body_height);
							break;
						case SOURTH:
							body_state.put(bodyX.size(), SOURTH);
							bodyX.add(bodyX.get(bodyX.size() - 1));
							bodyY.add(bodyY.get(bodyY.size() - 1) - body_height);
							break;
						case EAST:
							body_state.put(bodyX.size(), EAST);
							bodyX.add(bodyX.get(bodyX.size() - 1) - body_height);
							bodyY.add(bodyY.get(bodyY.size() - 1));
							break;
						case WEST:
							body_state.put(bodyX.size(), WEST);
							bodyX.add(bodyX.get(bodyX.size() - 1) + body_height);
							bodyY.add(bodyY.get(bodyY.size() - 1));
							break;
						}
						usePoint.put(bodyX.size() - 1, usePoint.get(bodyX.size() - 2));
					}
					//显示输赢
					for (int i = 1; i < getLength(0); i++){
						int dd1=(bodyX.get(i) + 5 - headX - head_width / 2) * (bodyX.get(i) + 5 - headX - head_width / 2)
								+ (bodyY.get(i) + 5 - headY - head_height / 2) * (bodyY.get(i) + 5 - headY - head_height / 2);
						if(dd1<225){
							failFlag=true;
						}
					}
					repaint();
				}
			}
		}.start();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		Integer[] a = new Integer[2];
		a[0] = headX + body_width / 2;
		a[1] = headY + body_height / 2;
		switch (key) {
		case KeyEvent.VK_UP:
			head_direction = NORTH;
			pointOfChangeDirection.add(a);
			break;
		case KeyEvent.VK_DOWN:
			head_direction = SOURTH;
			pointOfChangeDirection.add(a);
			break;
		case KeyEvent.VK_LEFT:
			head_direction = WEST;
			pointOfChangeDirection.add(a);
			break;
		case KeyEvent.VK_RIGHT:
			head_direction = EAST;
			pointOfChangeDirection.add(a);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
