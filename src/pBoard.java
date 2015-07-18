/**********************************************************************

   Class: pBoard.java
   purpose: Take in human moves and call AI module to generate computer
            moves.  Draw connectK board, human and computer moves.
   Author: Thuan Truong
   E-mail: tim@drimgmt.com

***********************************************************************/
import java.awt.*;
import java.awt.image.*;
import java.beans.*;
import java.util.Vector;
import java.lang.*;
import java.awt.Cursor;
import com.sun.java.swing.*;
import javax.swing.*;

public class pBoard extends JPanel
{
    private int rows;       // number of board rows
    private int cols;       // number of board cols
    private char pMark;     // mark played by AI player for board evaluation
    private int wins;       // Number of pieces required to win
    private boolean isGon;  // Gravity
	private Image human;    // human image
	private Image computer; // computer image
	private int row;        // AI row move
	private int col;        // AI col move
	private Vector<CharObj>[] board; // board is index by [row][column]
	private long endTime;   // hold end time
	private connectView cV; // a copy of connectView object used for repainting purpose
	private long humanTime; // store human time in milliseconds
	private long compTime;  // store computer time in milliseconds
	private int gameStatus; // indicate a status of a game.
	private int numOccupied;
	private Rectangle rS;
	private Rectangle goRs;
	private CharObj cO;
	private boolean gameOver;
	private connectK cK;
	private boolean first;
	
	public pBoard() // constructor
	{
		//{{INIT_CONTROLS
		Insets ins = getInsets();
		setSize(ins.left + ins.right + 430,ins.top + ins.bottom + 270);
		row = -1;
		col = -1;
		humanTime = 0;
		compTime  = 0;
		numOccupied = 0;
		gameOver = false;
		first = true;
		rS = new Rectangle(0,0,566,346);
		goRs = new Rectangle(0,0,544,40);
        //{{ Get Images
		human = Toolkit.getDefaultToolkit().getImage("res/human.gif");
		computer = Toolkit.getDefaultToolkit().getImage("res/computer.gif");
        //}}
		//{{REGISTER_LISTENERS
		SymMouse aSymMouse = new SymMouse();
		this.addMouseListener(aSymMouse);
		//}}
	}

	/*
	    method: myDrawLine
	    purpose: to draw a vertical or horizontal line.
	    visiblity: called ONLY by other methods in this class
	    paramters: 6
	        Graphics g: provided graphic drawing tools by Java.
	        int l: left position of a line.
	        int t: top position of a line.
	        int r: right position of a line.
	        int b: bottom position of a line.
	        boolean isV: if isV=true -> draw a vertical line, else horizonal line
	    return NONE.
	*/
	private void myDrawLine(Graphics g,int l,int t,int r,int b,boolean isV)
	{
	    int size = (rows > 10 && cols > 10)?1:5; //Width of a line
	    if(isV)
	    {
	        for(int i = 0; i < size; i++)
	        {
	            g.drawLine(l+i, t, r+i, b);
	        }
	    }
	    else
	    {
	        for(int i = 0; i < size; i++)
	        {
	            g.drawLine(l, t+i, r, b+i);
	        }
	    }
	    
	}
	
	/*
	    method: drawMoves
	    purpose: Draw a human or computer piece on connectK board.
	    visiblity: called ONLY by other methods in this class
	    paramters: 1
	        Graphics g: provided graphic drawing tools by Java.
	    return NONE.
	*/
	private void drawMoves(Graphics g)
    {
         
        // draw pieces
        for (int q=0; q<cols; q++)  //for each column loop
	    {
		    for (int p=0; p<rows; p++)  //for each row loop
		    {
		       cO = (CharObj)board[p].elementAt(q); // get a character at a specified 
		                                           // square (e.g. X, O, or blank)
		       if(cO.mark==constant.X)//if mark = X, draw human image
		       {
		            g.drawImage(human, ((3*q+1)*rS.width/(3*cols))+1-5, ((3*p+1)*rS.height/(3*rows))+1-5, this);          
		       }
		       else if(cO.mark==constant.O)//if mark = O, draw computer image
		       {
		            g.drawImage(computer, ((3*q+1)*rS.width/(3*cols))+1-5, ((3*p+1)*rS.height/(3*rows))+1-5, this);
		       }
		       else
		       ; // if mark = blank, do nothing
		    }
        }
    }

	/*
	    method: tie
	    purpose: Check for tie game.
	    visiblity: called ONLY by other methods in this class
	    paramters: NONE
	    return integer value 0 ->tie, -1 not.
	*/
    private int tie()
    {
	    return (numOccupied == (rows * cols))?constant.TIE:constant.CONTINUE;
    }
    
	/*
	    method: drawGrid
	    purpose: to draw a connectK board.
	    visiblity: called ONLY by other methods in this class
	    paramters: 1
	        Graphics g: provided graphic drawing tools by java.
	    return NONE.
	*/
    private void drawGrid(Graphics g)
    {
        int temp;
        //draw vertical lines
        g.setColor(Color.black);
        for(int j= 0; j < cols; j++)
        {
            temp = j*rS.width/cols+1;
            myDrawLine(g,temp,1,temp,rS.height,true);
        }
        temp = (cols*rS.width/cols+1)-(cols>10?2:6);
        myDrawLine(g,temp,1,temp,rS.height,true);
     
        //draw horizontal lines
        for(int j= 0; j < rows; j++)
        {
            temp = j*rS.height/rows+1;
            myDrawLine(g,1,temp,rS.width,temp,false);
        }
        temp = (rows*rS.height/rows+1)-(cols>10?2:6);
        myDrawLine(g,1,temp,rS.width,temp,false);
    }
    

	/*
	    method: convertTime
	    purpose: Convert a time in millisecond to hh:mm:ss format
	    visiblity: called ONLY by other methods in this class
	    paramters: 1
	        long number: a number in millisecond(s).
	    return a string with hh:mm:ss format.
	    Note: this is not a good function to convert time.
	          Anyone know how to convert time in Java?
	*/    
    private String convertTime(long number)
    {
        long hrs = 0;
        long mins = 0;
        long secs = 0;
        String sHr, sMin, sSec, lcReturn;
        
        lcReturn = "00:00:00";
        
        if(number > 0)
        {
            secs = number / 1000;
            if(secs > 60)
            {
                mins = secs / 60;
                secs  = secs % 60;
            }
        
            if(mins > 60)
            {
                hrs = mins / 60;
                mins = mins % 60;
            }
        
            // Convert to String Cannot find better a solution, might fix later
            sSec = "" + secs;
            sMin = "" + mins;
            sHr  = "" + hrs;
        
            sSec = (sSec.length()==1)?("0"+sSec):sSec;
            sMin = (sMin.length()==1)?("0"+sMin):sMin;
            sHr = (sHr.length()==1)?("0"+sHr):sHr;
            lcReturn = sHr+":"+sMin+":"+sSec;
        }
        return lcReturn;
    }
    
    //public interfaces
    
	/*
	    method: paint
	    purpose: To draw connectK board and pieces.
	    visiblity: Can be referenced through pBoard object.  However, call repaint() instead
	               so that the Java make schedule to redraw.  This will reduce
	               screen flickering.
	    paramters: 1
	        Graphics g: provided graphic drawing tools by Java.
	    return NONE
	*/        
    public void paint(Graphics g)
    {
        if(first) //paint the first time so that images available to graphic g
        {
            Graphics cg = g.create(0,0,0,0);
	        cg.drawImage(human, 0, 0, this);
	        cg.drawImage(computer, 0, 0, this);
	        first = false;
	        cg.dispose();
	    }
        super.paint(g);
        drawGrid(g);  
        drawMoves(g);
    }
    
    /*
	    method: point2int
	    purpose: Convert a mouse click position on a connectK board to
	             an row and col of the board array variable.
	    visiblity: called ONLY by other methods in this class
	    paramters: 1
	            Point p: a mouse click position.
	    return NONE
	*/  
    private void point2int(Point p)
    {

        if (p.x != 0 || p.y != 0) // check for valid mouse click
		{
			for (int r=1; r<=rows; r++)
			{
				if ((double)(p.y)/(double)rS.height  <= (double)r/(double)rows)
				{
					row = r-1;
					break;
				}
			}
			for (int c=1; c<=cols; c++)
			{
				if ((double)(p.x)/(double)rS.width <= (double)c/(double)cols)
				{
					col = c-1;
					break;
				}
			}
		}
    }
    /*
	    method: InitBoard
	    purpose: Create a board array and set all elements to Blank:" "
	    visiblity: called ONLY by other methods in this class
	    paramters: NONE
	    return NONE
	*/  
    private void InitBoard()
    {
        board = new Vector[rows];
        for(int j=0; j< rows; j++)
        {
            board[j] = new Vector<CharObj>(cols);
        }
        
        for(int j=0; j<rows; j++)
        {
            for(int i=0; i<cols; i++)
            {
                board[j].addElement(new CharObj());
            }
        }
    }
    
   /*
	    method: evaluate
	    purpose: Determine the state of a game - e.g. X or O win or tie or nobody win yet.
	    visiblity: called ONLY by other methods in this class
	    paramters: NONE
	    return integer value indicate a game status
	*/    
   
    private int evaluate()
    {   
	    int gs = constant.CONTINUE;
	    int numX = 0;
	    int numO = 0;
	    int m = 0;
	    int n = 0;
	    int i = 0;
	    int j = 0;


	    /* Check all horizontal groups of K */
	    /*
            X X X
        */
	    for ( m = 0; m < rows; m++ )
	    {
		    for ( n = 0; n < cols-wins+1; n++, numX = 0, numO = 0 )
		    {
			    for ( j = n; j < wins + n && j < cols; j++ )
			    {
                    cO = (CharObj)board[m].elementAt(j);
                    switch ( cO.mark )
			        {
     			        case constant.X:
						    numX++;
					        break;
				        case constant.O:
						    numO++;
					        break;
				        default:
					        break;
			        }
                }

                if ( numX == wins )
                    return constant.HUMANWIN ;
                else if ( numO == wins )
                    return constant.COMPUTERWIN;
                else 
                    gs = constant.CONTINUE;
		    }
	    }

	    /* Check all vertical groups of K */
	    /*
	            X
	            X
	            X
	    */
	    for ( n = 0; n < cols; n++ )
	    {
		    for ( m = 0; m < rows-wins+1; m++, numX = 0, numO = 0 )
		    {
			    for ( i = m; i < wins + m && i < rows; i++ )
			    {
	                cO = (CharObj)board[i].elementAt(n);
                    switch ( cO.mark )
			        {
     			        case constant.X:
						    numX++;
					        break;
				        case constant.O:
						    numO++;
					        break;
				        default:
					        break;
			        }
                }

                if ( numX == wins )
                    return constant.HUMANWIN ;
                else if ( numO == wins )
                    return constant.COMPUTERWIN;
                else 
                    gs = constant.CONTINUE;
	        }
	    }
	 
	    /* Check each all forward diagonal groups of K */
	    /*
		        X
		      X
	        X
	    */
	    for ( n = 0; n < cols-wins+1; n++ )
	    {
		    for ( m = wins-1; m < rows; m++, numX = 0, numO = 0 ) 
		    {
			    for ( i = m, j = n; j < wins + n && i >=0 && j < cols; i--, j++ )
			    {
                    cO = (CharObj)board[i].elementAt(j);
                    switch ( cO.mark )
			        {
     			        case constant.X:
						    numX++;
					        break;
				        case constant.O:
						    numO++;
					        break;
				        default:
					        break;
			        }
                }

                if ( numX == wins )
                    return constant.HUMANWIN ;
                else if ( numO == wins )
                    return constant.COMPUTERWIN;
                else 
                    gs = constant.CONTINUE;
            }
	    }
	    /* Check all backward  diagonal groups of K */
	    /*
	        X
		      X
		        X
	    */
	    for ( n = 0; n < cols-wins+1; n++ )
    	{
		    for ( m = 0; m < rows-wins+1; m++, numX = 0, numO = 0 ) 
		    {
			    for ( i = m, j = n; i < wins + m && i < rows && j < cols; i++, j++ )
			    {
                    cO = (CharObj)board[i].elementAt(j);
                    switch ( cO.mark )
			        {
     			        case constant.X:
						    numX++;
					        break;
				        case constant.O:
						    numO++;
					        break;
				        default:
					        break;
			        }
                }
                if ( numX == wins )
                    return constant.HUMANWIN ;
                else if ( numO == wins )
                    return constant.COMPUTERWIN;
                else 
                    gs = constant.CONTINUE;
		    }
	    }
        if(gs == constant.CONTINUE)
            gs = tie();
	    return gs;
    }

   	/*
	    method: setXY
	    purpose: Determine the row and col of an AI move or human move.
	    visiblity: Can be referenced through pBoard object.
	    paramters: 1
	        Point p: A position of a mouse click on a connectK board.
	    return NONE
	*/    
    public void setXY(Point p)
    {
        String lcStr = "";
        point2int(p); // convert to a row and col of a connectK board
        cO = (CharObj)board[row].elementAt(col);
        if(cO.mark == constant.BLANK)
        {
            // calculate time for human move and display it.
            long diff = (endTime>0)?(System.currentTimeMillis()-endTime):0;
            humanTime += diff;
            cV.setHumanTime(convertTime(humanTime));

            // Mark a human move in the board array
            if(isGon)
            {
                for(int i=(rows-1);i>=0;i--)
                {
                    cO = (CharObj)board[i].elementAt(col);
                    if(cO.mark==constant.BLANK)
                    {
                        board[i].setElementAt(new CharObj(pMark),col);
                        break;
                    }
                }
            }
            else
                board[row].setElementAt(new CharObj(pMark),col);
            numOccupied++;            
            
            cV.humanTime.repaint();
            this.paintImmediately(rS);
            gameStatus = evaluate();
            switch(gameStatus)
            {
                case constant.HUMANWIN:
                    gameOver = true;
                    lcStr = "The winner is player 1";
                    break;
                case constant.COMPUTERWIN:
                    gameOver = true;
                    lcStr = "The winner is player 2";
                    break;
                case constant.TIE:
                    gameOver = true;
                    lcStr = "It's a tie";
                    break;
                default:
                    break;
            }

            if(gameStatus ==constant.CONTINUE)
            {
                // switch turn
                if(pMark==constant.X)
                    pMark = constant.O;
                else if(pMark == constant.O)
                    pMark = constant.X;
                else
                    ;
                cV.jPgo.paintImmediately(goRs);
                //Get time for computer move.
                long Start = System.currentTimeMillis();
                //call AI to get comp move
                cK.nextMove();
                endTime    = System.currentTimeMillis();   

                // Mark a computer move in the board array
                if(isGon)
                {
                    for(int i=(rows-1);i>=0;i--)
                    {
                        cO = (CharObj)board[i].elementAt(col);
                        if(cO.mark==constant.BLANK)
                        {
                            board[i].setElementAt(new CharObj(pMark),col);
                            break;
                        }
                    }
                }
                else
                    board[row].setElementAt(new CharObj(pMark),col);
                numOccupied++;
                this.paintImmediately(rS);
                gameStatus = evaluate();
                switch(gameStatus)
                {
                    case constant.HUMANWIN:
                        gameOver = true;
                        lcStr = "The winner is player 1";
                        break;
                    case constant.COMPUTERWIN:
                        gameOver = true;
                        lcStr = "The winner is player 2";
                        break;
                    case constant.TIE:
                        gameOver = true;
                        lcStr = "It's a tie";
                        break;
                    default:
                        break;
                }
                
                // switch turn
                if(pMark==constant.X)
                    pMark = constant.O;
                else if(pMark == constant.O)
                    pMark = constant.X;
                else
                    ;
                    
                cV.jPgo.paintImmediately(goRs);
                // calculate time for computer move and display it.
                compTime += (endTime-Start);
                cV.setComputerTime(convertTime(compTime));
                cV.computerTime.repaint();
            }
            if(gameOver)
                JOptionPane.showMessageDialog(this,lcStr,"Game Over" ,2);
        }
    }

    /*
	    method: setVars
	    purpose: set required variables every time a user reset or start a new game
	    visiblity: Can be referenced through pBoard object.
	    paramters: 5
	        int r: connectK board row size
	        int c: connectK board col size
	        int w: number of pieces required to win
	        char mp: mark played by computer or human
	        boolean g: gravity
	    return NONE
	*/  
    public void setVars(connectView v, Options o)
    {
        rows = o.getRows();
        cols = o.getCols();
        isGon = o.getG();
        pMark = o.getPlayer();
        wins = o.getWins();
        cV   = v;
        cK   = new connectK(this);
        gameOver = false;
        InitBoard(); // create board array variable
        this.paintImmediately(rS);
        cV.jPgo.paintImmediately(goRs);
        cV.setVisible(true);
        if(row ==-1 && col ==-1 && pMark==constant.O)// computer move first
        {
            long Start = System.currentTimeMillis();
            
            //call AI to get comp move
            cK.nextMove();

            endTime = System.currentTimeMillis();
            // calculate time for computer move and display it.
            compTime += endTime-Start;
            if(compTime==0)
                cV.setComputerTime("00:00:00");
            else
                cV.setComputerTime(convertTime(compTime));
            // mark a move in the board array variable
            if(isGon)
            {
                for(int i=(rows-1);i>=0;i--)
                {
                    cO = (CharObj)board[i].elementAt(col);
                    if(cO.mark==constant.BLANK)
                    {
                        board[i].setElementAt(new CharObj(pMark),col);
                        break;
                    }
                }
            }
            else
                board[row].setElementAt(new CharObj(pMark),col);
            numOccupied++;
            pMark = constant.X; // switch turn.
            cV.jPgo.paintImmediately(goRs);            
            this.repaint();
         }
    }
    
    /*
	    method: reset
	    purpose: set required variables every time a user reset or start a new game
	    visiblity: Can be referenced through pBoard object.
	    paramters: 5
	        int r: connectK board row size
	        int c: connectK board col size
	        int w: number of pieces required to win
	        char mp: mark played by computer or human
	        boolean g: gravity
	    return NONE
	*/  
    public void reset(char player, boolean newGame)
    {
        pMark = player;
        cV.jPgo.paintImmediately(goRs);
        for(int i=0; i < rows; i++)
            for(int j=0; j < cols; j++)        
                board[i].setElementAt(new CharObj(), j);
        humanTime = 0;
        compTime  = 0;
        endTime   = 0;
        numOccupied = 0;
        gameOver = false;
        row = -1;
        col = -1;
        cV.setComputerTime("00:00:00");
        cV.setHumanTime("00:00:00");
        this.paintImmediately(rS);
        if(pMark==constant.O && !newGame)
        {
            long Start = System.currentTimeMillis();
            
            //call AI to get comp move
            cK.nextMove();            

            endTime = System.currentTimeMillis();
            compTime += endTime-Start;
            if(compTime==0)
                cV.setComputerTime("00:00:00");
            else
                cV.setComputerTime(convertTime(compTime));
                
            if(isGon)
            {
                for(int i=(rows-1);i>=0;i--)
                {
                    cO = (CharObj)board[i].elementAt(col);
                    if(cO.mark==constant.BLANK)
                    {
                        board[i].setElementAt(new CharObj(pMark),col);
                        break;
                    }
                }
            }
            else
                board[row].setElementAt(new CharObj(pMark),col);
            numOccupied++;
            pMark = constant.X;
            cV.jPgo.paintImmediately(goRs);
            this.repaint();
        }
    }
    // Class interfaces: return class private variables
    public int getRows() {return rows;}
    public int getCols() {return cols;}
    public boolean getG() {return isGon;}
    public int getWins() {return wins;}
    public char getPlayer() {return pMark;}
    public void setPlayer(char c) {pMark = c;}
    public void setRow(int r) {row = r;}
    public void setCol(int c) {col = c;}
    public Vector<CharObj>[] getBoard() {return board;}
    public int getNumOccupied() {return numOccupied;}

    // SymMouse class used to trap a user's mouse interaction on a connectK board
    // to get his/her move.
	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseReleased(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == pBoard.this)
				pBoard_MouseReleased(event);
		}
	}

	void pBoard_MouseReleased(java.awt.event.MouseEvent event)
	{
	    if(!gameOver)
	    {
		    point2int(event.getPoint());
            if(row >=0 && row <rows && col >= 0 && col < cols)
            {        
                cO = (CharObj)board[row].elementAt(col);
                if(cO.mark == constant.BLANK)    
		            setXY(event.getPoint());
		    }
		}
	 }
}