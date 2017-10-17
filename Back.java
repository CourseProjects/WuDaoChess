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
		// argv=-t  ִ����ѵ���� ����train.txt
		// argv=-weight  ����  -nextState ���� 
		//ȱʡ ����

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
		ch = 'b'; //ȱʡ�����˻����� ������
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
	private static int[][] weight = new int[10][Back.getWeightlength()];	//�洢ÿһ����Ⱥ��ÿ�������Ȩֵ
	private static int[][] best = new int[5][Back.getWeightlength()];	//��������õ�5������
	private static int depth = 8 ;	//�������
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

		//�˹��͵��Զ��ģ�����numΪ��Ӧ��Ԫ���� first=true ��ʾ����BLACK
		int firstColor, secondColor;
		if (first){
			firstColor = Chess.getBlack();
			secondColor = Chess.getWhite();
		}
		else  {
			firstColor = Chess.getWhite();
			secondColor = Chess.getBlack();
		}
		
		setDepth(5);		//�����������Ϊ5
		//����õ�һ����ѵ���Ȩֵ�ָ���w[0]
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

				//now �л�����˳��
			}
			else first = true; //��������л������巽��	


			Chess.bestMove(x, y, secondColor);
			Chess.move(x, y, secondColor);



			if (Chess.isWin() == Chess.getBlack()) return Chess.isWin();
		}
	}
}

class Chess{
	private final static int black = 1;
	private final static int white = -1;
	private static int[] winScore = new int[10];	//�����໥���ĵõ��ļƷ�
	private static int chessNum;	//���������ӵĸ���
	private static final int chessBoardNum = 25;	//�����ܵ�����
	private static int[][] chessBoardState = 
			new int [Grid.getGridNum()][Grid.getGridNum()];//����״̬
	private static int totalStep = 0;		//�ܹ��Ĳ���
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
		//���ص�ǰ���̾�����,player������ѡ��
		//��������Ϊx,y 
		int x_pos = 0, y_pos = 0;
		int var = -100000;
		int maxEstmt = -100000;
		int tempDepth = 1;
		for (int x = 0; x < Grid.getGridNum(); x++) {
			for (int y = 0; y < Grid.getGridNum(); y++) {
				//ѡȡ��������λ��
				if (chessBoardState[x][y] == 0) {


					pushChessBoard();
					int eatFlag = move(x, y, player); //������޳���
					//�������̽����һ���壬����״̬�ı��ˣ��ڸ�״̬�¼�������Ϊdep-1������״̬����ֵval
					var = alphaBeta(var, tempDepth, 1, 0);//ȱʡ�õ�һ��������ϵ��
					var = var + 1000 * eatFlag; //���ӳ��ӽ���
					if (var > maxEstmt) {    //mҪ��¼ͨ����̽��õ�����״̬��������ֵ
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
	//���ĺ���
	public static void fight(int tempState, int nextState) //a��Ӧ�������������֣�b��Ӧ��������������
	{
		int maxEstmt = -10001, var = -10000, tempDepth = 1;   //m�����������val
		int x_pos = 0, y_pos = 0;                //��¼����߲�������
		initialChessBoard();
		//cout<<"Qipan: "<<endl;
		//printChessboard();
		while (true) {
			for (int x = 0; x < Grid.getGridNum(); x++) {
				for (int y = 0; y < Grid.getGridNum(); y++) {
					if (chessBoardState[x][y] == 0) {

						//QP[x][y] = 1;
						pushChessBoard(); //��������״̬
						move(x, y, black); //�ƶ����ӣ������г��Ӽ��

						var = alphaBeta(var, tempDepth, 1, tempState);//�������̽����һ���壬����״̬�ı��ˣ��ڸ�״̬�¼�������Ϊdep-1������״̬����ֵval
						if (var > maxEstmt) {             //mҪ��¼ͨ����̽��õ�����״̬��������ֵ
							maxEstmt = var;
							x_pos = x;
							y_pos = y;
							//cout<<"The computer put the qizi at:"<<x_pos+1<<y_pos+1<<endl;
						}
						//computer1Ӯ��
						if (isWin() == 1) {
							//cout<<"The computer1 put the qizi at:"<<x+1<<y+1<<endl;
							printChessboard();
							System.out.println("The computer 1 WIN! GAME OVER."); 
							winScore[tempState] += 3;
							return;
						}

						//QP[x][y]=0;
						popChessBoard(); //�ָ�����״̬
					}
				}
			}
			//QP[x_pos][y_pos] = 1;

			//chessnum ++ ;
			chessNum = chessNum + 1 - move(x_pos, y_pos, black); //ѡ������,�����г��Ӽ��
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
						var = alphaBeta(var, tempDepth, 1, nextState);//�������̽����һ���壬����״̬�ı��ˣ��ڸ�״̬�¼�������Ϊdep-1������״̬����ֵval
						//computer2Ӯ��
						if (isWin() == 2) {
							//cout<<"The computer2 put the qizi at:"<<x+1<<y+1<<endl;
							printChessboard();
							System.out.println("The computer2 WIN! GAME OVER.");
							winScore[nextState] += 3;
							return;
						}

						if (var > maxEstmt) {    //mҪ��¼ͨ����̽��õ�����״̬��������ֵ
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
	
	//��ʼ������
	public static void initialChessBoard()
	{
		for (int i = 0; i < Grid.getGridNum(); i++) {
			for (int j = 0; j < Grid.getGridNum(); j++) {
				chessBoardState[i][j] = 0;
			}
		}
		chessNum = 0;
	}
	//��ӡ����
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
		//�������̣���Ϊ�漰�����ӣ����뱣�����ǰ��״̬���Ա�ָ�
		//��ȫ��QP[i][j] ѹ��ȫ��ջQP

		//first QP[i][j] ת��Ϊ25����������
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
	//���ۺ���
	public static int value(int num)
	{  //������ 25���ڵ�---���ز�9���ڵ�---������1���ڵ�
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
		//	//���㵱ǰ����ͬ������Ŀ 1--����  -1 ����

		for (i = 0; i<Grid.getGridNum()*Grid.getGridNum(); i++){
			if (chessBoardState[i / Grid.getGridNum()][i%Grid.getGridNum()] == type) totalNum++;

		}
		return totalNum;

	}
	public static int isWin(){
		int result = 0;		//0��ʾδ�������1��ʾ����Ӯ��2��ʾ����Ӯ
		int num = 0;		//�ܹ���������

		totalStep++;
		num = countChessNum(black);
		num += countChessNum(white);

		if (num >24) {
			if (countChessNum(black)>12) result = 1;
			else result = 2;
		}
		if (totalStep>1000){
			//����������ڣ�ѭ�����ӵ��³�����������ѭ��״̬�������ۻ�������ʱ��ֱ�ӽ������
			totalStep = 0;
			result = (countChessNum(black) >= countChessNum(white)) ? 1 : 2;
			System.out.println("\nnow recursive");
		}
		if (result>0) totalStep = 0; //��ʤ����� ������
		return result;

	}
	public static int eatChess(int[] dir)
	{
		// ���ӣ�ÿ�������Ӧһ�������� dir[0..5] ����0 ��ʾ�� 3 ��ʾ�Ƿ��߽� ����ͳһ����
		// ���� dir[1] ��������� dir[1]=QP[i,j] ȡֵ={ 1 ��-1};
		// Ӱ�쵽 dir[2] or dir[3] ���ܱ�����
		//
		if ((dir[2] == 1 || dir[2] == -1) && (dir[2] != dir[1]) && (dir[1] == dir[0]) && (dir[2] != dir[3]))
			return 2;

		if ((dir[3] == 1 || dir[3] == -1) && (dir[3] != dir[1]) && (dir[1] == dir[2]) && (dir[3] != dir[4]))
			return 3;
		return 0;

	}
	public static int[] checkMove(int i, int j, int dir)
	{
		//��� i��j add chess ,and dir=0..4
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
		// ����[i,j]���ӣ�����4��������г��Ӽ��
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
					result++; //���س�������
				}



			}

		}
		else{
			System.out.println("pos " + i + ": " + j +
					"is occu " + chessBoardState[i][j]);
		}

		return result;

	}
	//��֦����
	public static int alphaBeta(Integer pre_val, int tempDepth, int max, int num)
	{
		int i, j, thisMax, nextMax;//thisMax�Ǳ���ļ�ֵ��nextMax������һ����õļ�ֵ
		boolean iscut = false;//�Ƿ񱻼�֦�ı�־
		//�ﵽ������ȣ�ֹͣ����
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
					//�����һ���Ǽ���㣬����һ���Ǽ�С�㣬�������
					if (max == black) {
						//QP[i][j] = 1;
						pushChessBoard();
						move(i, j, black);  //�ƶ����ӣ���������� 
						nextMax = alphaBeta(thisMax, tempDepth + 1, white, num);
						if (nextMax < thisMax) {
							thisMax = nextMax;
						}
						if (thisMax <= pre_val) {
							iscut = true;
						}
					}
					//�����һ���Ǽ�С�㣬����һ���Ǽ���㣬���������
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
		//�޸���һ��ļ�ֵ
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
	//�����㷨ѵ��Ȩֵ
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
		Human.setDepth(2); //ѵ���������Ϊ 2


		initialChessBoard();


		int i, j, k, maxEstmt;
		int n; //�������� 
		int max, max_num = 0;//max��¼����ʤ��������max_num��¼���ʤ����������Ӧ�ĸ���ı��
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
			//�����㷨 
			//ѡ�񸸴� 
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
			//ͻ������Ӵ� 
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

		//����õ�һ��Ȩֵд���ļ��б�������
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
