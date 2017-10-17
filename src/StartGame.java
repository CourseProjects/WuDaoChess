
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

public class StartGame extends Observable implements Runnable {//游戏开始

	public StartGame(){
		nn.init();//首先初始化
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
			chessboard[i] = 0;//先把棋盘所有的位置都标记为“未下”状态
		}
		
		isGameOver = false;//游戏没有结束
		who = true;//玩家先手
		while(nn.draw(chessboard) == 0 && !isGameOver){//棋盘都没有下完而且游戏没有结束
			setState("Human's Turn");//设置为玩家状态
			while(who && !isGameOver){
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};	
			if(nn.win(chessboard) == 1){//如果玩家连成4子
				setState("Win!");//显示玩家胜利并且弹出弹框告诉玩家胜利
				JOptionPane.showConfirmDialog(null,
						"U Win!", "GameOver", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			setState("Computer's Turn");//然后是电脑下棋
			chessboard[nn.move(chessboard)] = 1;//将算法计算出来的下的地点置为1
			if(nn.win(chessboard) == 1){//如果电脑连成4子
				setState("Win!");//显示是结束的状态
				JOptionPane.showConfirmDialog(null,//告诉玩家赢了并且弹框
						"U WIN!", "GameOver", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}


			who = true;//装换状态
		}
		if(nn.draw(chessboard) == 1){//如果棋盘满掉
			setState("LOST!");
			JOptionPane.showConfirmDialog(null,//显示玩家输了
					"U LOST!", "GameOver", JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
		}*/
	}
/**************以下仍旧是一些变量定义以及函数定义*/
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