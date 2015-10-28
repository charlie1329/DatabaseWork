package guiCode;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import operations.RegisterAndReport;

/** this class actually runs the database system
 * 
 * @author Charlie Street
 *
 */
public class RunDatabaseSystem
{
	/**main method starts the system
	 * 
	 */
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("University Database System");//creating frame with title
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//allowing my own close operation
		frame.setResizable(false);//don't want resizable
		frame.setSize(600, 230);
		
		//set true if you want to create the database before running the system
		RegisterAndReport register = new RegisterAndReport("jdbc:postgresql://dbteach2.cs.bham.ac.uk/cxs412",true);//will sort out all db ops
		MainMenuPanel menu = new MainMenuPanel(frame, register);//the start of the gui
		
		frame.addWindowListener(new WindowAdapter(){//closing database connection on exit and then closing system
			public void windowClosing(WindowEvent e)
			{
				register.shutDown();
			}
		});
		
		frame.add(menu);
		frame.setLocationRelativeTo(null);//centering on screen
		frame.setVisible(true);
	}
}
