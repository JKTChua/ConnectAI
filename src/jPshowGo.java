/**********************************************************************

   Class: jPshowGo.java
   purpose: Indicate whose turn it is (e.g. Green = Go, Red = Wait) 
   Author: Thuan Truong
   E-mail: tim@drimgmt.com

***********************************************************************/

import com.sun.java.swing.*;
import java.beans.*;
import java.awt.*;
import javax.swing.*;

public class jPshowGo extends javax.swing.JPanel
{
	public jPshowGo(pBoard b)
	{

		//{{INIT_CONTROLS
		setLayout(null);
		Insets ins = getInsets();
		setSize(ins.left + ins.right + 430,ins.top + ins.bottom + 270);
		//}}
        pB = b;
		//{{REGISTER_LISTENERS
		//}}
	}

	//{{DECLARE_CONTROLS
	//}}
    public void paint(Graphics g) // indicate who goes and who waits
    {
        this.paintComponent(g);
        if(pB.getPlayer()==constant.X)            
        {
            g.setColor(Color.green);
            g.drawArc(12, 12, 22, 22, 0, 360);
            g.fillArc(12, 12, 22, 22, 0, 360);
            g.setColor(Color.red);
            g.drawArc(271, 12, 22, 22, 0, 360);
            g.fillArc(271, 12, 22, 22, 0, 360);
        }
        else
        {
            g.setColor(Color.red);
            g.drawArc(12, 12, 22, 22, 0, 360);
            g.fillArc(12, 12, 22, 22, 0, 360);
            g.setColor(Color.green);
            g.drawArc(271, 12, 22, 22, 0, 360);
            g.fillArc(271, 12, 22, 22, 0, 360);
        }
    }    
    private pBoard pB; // Need this variable to get player turn. 
}