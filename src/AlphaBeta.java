

public class AlphaBeta { //������

	public void init(){
		score = new int[50000000];
		for(int i = 0;i < 50000000;i ++){
			score[i] = -INF - 1;
		}
	}

	private boolean repeat(int[] chessboard,int m,int mm){ // ������������
		int x = m/4; //����
		int y = m%4;  //����
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

		if(x == a){ //�������ͬһ��
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
		} else if(b == y){ //�������ͬһ��
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
    		while(turn > 0){ //����ڵ�ֵ
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

	private int alphaBeta(int[] chessboard,int alpha,int beta,int color){ //���м�֦
		if(win(chessboard) == 1){
			return (color == 1) ? -INF : INF;
		}
		if(draw(chessboard) == 1){
			return (color == 1) ? INF : -INF;
		}//�ж��Ƿ������
		int[] moves = new int[16];//ѡ������ĵط�
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
				if(res >= best){ //�������ֵ
					best = res;
					if(res > alpha){ //����maxֵ
						alpha = res;
					}
					if(res >= beta){ //b��֦
						break;
					}
				}
			}
		}
		return best;
	}

	public int move(int[] chessboard){//����
		int[] cboard = new int[16];
		for(int i = 0;i < 16;i ++){
			cboard[i] = chessboard[i]; //ѡ��һ���ط�����
		}
		int alpha = -INF;//������
		int beta = INF;//������
		int move = 0;
		int[] moves = new int[16];
		int best = -INF - 1;
		int len = generateMoves(cboard,moves);
		for(int index = 0;index < len;index ++){ //������������
			if(cboard[moves[index]] == 0){
				cboard[moves[index]] = 1;
				int status = Status(cboard);
				int res = score[status];
				if(res == -INF - 1){
					 res = score[status] = - alphaBeta(cboard,-beta,-alpha,2);
				}
				cboard[moves[index]] = 0;
				if(res >= best){ //�������ֵ
					best = res;
					move = moves[index];
					if(res >= alpha){//����maxֵ
						alpha = res;
					}
					if(res >= beta){ //b��֦
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

	public int win(int[] chessboard){ //�ж���Ӯ
		int i,j;
		for(i = 0;i < 4;i ++){
			for(j = 0;j < 4 && chessboard[4*i] != 0;j ++){
				if(chessboard[4*i + j] != chessboard[4*i]){ //�ж��Ƿ���һ����ͬ
					break;
				}
			}
			if(j == 4){
				return 1;
			}
		}

		for(i = 0;i < 4;i ++){
			for(j = 0;j < 4 && chessboard[i] != 0;j ++){
				if(chessboard[4*j + i] != chessboard[i]){ //�ж��Ƿ���һ����ͬ
					break;
				}
			}
			if(j == 4){
				return 1;
			}
		}
		for(i = 0;i < 4 && chessboard[0] != 0;i ++){ //�ж϶Խ���
			if(chessboard[5*i] != chessboard[0]){
				break;
			}
		}
		if(i == 4){
			return 1;
		}

		for(i = 0;i < 4 && chessboard[3] != 0;i ++){
			if(chessboard[4*i + 3 - i] != chessboard[3]){ //�ж϶Խ���
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
