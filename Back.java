import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.List;


public class Back {
	private final static int randMax = Integer.MAX_VALUE;
	private final static int weightLength = 25*9+9;
	public static int getRandmax() {
		return randMax;
	}
	public static int getWeightlength() {
		return weightLength;
	}
	public static void main(String[] args) { 
		// argv=-t  执行培训程序 生成train.txt
		// argv=-weight  后手  -nextState 先手 
		//缺省 先手

		Random rand = new Random(); 
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < weightLength; j++) {
				//weight[i][j] = (int)(rand()%10);
				Human.setWij(-10 + (int)(20.0*rand.nextFloat() / (randMax + 1.0)), i, j);
				//printf("%d ", weight[i][j]);
			}
		}
		System.out.println("");


		char ch;
		ch = 'b'; //缺省采用人机对弈 人先行
		System.out.print("\n wudaoqi game ver0.8 2015-1-5");
		System.out.println("\n argument: -nextState black  -weight white -t train\n");
		
		switch (ch)
		{
		case 'b':
			Human.humanChess(0, true);
			break;
		case 'w':
			Human.humanChess(0, false);
			break;
		case 't':
			Chess.train();
			break;
		default:
			System.out.println("error argument");
			System.out.println("-nextState black  -weight white -t train");
		}
	}
}
class Human{
	private static int[][] weight = new int[10][Back.getWeightlength()];	//存储每一代种群中每个个体的权值
	private static int[][] best = new int[5][Back.getWeightlength()];	//父代中最好的5个个体
	private static int depth = 8 ;	//搜索深度
	private final static int generation = 5;
	private static int x;
	private static int y;
	public static int getGeneration() {
		return generation;
	}
	public static int getBestij(int i, int j) {
		return best[i][j];
	}
	public static void setBestij(int num, int i, int j) {
		best[i][j] = num;
	}
	public static int getWij(int i, int j) {
		return weight[i][j];
	}
	public static void setWij(int num, int i, int j) {
		weight[i][j] = num;
	}
	public static int getDepth() {
		return depth;
	}
	public static void setDepth(int depth) {
		Human.depth = depth;
	}
	public static int getY() {
		return y;
	}
	public static int getX() {
		return x;
	}
	public static void setX(int x) {
		Human.x = x;
	}
	public static void setY(int y) {
		Human.y = y;
	}
	public static int humanChess(int num, Boolean first){

		//人工和电脑对弈，其中num为对应神经元数字 first=true 表示人下BLACK
		int firstColor, secondColor;
		if (first){
			firstColor = Chess.getBlack();
			secondColor = Chess.getWhite();
		}
		else  {
			firstColor = Chess.getWhite();
			secondColor = Chess.getBlack();
		}
		
		setDepth(5);		//搜索深度设置为5
		//将最好的一组培训后的权值恢复到w[0]
		File file = new File("D:/Eclipse/MyWorkSpace/Back/bin/weight.txt");
		if(!file.exists()){
			System.out.println("Can not open weight.txt,please first use -t train");
		}
		List<String> linesRead = null;
        try {
            // read back
            linesRead = Files.readAllLines(file.toPath());
        } catch (IOException ex) {
            System.out.println("Can not open weight.txt,"
            		+ "please first use -t train");
            return 0;
        }
		for (int i = 0; i < Back.getWeightlength();)
		{
			if(linesRead.isEmpty()){
				break;
			}else{
				for(String line : linesRead){
					String[] str = line.split(" ");
					for(String s : str){
						setWij(Integer.valueOf(s), 0, i++);
					}
				}
			}
		}

		while (true){
			Chess.printChessboard();
			if (first){
				System.out.print("\n enter your move [0-4,0-4] = 0,0   ");

				Scanner input = new Scanner(System.in);
				x = input.nextInt();
				y = input.nextInt();
				if (x>4 || x<0 || y<0 || y>4){
					System.out.println("illegal move, game over");
					return 0;
				}


				Chess.move(x, y, firstColor);

				//now 切换走子顺序
			}
			else first = true; //后面可以切换到走棋方了	


			Chess.bestMove(x, y, secondColor);
			Chess.move(x, y, secondColor);



			if (Chess.isWin() == Chess.getBlack()) return Chess.isWin();
		}
	}
}

class Chess{
	private final static int black = 1;
	private final static int white = -1;
	private static int[] winScore = new int[10];	//父代相互对弈得到的计分
	private static int chessNum;	//棋盘上棋子的个数
	private static final int chessBoardNum = 25;	//棋盘总的子数
	private static int[][] chessBoardState = 
			new int [Grid.getGridNum()][Grid.getGridNum()];//棋盘状态
	private static int totalStep = 0;		//总共的步数
	private static int[] tempState = new int[Grid.getGridNum()];
	private static Stack<ArrayList<Integer>> stackOfChessBoard = new Stack<ArrayList<Integer>>();
	
	public static int getBlack() {
		return black;
	}
	public static int getWhite() {
		return white;
	}
	public static int getWinScorei(int i){
		return winScore[i];
	}
	public static void setWinScorei(int i, int num){
		winScore[i] = num;
	}
	public static int bestMove(int row, int col, int player){
		//返回当前棋盘局面下,player的最优选择
		//返回坐标为x,y 
		int x_pos = 0, y_pos = 0;
		int var = -100000;
		int maxEstmt = -100000;
		int tempDepth = 1;
		for (int x = 0; x < Grid.getGridNum(); x++) {
			for (int y = 0; y < Grid.getGridNum(); y++) {
				//选取可以落子位置
				if (chessBoardState[x][y] == 0) {


					pushChessBoard();
					int eatFlag = move(x, y, player); //检测有无吃子
					//计算机试探的走一步棋，棋盘状态改变了，在该状态下计算出深度为dep-1的棋盘状态估计值val
					var = alphaBeta(var, tempDepth, 1, 0);//缺省用第一个神经网络系数
					var = var + 1000 * eatFlag; //增加吃子奖励
					if (var > maxEstmt) {    //m要记录通过试探求得的棋盘状态的最大估计值
						maxEstmt = var;
						x_pos = x;
						y_pos = y;
						//cout<<"The computer put the qizi at:"<<x_pos+1<<y_pos+1<<endl;
					}
					if (isWin() == player){
						x_pos = x;
						y_pos = y;
						return 1;
					}


					popChessBoard();
				}
			}

		}
		col = y_pos;
		row = x_pos;
		Human.setY(col);
		Human.setX(row);
		System.out.println("computer maxEstmt=" + maxEstmt + row + ":" + col);
		return 0;
	}
	//对弈函数
	public static void fight(int tempState, int nextState) //a对应先手神经网络旗手，b对应后手神经网络旗手
	{
		int maxEstmt = -10001, var = -10000, tempDepth = 1;   //m用来存放最大的val
		int x_pos = 0, y_pos = 0;                //记录最佳走步的坐标
		initialChessBoard();
		//cout<<"Qipan: "<<endl;
		//printChessboard();
		while (true) {
			for (int x = 0; x < Grid.getGridNum(); x++) {
				for (int y = 0; y < Grid.getGridNum(); y++) {
					if (chessBoardState[x][y] == 0) {

						//QP[x][y] = 1;
						pushChessBoard(); //保存棋盘状态
						move(x, y, black); //移动棋子，并进行吃子检测

						var = alphaBeta(var, tempDepth, 1, tempState);//计算机试探的走一步棋，棋盘状态改变了，在该状态下计算出深度为dep-1的棋盘状态估计值val
						if (var > maxEstmt) {             //m要记录通过试探求得的棋盘状态的最大估计值
							maxEstmt = var;
							x_pos = x;
							y_pos = y;
							//cout<<"The computer put the qizi at:"<<x_pos+1<<y_pos+1<<endl;
						}
						//computer1赢了
						if (isWin() == 1) {
							//cout<<"The computer1 put the qizi at:"<<x+1<<y+1<<endl;
							printChessboard();
							System.out.println("The computer 1 WIN! GAME OVER."); 
							winScore[tempState] += 3;
							return;
						}

						//QP[x][y]=0;
						popChessBoard(); //恢复棋盘状态
					}
				}
			}
			//QP[x_pos][y_pos] = 1;

			//chessnum ++ ;
			chessNum = chessNum + 1 - move(x_pos, y_pos, black); //选中落子,并进行吃子检测
			var = -10000;
			maxEstmt = -10001;
			tempDepth = 1;
			//cout<<"The computer1 put the qizi at:"<<x_pos+1<<y_pos+1<<endl;
			//printChessboard();
			//cout<<endl;
			if (chessNum >= chessBoardNum) {
				if (isWin() == 1) {
					winScore[tempState] += 3;
					printChessboard();
					System.out.print("computer 1 win");
				}
				else{
					winScore[nextState] += 3;
					printChessboard();
					System.out.print("computer 2 win ");
				}
				return;
			}


			for (int x = 0; x < Grid.getGridNum(); x++) {
				for (int y = 0; y < Grid.getGridNum(); y++) {
					if (chessBoardState[x][y] == 0) {

						//QP[x][y] = 2;
						pushChessBoard();
						move(x, y, black);
						var = alphaBeta(var, tempDepth, 1, nextState);//计算机试探的走一步棋，棋盘状态改变了，在该状态下计算出深度为dep-1的棋盘状态估计值val
						//computer2赢了
						if (isWin() == 2) {
							//cout<<"The computer2 put the qizi at:"<<x+1<<y+1<<endl;
							printChessboard();
							System.out.println("The computer2 WIN! GAME OVER.");
							winScore[nextState] += 3;
							return;
						}

						if (var > maxEstmt) {    //m要记录通过试探求得的棋盘状态的最大估计值
							maxEstmt = var;
							x_pos = x;
							y_pos = y;
							//cout<<"The computer put the qizi at:"<<x_pos+1<<y_pos+1<<endl;
						}

						//QP[x][y]=0;
						popChessBoard();
					}
				}
			}
			//QP[x_pos][y_pos] = 2;

			//chessnum ++ ;
			chessNum = chessNum + 1 - move(x_pos, y_pos, white);
			var = -10000;
			maxEstmt = -10001;
			tempDepth = 1;
			//cout<<"The computer2 put the qizi at:"<<x_pos+1<<y_pos+1<<endl;
			//printChessboard();
			//cout<<endl;
			if (chessNum >= chessBoardNum) {
				if (isWin() == 1) {
					winScore[tempState] += 3;
					printChessboard();
					System.out.print("computer 1 win");
				}
				else{
					winScore[nextState] += 3;
					printChessboard();
					System.out.print("computer 2 win ");
				}
				return;
			}

		}
	}
	
	//初始化棋盘
	public static void initialChessBoard()
	{
		for (int i = 0; i < Grid.getGridNum(); i++) {
			for (int j = 0; j < Grid.getGridNum(); j++) {
				chessBoardState[i][j] = 0;
			}
		}
		chessNum = 0;
	}
	//打印棋盘
	public static void printChessboard()
	{
		int blacknum = 0;
		int whitenum = 0;
		System.out.println("\n    0  1  2  3  4");
		System.out.println("-------------------------");
		for (int i = 0; i < Grid.getGridNum(); i++) {
			System.out.print(" " + i);
			for (int j = 0; j < Grid.getGridNum(); j++) {
				//cout <<" " << QP[i][j] << " ";
				System.out.printf("%3d",chessBoardState[i][j]);
				if (chessBoardState[i][j] == black) blacknum++;
				if (chessBoardState[i][j] == white) whitenum++;
			}
			System.out.println("");
		}
		System.out.print("score= " + blacknum + " : " + whitenum);
	}
	public static void pushChessBoard(){
		//保存棋盘，因为涉及到吃子，必须保存吃子前的状态，以便恢复
		//将全局QP[i][j] 压入全局栈QP

		//first QP[i][j] 转换为25个数据向量
		ArrayList<Integer> chessBoardStatus = new ArrayList<Integer>(chessBoardNum + 1);

		//System.out.println(chessBoardStatus.size());
		for (int step = 0; step<chessBoardNum; step++){
			chessBoardStatus.add(chessBoardState[step / 5][step % 5]);
		}

		stackOfChessBoard.push(chessBoardStatus);
	}
	public static void popChessBoard()
	{

		ArrayList<Integer> chessBoardStatus = new ArrayList<Integer>(chessBoardNum + 1);

		chessBoardStatus = stackOfChessBoard.pop();
		for (int i = 0; i<chessBoardNum; i++)
		{
			chessBoardState[i / 5][i % 5] = chessBoardStatus.get(i);
		}

	}
	//估价函数
	public static int value(int num)
	{  //神经网络 25个节点---隐藏层9个节点---最后输出1个节点
		//
		final int hidenNodeNum = 9;


		int i, j;
		int var = 0;
		int [] nextState = new int[10];
		for (i = 0; i < hidenNodeNum; i++) {
			nextState[i] = 0;
			for (j = 0; j < chessBoardNum; j++) {
				nextState[i] += Human.getWij(num, chessBoardNum*i + j) * chessBoardState[j / 5][j % 5];
			}
			var += nextState[i] * Human.getWij(num, chessBoardNum*hidenNodeNum + i);
		}
		//printf("%d ", var);
		return var;
	}
	public static int countChessNum(int type)
	{
		int i;
		int totalNum = 0;
		//	//计算当前棋盘同类型数目 1--黑子  -1 白子

		for (i = 0; i<Grid.getGridNum()*Grid.getGridNum(); i++){
			if (chessBoardState[i / Grid.getGridNum()][i%Grid.getGridNum()] == type) totalNum++;

		}
		return totalNum;

	}
	public static int isWin(){
		int result = 0;		//0表示未出结果，1表示黑棋赢，2表示白棋赢
		int num = 0;		//总共的棋子数

		totalStep++;
		num = countChessNum(black);
		num += countChessNum(white);

		if (num >24) {
			if (countChessNum(black)>12) result = 1;
			else result = 2;
		}
		if (totalStep>1000){
			//针对五道棋存在，循环落子导致程序搜索陷入循环状态，采用累积步数到时间直接结束棋局
			totalStep = 0;
			result = (countChessNum(black) >= countChessNum(white)) ? 1 : 2;
			System.out.println("\nnow recursive");
		}
		if (result>0) totalStep = 0; //有胜负棋局 就清零
		return result;

	}
	public static int eatChess(int[] dir)
	{
		// 吃子，每个方向对应一个整数串 dir[0..5] 其中0 表示空 3 表示非法边界 便于统一处理
		// 其中 dir[1] 是最后落子 dir[1]=QP[i,j] 取值={ 1 ，-1};
		// 影响到 dir[2] or dir[3] 可能被吃子
		//
		if ((dir[2] == 1 || dir[2] == -1) && (dir[2] != dir[1]) && (dir[1] == dir[0]) && (dir[2] != dir[3]))
			return 2;

		if ((dir[3] == 1 || dir[3] == -1) && (dir[3] != dir[1]) && (dir[1] == dir[2]) && (dir[3] != dir[4]))
			return 3;
		return 0;

	}
	public static int[] checkMove(int i, int j, int dir)
	{
		//检测 i，j add chess ,and dir=0..4
		//0 

		int index = 0;

		switch (dir)
		{
		case 0: //right
			for (int k = 0; k<Grid.getGridNum(); k++)
			{
				index = j + k - 1;
				tempState[k] = (index >= 0 && index<Grid.getGridNum()) ? chessBoardState[i][index] : 3;
			}


			break;

		case 1: //down

			for (int k = 0; k<Grid.getGridNum(); k++)
			{
				index = i + k - 1;
				tempState[k] = (index >= 0 && index<Grid.getGridNum()) ? chessBoardState[index][j] : 3;
			}


			break;

		case 2: //to left
			for (int k = 0; k<Grid.getGridNum(); k++)
			{
				index = j + 1 - k;
				tempState[k] = (index >= 0 && index<Grid.getGridNum()) ? chessBoardState[i][index] : 3;
			}
			break;

		case 3: //to up
			for (int k = 0; k<Grid.getGridNum(); k++)
			{
				index = i + 1 - k;
				tempState[k] = (index >= 0 && index<Grid.getGridNum()) ? chessBoardState[index][j] : 3;
			}
			break;
		default:
			System.out.println("\n=== " + dir + "=====");
		}

		return tempState;
	}
	public static int move(int i, int j, int type)
	{
		// 棋盘[i,j]落子，并对4个方向进行吃子检测
		int index;
		int result = 0;

		if (chessBoardState[i][j] == 0){
			chessBoardState[i][j] = type;
			//move other can move chess 
			for (int k = 0; k<4; k++){
				index = eatChess(checkMove(i, j, k));
				//now make four direction
				if (index > 0) {
					switch (k)
					{
					case 0: //right
						chessBoardState[i][j + index - 1] = 0;
						break;
					case 1: //down
						chessBoardState[i + index - 1][j] = 0;
						break;
					case 2: //to left
						chessBoardState[i][j - index + 1] = 0;
						break;
					default:
						chessBoardState[i - index + 1][j] = 0;


					}
					result++; //返回吃子数量
				}



			}

		}
		else{
			System.out.println("pos " + i + ": " + j +
					"is occu " + chessBoardState[i][j]);
		}

		return result;

	}
	//剪枝函数
	public static int alphaBeta(Integer pre_val, int tempDepth, int max, int num)
	{
		int i, j, thisMax, nextMax;//thisMax是本层的极值，nextMax是由下一层求得的极值
		boolean iscut = false;//是否被剪枝的标志
		//达到搜索深度，停止搜索
		if (tempDepth == Human.getDepth()) {
			return value(num);
		}
		if (max == black) {
			thisMax = 100000;
		}
		else {
			thisMax = -100000;
		}

		for (i = 0; i < Grid.getGridNum() && !iscut; i++) {
			for (j = 0; j < Grid.getGridNum() && !iscut; j++) {
				if (chessBoardState[i][j] == 0) {
					//如果上一层是极大层，则这一层是极小层，玩家走棋
					if (max == black) {
						//QP[i][j] = 1;
						pushChessBoard();
						move(i, j, black);  //移动棋子，并计算吃子 
						nextMax = alphaBeta(thisMax, tempDepth + 1, white, num);
						if (nextMax < thisMax) {
							thisMax = nextMax;
						}
						if (thisMax <= pre_val) {
							iscut = true;
						}
					}
					//如果上一层是极小层，则这一层是极大层，计算机走棋
					else {
						//QP[i][j] = 2;
						pushChessBoard();
						move(i, j, white);
						nextMax = alphaBeta(thisMax, tempDepth + 1, black, num);
						if (nextMax > thisMax) {
							thisMax = nextMax;
						}
						if (thisMax >= pre_val) {
							iscut = true;
						}
					}
					popChessBoard();
					//QP[i][j] = 0;
				}
			}
		}
		//修改上一层的极值
		if (max == black) {
			if (thisMax > pre_val) {
				pre_val = thisMax;
			}
		}
		else {
			if (thisMax < pre_val) {
				pre_val = thisMax;
			}
		}
		return thisMax;
	}
	//进化算法训练权值
	public static int train()
	{
		int temp ;
		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < Back.getWeightlength(); j++) {
				//weight[i][j] = (int)(rand()%10);
				temp = -10 + (int)(20.0*rand.nextFloat() / 
						(Back.getRandmax() + 1.0));
				Human.setWij(temp, i, j);
				System.out.print(Human.getWij(i, j)+ " ");
			}
		}
		System.out.println("");
		Human.setDepth(2); //训练深度设置为 2


		initialChessBoard();


		int i, j, k, maxEstmt;
		int n; //进化次数 
		int max, max_num = 0;//max记录最大的胜利次数，max_num记录最大胜利次数所对应的个体的编号
		for (n = 0; n < Human.getGeneration(); n++)
		{
			System.out.println("\n now in " + n);
			for (i = 0; i < 10; i++)
			{
				setWinScorei(i, 0);
			}
			for (i = 0; i < 10; i++)
			{
				for (j = i + 1; j < 10; j++)
				{
					fight(i, j);
				}
			}
			for (i = 0; i < 10; i++)
			{

				System.out.println(getWinScorei(i));
			}
			//进化算法 
			//选择父代 
			for (i = 0; i < 5; i++)
			{
				max = -100;
				for (j = 0; j < 10; j++)
				{
					if (winScore[j] > max)
					{
						max = winScore[j];
						max_num = j;
					}
				}
				//printf("%d\n", max_num); 
				for (j = 0; j < Back.getWeightlength(); j++)
				{
					Human.setBestij(Human.getWij(max_num, j), i, j);
					//printf("%d",best[i][j]);
				}
				setWinScorei(max_num, -100);
			}
			//突变产生子代 
			maxEstmt = 0;
			for (i = 0; i < 5; i++)
			{
				for (j = i + 1; j < 5; j++)
				{
					for (k = 0; k < Back.getWeightlength(); k++)
					{
						Human.setWij((Human.getBestij(i, k) + Human.getBestij(j, k)) / 2
								, maxEstmt, k);
					}
					maxEstmt++;
				}
			}
		}

		//将最好的一组权值写入文件中保存起来
		File file = new File("weight.txt");
		String str = null;
		List<String> lines = null;
		Charset charset = Charset.forName("US-ASCII");
		if (!file.exists())
		{
			System.out.println("Can not open");
		}
		for (i = 0; i < Back.getWeightlength(); i++)
		{
			str = ((Integer)Human.getBestij(0, i)).toString();
			str += " ";
			lines.add(str);
			System.out.println(Human.getBestij(0, i) + " ");
		}
		try {
			Files.write(file.toPath(), lines, charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return 0;


	}
}
class Grid{
	private final static int gridNum = 5;
	public static int getGridNum() {
		return gridNum;
	}
}
