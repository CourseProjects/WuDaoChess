

public class AlphaBeta { //定义树

	public void init(){
		score = new int[50000000];
		for(int i = 0;i < 50000000;i ++){
			score[i] = -INF - 1;
		}
	}

	private boolean repeat(int[] chessboard,int m,int mm){ // 进行树的搜索
		int x = m/4; //行数
		int y = m%4;  //列数
		int a = mm/4;
		int b = mm%4;
		int i,j;
		int[][] board = new int[4][4];
		for(i = 0;i < 4;i ++){
			for(j = 0;j < 4;j ++){
				board[i][j] = chessboard[4*i + j];
			}
		}
		board[x][y] = board[a][b] = -1;

		if(x == a){ //如果下在同一列
			for(i = 0;i < 2;i ++){
				for(j = 0;j < 4;j ++){
					if(board[j][i] != board[j][3 - i]){
						break;
						}
					}
				if(j != 4){
					break;
				}
			}
			if(i == 2){
				return true;
			}
		} else if(b == y){ //如果下在同一行
			for(i = 0;i < 2;i ++){
				for(j = 0;j < 4;j ++){
					if(board[i][j] != board[3 - i][j]){
						break;
					}
				}
				if(j != 4){break;}
            }
            if(i == 2){
            	return true;
            }
       } else if(a == y && b == x){
            for(i = 0;i < 4;i ++){
            	for(j = 0;j < i;j ++){
            		if(board[i][j] != board[j][i]){
            			break;
            		}
            	}
            	if(j != i){ break;}
            }
            if(i == 4){
            	return true;
            }
    } else if(a == (3 - y) && b == (3 -x)){
    	for(i = 0;i < 4;i ++){
    		for(j = 0;j < 3 - i;j ++){
    			if(board[i][j] != board[3 - i][3 - j]){
    				break;
    			}
    		}
    		if(j != 3 - i){ break;}
    	}
    	if(i == 4){
    		return true;
    	}
    } else {
    	int turn = 0;
    	int ta = a;
    	int tb = b;
    	int t;
    	board[a][b] = board[x][y] = 0;
    	while((ta != x || tb != y) && turn < 4){
    		t = tb;
    		tb = 3 - ta;
    		ta = t;
    		turn ++;
    	}
    	if(turn != 4){
    		int[][] tmp = new int[4][4];
    		for(i = 0;i < 4;i ++){
    			for(j = 0;j < 4;j ++){
    				tmp[i][j] = board[i][j];
    			}
    		}
    		board[x][y] = -1;
    		tmp[a][b] = -1;
    		while(turn > 0){ //计算节点值
    			turn --;
    			for(i = 0;i < 2;i ++){
    				for(j = 0;j < 2;j ++){
    					ta = i;
    					tb = j;
    					t = tmp[tb][3 - ta];
    					tmp[tb][3 - ta] = tmp[ta][tb];
    					int tt = tmp[3 - ta][3 - tb];
    					tmp[3 - ta][3 - tb] = t;
    					t = tt;
    					tt = tmp[3 - tb][ta];
    					tmp[3 - tb][ta] = t;
    					tmp[ta][tb] = tt;
    					}
    				}
    			}
    		for(i = 0;i < 4;i ++){
    			for(j = 0;j < 4;j ++){
    				if(tmp[i][j] != board[i][j]){
    					break;
    					}
    				}
    			if(j != 4){break;}
    		}
    		if(i == 4){
    			return true;
    			}
    		}
    	}
		return false;
	}

	private boolean noDouble(int[] chessboard,int move,int[] moves,int cnt){
		for(int i = 0;i < cnt;i ++){
			if(repeat(chessboard,move,moves[i])){
				return false;
			}
		}
		return true;
	}

	private int generateMoves(int[] chessboard,int[] moves){
		int cnt = 0;
		for(int i = 15;i >= 0;i --){
			if(chessboard[i] == 0 && noDouble(chessboard,i,moves,cnt)){
				moves[cnt ++] = i;
			}
		}
		return cnt;
	}

	private int Status(int[] chessboard){
		int res = 0;
		int k = 1;
		for(int i = 0;i < 16;i ++){
			res = res + chessboard[i]*k;
			k *= 3;
		}
		return res;
	}

	private int alphaBeta(int[] chessboard,int alpha,int beta,int color){ //进行剪枝
		if(win(chessboard) == 1){
			return (color == 1) ? -INF : INF;
		}
		if(draw(chessboard) == 1){
			return (color == 1) ? INF : -INF;
		}//判断是否可以下
		int[] moves = new int[16];//选择下棋的地方
		int len = generateMoves(chessboard,moves);
		int best = -INF - 1;
		for(int index = 0;index < len;index ++){
			if(chessboard[moves[index]] == 0){
				chessboard[moves[index]] = color;
				int status = Status(chessboard);
				int res = score[status];
				if(res == -INF-1){
					 res = score[status] = - alphaBeta(chessboard,-beta,-alpha,color ^ 3);
				}
				chessboard[moves[index]] = 0;
				if(res >= best){ //更新最大值
					best = res;
					if(res > alpha){ //更新max值
						alpha = res;
					}
					if(res >= beta){ //b剪枝
						break;
					}
				}
			}
		}
		return best;
	}

	public int move(int[] chessboard){//下棋
		int[] cboard = new int[16];
		for(int i = 0;i < 16;i ++){
			cboard[i] = chessboard[i]; //选择一个地方下棋
		}
		int alpha = -INF;//负无穷
		int beta = INF;//正无穷
		int move = 0;
		int[] moves = new int[16];
		int best = -INF - 1;
		int len = generateMoves(cboard,moves);
		for(int index = 0;index < len;index ++){ //定义搜索层数
			if(cboard[moves[index]] == 0){
				cboard[moves[index]] = 1;
				int status = Status(cboard);
				int res = score[status];
				if(res == -INF - 1){
					 res = score[status] = - alphaBeta(cboard,-beta,-alpha,2);
				}
				cboard[moves[index]] = 0;
				if(res >= best){ //更新最大值
					best = res;
					move = moves[index];
					if(res >= alpha){//更新max值
						alpha = res;
					}
					if(res >= beta){ //b剪枝
						break;
					}
				}
			}
		}
		return move;
	}

	public int draw(int[] chessboard){
		for(int i = 0;i < 16;i ++){
			if(chessboard[i] == 0){
				return 0;
			}
		}
		return 1;
	}

	public int win(int[] chessboard){ //判断输赢
		int i,j;
		for(i = 0;i < 4;i ++){
			for(j = 0;j < 4 && chessboard[4*i] != 0;j ++){
				if(chessboard[4*i + j] != chessboard[4*i]){ //判断是否有一列相同
					break;
				}
			}
			if(j == 4){
				return 1;
			}
		}

		for(i = 0;i < 4;i ++){
			for(j = 0;j < 4 && chessboard[i] != 0;j ++){
				if(chessboard[4*j + i] != chessboard[i]){ //判断是否有一行相同
					break;
				}
			}
			if(j == 4){
				return 1;
			}
		}
		for(i = 0;i < 4 && chessboard[0] != 0;i ++){ //判断对角线
			if(chessboard[5*i] != chessboard[0]){
				break;
			}
		}
		if(i == 4){
			return 1;
		}

		for(i = 0;i < 4 && chessboard[3] != 0;i ++){
			if(chessboard[4*i + 3 - i] != chessboard[3]){ //判断对角线
				break;
			}
		}
		if(i == 4){
			return 1;
		}
		return 0;
	}
	private final static int INF = 1;
	private int[] score;
}
