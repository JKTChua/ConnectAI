/******************************************************

   Class: ConnectK.java
   purpose: Return AI moves - a row and col number. 
   Author: Jeremy Chua and Jason Rogers

*******************************************************/

import java.util.Vector;

public class connectK
{
/////////////////////////////////////////////////////////////////////////////////////////
// 
//	            Private variables definition
//
//              pB:     		used to populate variables such as rows, cols, gravity, etc
//				board:			the board layout used by the GUI has the origin located at the top left corner
//								of the board.  For example:
//              rows:			number of rows in the game board
//				cols:			number of columns in the game board
//				wins:			number of pieces required to win a game
//              cO:     		character object
//				maxTime:		max time for iterative depth search in ms
//				outOfTime:		identifies when out of time
//				start:			start time for iterative depth search
//				currentDepth:	shows current depth when doing iterative depth search
//				excludeList:	the list of moves that lead to a loss in the next turn
//				hasWin:			shows when the next move leads to a win or loss
//				xInARow,		
//				oInARow:		Point values for number of X or O in a row
//				
//
//					N columns
//
//				  0  1  2..N-1
//               +-----------+
//              0|	|  |  |	 |
//               |--+--+--+--|
//				1|	|  |  |	 |
//     M rows    |--+--+--+--|
//				2|  |  |  |	 |
//             ..|--+--+--+--|
//			  M-1|  |  |  |  |
//               +-----------+
//

    private pBoard pB;
    private Vector<CharObj>[] board;
    private int rows;
    private int cols;
    private int wins;
    private CharObj cO;
    private int maxTime = 5000;
    private boolean outOfTime = false;
    private long start;
//    private int depthTotal = 0;
    private int currentDepth = 0;
    private int[][] excludeList;
    private boolean hasWin = false;
    private int[] xInARow, oInARow;
//    private int totalCheckedNodes;
    
    
    connectK(pBoard b) //constructor
    {
        pB = b;        // use all of these public interfaces to get important
                       // variables: rows, cols, isGon, mark, board, etc.
        rows        = b.getRows();
        cols        = b.getCols();
        wins        = b.getWins();
        InitBoard();
        xInARow = new int[b.getWins()];
        oInARow = new int[b.getWins()];
        
        for(int k = 0; k < b.getWins(); k++)
        {
        	oInARow[k] = (int)Math.pow(10, k);
        	xInARow[k] = -(int)(Math.pow(10, k));
        }
        
    }
    
    // chooses the best AI move with limited information
    public void nextMove()
    {
        copyBoard();
        
    	start = System.nanoTime() / 1000000; 
    	excludeList = new int[cols][rows];
    	int highestH = 0;
    	int temp;
    	boolean set = false;
    	outOfTime = false;
    	int bestRow = 0, bestCol = 0, curRow = 0, curCol = 0;
//    	int finalHigh = 0;
//    	totalCheckedNodes = 0;
    	hasWin = false;
    	
        if(pB.getG())	//gravity is on
        {
        	levelloop:
        	for (int levels = 0; levels < rows * cols && !outOfTime; levels++)
        	{
        		set = false;
        		currentDepth = levels;
        		colloop:
        		for(int c = 0; c < cols; c++)
	        	{
        			if ((System.nanoTime() / 1000000) - start > maxTime)
        			{
        				outOfTime = true;
        				break;
        			}
	        		int r = rows - 1;
	        		cO = (CharObj)board[r--].elementAt(c);
	        		while(r >= 0 && (cO.mark != constant.BLANK))
	        			cO = (CharObj)board[r--].elementAt(c);
	        		if (excludeList[c][r + 1] == 1)
        				continue colloop;
	        		if (cO.mark == constant.BLANK)
	        		{
	        			board[++r].setElementAt(new CharObj(constant.O), c);
	        			temp = nextMoveHelperGravity(board, new CharObj(constant.X), levels, -2000000000, 2000000000); //must be ODD?
	        			board[r].setElementAt(new CharObj(), c);
	        			if (temp == 2000000000 || temp == -2000000000)
	        				break;
	        			if (levels == 0 && hasWin)
	            		{
//	            			System.out.println("Win move r:" + r + " c:" + c);
	            			pB.setRow(r);
	            			pB.setCol(c);
	            			return;
	            		}
	        			if (levels == 1 && hasWin)
		        		{
//		        			System.out.println("Excluded: c:" + c + ", r:" + r + ", " + levels);
		        			excludeList[c][r] = 1;
		        			hasWin = false;
		        			continue colloop;
		        		}
	        			if (outOfTime)
	        				break levelloop;
	        			if(!set)
	        			{
	        				highestH = temp; // initialize highestH to the first possible move
	        				set = true;
	        			}
	        			if (temp >= highestH)
	        			{
	        				highestH = temp;
	        				curRow = r;
	        				curCol = c;
//	        				System.out.println("r:" +r + " c:" + c + " " + levels + " " + highestH);
	        			}
	        		}
	        	}
        		if (!outOfTime)
        		{
        			bestRow = curRow;
        			bestCol = curCol;
//        			depthTotal = levels;
//        			finalHigh = highestH;
        		}
	        }
//        	System.out.println("Final r:" + bestRow + " c:" + bestCol + " " + depthTotal + " " + finalHigh + " Total checked:" + totalCheckedNodes);
        	pB.setRow(bestRow);
			pB.setCol(bestCol);
        }
        else	//gravity is off
        {
        	levelloop:
        	for (int levels = 0; levels < rows * cols && !outOfTime; levels++)
        	{
        		set = false;
        		currentDepth = levels;
        		for(int c = 0; c < cols && !outOfTime; c++)
	            {
        			rowloop:
	        		for(int r = 0; r < rows; r++)
	        		{
	        			if ((System.nanoTime() / 1000000) - start > maxTime)
	        			{
	        				outOfTime = true;
	        				break;
	        			}
	        			cO = (CharObj)board[r].elementAt(c);
	        			if (cO.mark == constant.BLANK)
	        			{
	        				board[r].setElementAt(new CharObj(constant.O), c);
	        				temp = nextMoveHelper(board, new CharObj(constant.X), levels, -2000000000, 2000000000);
	        				board[r].setElementAt(new CharObj(), c);
	        				
	        				if (temp == 2000000000 || temp == -2000000000)
		        				break;
		        			if (levels == 0 && hasWin)
		            		{
		            			System.out.println("Win move r:" + r + " c:" + c);
		            			pB.setRow(r);
		            			pB.setCol(c);
		            			return;
		            		}
		        			if (levels == 1 && hasWin)
			        		{
//			        			System.out.println("Excluded: c:" + c + ", r:" + r + ", " + levels);
			        			excludeList[c][r] = 1;
			        			hasWin = false;
			        			continue rowloop;
			        		}
		        			if (outOfTime)
		        				break levelloop;
	        				
	            			if(!set)
	            			{
	            				highestH = temp; // initialize highestH to the first possible move
	            				set = true;
	            			}
	            			if (temp >= highestH)
	            			{
	            				highestH = temp;
		        				curRow = r;
		        				curCol = c;
//		        				System.out.println("r:" +r + " c:" + c + " level:" + levels + " heuristic:" + highestH);
	            			}
	        			}    			
	        		}
	            }
        		if (!outOfTime)
        		{
        			bestRow = curRow;
        			bestCol = curCol;
//        			depthTotal = levels;
//        			finalHigh = highestH;
        		}
	        }
//	        System.out.println("Final r:" + bestRow + " c:" + bestCol + " " + depthTotal + " " + finalHigh + " Total checked:" + totalCheckedNodes);
	    	pB.setRow(bestRow);
			pB.setCol(bestCol);
        }
    }
    
    // recursive alpha-beta evaluation function
    private int nextMoveHelper(Vector<CharObj>[] board, CharObj m, int depth, int min, int max)
    {
    	
    	if(depth <= 0)
    		return heuristic(board);
    	if(m.mark == constant.O)
    	{
    		int heuristic = min;
    		for(int c = 0; c < cols; c++)
    		{
    			rowloop:
    			for(int r = 0; r < rows; r++)
    			{
	    			if ((System.nanoTime() / 1000000) - start > maxTime)
	    			{
	    				outOfTime = true;
	    				return 0;
	    			}
	            	cO = (CharObj)board[r].elementAt(c);
	            	if (excludeList[c][r] == 1)
	        			continue rowloop;
	            	if(cO.mark == constant.BLANK)
	            	{
		            	board[r].setElementAt(m, c);
		            	int temp = nextMoveHelper(board, new CharObj(constant.X), depth-1, heuristic, max);
		            	board[r].setElementAt(new CharObj(), c);
		            	if(temp > heuristic)
		            		heuristic = temp;
		            	if(heuristic > max)
		            		return max;
	            	}
    			}
    		}
    		return heuristic;
    	}
    	else
    	{
    		int heuristic = max;
    		for(int c = 0; c < cols; c++)
    		{
    			for (int r = 0; r < rows; r++)
    			{
	    			if ((System.nanoTime() / 1000000) - start > maxTime)
	    			{
	    				outOfTime = true;
	    				return 0;
	    			}
	            	cO = (CharObj)board[r].elementAt(c);
	            	if(cO.mark == constant.BLANK)
	            	{
		            	board[r].setElementAt(m, c);
		            	int temp = nextMoveHelper(board, new CharObj(constant.O), depth-1, min, heuristic);
		            	board[r].setElementAt(new CharObj(), c);
		            	if(temp < heuristic)
		            		heuristic = temp;
		            	if(heuristic < min)
		            		return min;
	            	}
    			}
    		}
    		return heuristic;
    	}
    }
    
    // recursive alpha-beta evaluation function with gravity
    private int nextMoveHelperGravity(Vector<CharObj>[] board, CharObj m, int depth, int min, int max)
    {
    	if(depth <= 0)
    		return heuristic(board);
    	if(m.mark == constant.O)
    	{
    		int heuristic = min;
    		
    		columnloop:
    		for(int c = 0; c < cols; c++)
    		{
    			if ((System.nanoTime() / 1000000) - start > maxTime)
    			{
    				outOfTime = true;
    				return 0;
    			}
    			int r = rows - 1;
            	cO = (CharObj)board[r--].elementAt(c);
            	while(r >= 0 && (cO.mark != constant.BLANK))
            		cO = (CharObj)board[r--].elementAt(c);
            	if (excludeList[c][r + 1] == 1)
        			continue columnloop;
            	if(cO.mark == constant.BLANK)
            	{
	            	board[++r].setElementAt(m, c);
	            	int temp = nextMoveHelperGravity(board, new CharObj(constant.X), depth-1, heuristic, max);
	            	board[r].setElementAt(new CharObj(), c);
	            	if(temp > heuristic)
	            		heuristic = temp;
	            	if(heuristic > max)
	            		return max;
            	}
    		}
    		return heuristic;
    	}
    	else
    	{
    		int heuristic = max;
    		for(int c = 0; c < cols; c++)
    		{
    			if ((System.nanoTime() / 1000000) - start > maxTime)
    			{
    				outOfTime = true;
    				return 0;
    			}
    			int r = rows - 1;
            	cO = (CharObj)board[r--].elementAt(c);
            	while(r >= 0 && (cO.mark != constant.BLANK))
            		cO = (CharObj)board[r--].elementAt(c);
            	if(cO.mark == constant.BLANK)
            	{
	            	board[++r].setElementAt(m, c);
	            	int temp = nextMoveHelperGravity(board, new CharObj(constant.O), depth-1, min, heuristic);
	            	board[r].setElementAt(new CharObj(), c);
	            	if(temp < heuristic)
	            		heuristic = temp;
	            	if(heuristic < min)
	            		return min;
            	}
    		}
    		return heuristic;
    	}
    }
    
    // checks heuristic value for configuration of Vector<CharObj>[] board
    private int heuristic(Vector<CharObj>[] board)
    {
//    	totalCheckedNodes++;
    	return horizontalHeuristic(board) + 
		   verticalHeuristic(board) + 
		   diagonalHeuristic(board);
    }
    
    // checks heuristic value for configuration of Vector<CharObj>[] board
    private int lineHeuristic(Vector<CharObj>[] board, int x, int y, int xStep, int yStep)
    {
    	int size;
    	int lastSeenX, lastSeenY; //position of last seen element
    	int currentBlockTotal, blockSize = 0;
    	char currentBlock;
    	int heuristic = 0;
    	
    	size = 1;
    	lastSeenX = x - xStep;
		lastSeenY = y - yStep;
		currentBlockTotal = 0;
		currentBlock = board[y].elementAt(x).mark;
		if(currentBlock != constant.BLANK)
		{
			currentBlockTotal = 1;
			lastSeenX = x;
			lastSeenY = y;
		}
    	
    	for(x += xStep, y += yStep; x < cols && x >= 0 && y < rows; x += xStep, y += yStep)
    	{
    		cO = board[y].elementAt(x);
			if(cO.mark != constant.BLANK)
			{
				if(currentBlock != cO.mark)
				{
					if(currentBlock != constant.BLANK)
					{
						if (xStep < 0)
							size = lastSeenX - x - xStep; //subtract by one because size++ later
						else if (xStep > 0)
							size = x - lastSeenX - xStep;
						else
							size = y - lastSeenY - yStep;
					}
					currentBlock = cO.mark;
					currentBlockTotal = 1;
					blockSize = 1;
				}
				else
				{
					currentBlockTotal++;
					blockSize++;
				}
				lastSeenX = x;
				lastSeenY = y;
			}
			else
				blockSize++;
			size++;
			
			if(size >= wins && blockSize >= wins && board[y - wins * yStep].elementAt(x - wins * xStep).mark == currentBlock && currentBlockTotal > 0)
				currentBlockTotal--;
			
			if(size >= wins)
			{
				if(lastSeenX <= x - wins * xStep && lastSeenY <= y - wins * yStep) //if lastSeen is out of the range, currentBlock is BLANK
					currentBlock = constant.BLANK;
				if (currentBlock != constant.BLANK)
				{
					for(int z = 1; z <= wins; z++)
						if(currentBlockTotal >= z)
							heuristic += currentBlock == constant.O ? oInARow[z-1] : xInARow[z-1];
					if (currentBlockTotal >= wins)
					{
						if (currentDepth <= 1)
						{
//							System.out.println("Win detected at level: " + currentDepth + ", c:" + x + ", r:" + y);
							hasWin = true;
						}
					}
				}
			}
    	}
    	return heuristic;
    }
    
    // Returns the horizontal heuristic evaluation given the board
    private int horizontalHeuristic(Vector<CharObj>[] board)
    {
    	int heuristic = 0;
    	
    	for(int y = 0; y < rows; y++)
    	{
    		heuristic += lineHeuristic(board, 0, y, 1, 0);
    	}
    	return heuristic;
    }
    
    // Returns the vertical heuristic evaluation given the board
    private int verticalHeuristic(Vector<CharObj>[] board)
    {
    	int heuristic = 0;
    	
    	for(int x = 0; x < cols; x++)
    	{
    		heuristic += lineHeuristic(board, x, 0, 0, 1);
    	}
    	return heuristic;
    }
    
    // Returns the combined diagonal heuristic evaluations given the board
    private int diagonalHeuristic(Vector<CharObj>[] board)
    {
    	int heuristic = 0;
    	for (int x = 0; x <= cols - wins; x++)
    		heuristic += rightDiagonalHeuristic(board, x, 0);
    	for (int x = wins; x <= cols; x++)
    		heuristic += leftDiagonalHeuristic(board, x - 1, 0);
    	for (int y = 1; y <= rows - wins; y++)
    	{
    		heuristic += rightDiagonalHeuristic(board, 0, y);
    		heuristic += leftDiagonalHeuristic(board, cols - 1, y);
    	}
    	return heuristic;
    }
    
    // Returns the heuristic evaluation from going down right given the board
    private int rightDiagonalHeuristic(Vector<CharObj>[] board, int x, int y)
    {
    	return lineHeuristic(board, x, y, 1, 1);
    }
    
    //Returns the heuristic evaluation from going down left given the board
    private int leftDiagonalHeuristic(Vector<CharObj>[] board, int x, int y)
    {
    	return lineHeuristic(board, x, y, -1, 1);
    }
    
    /*
	    method: InitBoard
	    purpose: Create a board array and set all elements to Blank:" "
	    visibility: called ONLY by other methods in this class
	    parameters: NONE
	    return NONE
	*/
	private void InitBoard()
    {
        board = new Vector[rows];
        for(int j=0; j< rows; j++)
            board[j] = new Vector<CharObj>(cols);
        
        for(int j=0; j<rows; j++)
            for(int i=0; i<cols; i++)
                board[j].addElement(new CharObj());
    }
    
    /*
	    method: copyBoard
	    purpose: Make a copy of board variable in pBoard class to variable board of this class
	    visibility: called ONLY by other methods in this class
	    parameters: NONE
	    return NONE
	*/
    private void copyBoard()
    {
        Vector<CharObj>[] temp = pB.getBoard();
        
        for(int j=0; j<rows; j++)
            for(int i=0; i<cols; i++)
                board[j].setElementAt(temp[j].elementAt(i), i);
    }    
}