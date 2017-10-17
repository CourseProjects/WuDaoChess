
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

public class StartGame extends Observable implements Runnable {//��Ϸ��ʼ

	public StartGame(){
		nn.init();//���ȳ�ʼ��
	}
	
	@Override
	public void run() {
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Back.start();
/*		for(int i = 0; i < ChessNum; i++){
			chessboard[i] = 0;//�Ȱ��������е�λ�ö����Ϊ��δ�¡�״̬
		}
		
		isGameOver = false;//��Ϸû�н���
		who = true;//�������
		while(nn.draw(chessboard) == 0 && !isGameOver){//���̶�û�����������Ϸû�н���
			setState("Human's Turn");//����Ϊ���״̬
			while(who && !isGameOver){
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};	
			if(nn.win(chessboard) == 1){//����������4��
				setState("Win!");//��ʾ���ʤ�����ҵ�������������ʤ��
				JOptionPane.showConfirmDialog(null,
						"U Win!", "GameOver", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			setState("Computer's Turn");//Ȼ���ǵ�������
			chessboard[nn.move(chessboard)] = 1;//���㷨����������µĵص���Ϊ1
			if(nn.win(chessboard) == 1){//�����������4��
				setState("Win!");//��ʾ�ǽ�����״̬
				JOptionPane.showConfirmDialog(null,//�������Ӯ�˲��ҵ���
						"U WIN!", "GameOver", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}


			who = true;//װ��״̬
		}
		if(nn.draw(chessboard) == 1){//�����������
			setState("LOST!");
			JOptionPane.showConfirmDialog(null,//��ʾ�������
					"U LOST!", "GameOver", JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
		}*/
	}
/**************�����Ծ���һЩ���������Լ���������*/
	public void human(int i, int j){
		Human.setX(i);
		Human.setY(j);
		if(i >= 0 && i < 5 && j >= 0 && j < 5 && chessboard[i][j] == 0 && who){
			chessboard[i][j] = 1;
			who = false;
			setChanged();
			notifyObservers();
		}
	}
	
	public boolean isEable(){
		return who;
	}
	
	public String getState(){
		return state;
	}
	
	private void setState(String state){
		this.state = state;
		setChanged();
		notifyObservers();
	}
	
	public void setGameOver(){
		isGameOver = true;
	}
	
	public int[][] getChessboard(){
		return chessboard;
	}
	
	public void show(){
		for(int i = 0;i < 4;i ++){
			for(int j = 0;j < 4;j ++){
				System.out.print(chessboard[4*i + j]);
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}
	
	private boolean isGameOver = true;
	private String state;
	private boolean who = false;
	//private NeuralNetwork nn = new NeuralNetwork();
	private AlphaBeta nn = new AlphaBeta();
	private int[][] chessboard = new int[GridNum][GridNum];
	private final static int ChessNum = 25;
	private final static int GridNum = 25;
}