
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class ChessBoardFrame extends JFrame implements Observer {//��������

	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args){
		ChessBoardFrame play = new ChessBoardFrame();
		play.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//��ʾ���̵����
		play.setVisible(true);
	}
	
	public ChessBoardFrame() {	
		setTitle("������");//����Ϊ������
		
		setSize(250,250);//�������̵Ĵ�С��λ��
		setLocation(400,200);
		
		panel = new JPanel();
		GridLayout gridLayout = new GridLayout(5,5);
		panel.setLayout(gridLayout);
		panel.setBorder(new LineBorder(Color.black,2));//������ɫ
		
		startGame.addObserver(this);//
		
		initButton();
		
		JPanel bottomPanel = new JPanel();
		final JButton startButton = new JButton("Start Game");//��ӿ�ʼbutton
		startButton.addActionListener(new ActionListener(){//Button����������󴥷�����
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame.setGameOver();//��ʼ��Ϸ
				new Thread(startGame).start();
				startButton.setText("Restart");//����ʼ��ť������ΪRestart
			}
		});
		stateLabel = new JLabel("Human first");//��������ȿ�ʼ
		bottomPanel.add(startButton);//�������Ӱ�ť�ͱ�ǩ
		bottomPanel.add(stateLabel);
		
		add(BorderLayout.CENTER,panel);
		add(BorderLayout.SOUTH,bottomPanel);//�����Ƕ���������������
	}

	private void drawChessboard(int[][] chessboard){
		for(int i = 0; i < GridNum; i++){
			for(int j = 0; j < GridNum; j++){
				if(chessboard[i][j] == -1 && chess[i][j].getIcon() != rectIcon){//��������ʱ�����·���ͼ��
					chess[i][j].setIcon(rectIcon);
				}else if(chessboard[i][j] == 1 && chess[i][j].getIcon() != ovalIcon){//���µ�ʱ����Բ��ͼ��
					chess[i][j].setIcon(ovalIcon);
				}else if(chessboard[i][j] == 0 && chess[i][j].getIcon() != emptyIcon){//û���µĵط��ǿյ�
					chess[i][j].setIcon(emptyIcon);
				}
			}
		}
	}

	private void initButton(){//��ʼ��
		for(int i = 0; i < GridNum; i++){
			final int ii = i;
			for(int j =0; j < GridNum; j++){
				chess[i][j] = new JButton(emptyIcon);
				final int jj = j;
				chess[ii][jj].addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						if(chess[ii][jj].getIcon() == emptyIcon){
							chess[ii][jj].setIcon(ovalIcon);
						}
						startGame.human(ii,jj);
					}			
				});
				chess[ii][jj].setBorder(new LineBorder(Color.black));
				panel.add(chess[i][j]);
			}
		}
	}
	
	private Icon instanceIcon(final int style){
		final int startAdd = 10;
		final int cell = 50;
		final int chessman = 30;
		return new Icon(){
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				if(style == 0){
					g.fillRect(x+startAdd, y+startAdd, chessman, chessman);//��0����䷽��
				}else if(style == 1){
					g.fillOval(x+startAdd, y+startAdd, chessman, chessman);//��1�����Բ��
				}
			}
			@Override
			public int getIconWidth() {
				return cell;
			}
			@Override
			public int getIconHeight() {
				return cell;
			}
		};
	}
	//�����Ƕ����ǡ�����
	StartGame startGame = new StartGame();
	private Icon rectIcon = instanceIcon(0);
	private Icon ovalIcon = instanceIcon(1);
	private Icon emptyIcon = instanceIcon(2);
	
	JLabel stateLabel;
	private JButton[][] chess = new JButton[GridNum][GridNum];
	private JPanel panel;
	private final static int ChessNum = 25;
	private final static int GridNum = 5;

	@Override
	public void update(Observable o, Object arg) {
		StartGame sg = (StartGame)o;
		drawChessboard(sg.getChessboard());
		stateLabel.setText(sg.getState());
	}
}