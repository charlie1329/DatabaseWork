package guiCode;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import operations.RegisterAndReport;

/**this class will provide the gui work for the inserting of a new tutor
 * 
 * @author Charlie Street
 *
 */
public class TutorEntryForm extends JPanel implements submissionForm
{
	private JLabel studentLabel;
	private JLabel tutorLabel;
	
	private JTextField studentField;
	private JTextField tutorField;
	
	private JButton submitButton;
	private JButton mainMenuButton;
	
	private JFrame parent;
	private RegisterAndReport register;
	
	/**constructor sets up components of panel
	 * 
	 * @param parent the parent frame
	 * @param register the register object for the db ops
	 */
	public TutorEntryForm(JFrame parent, RegisterAndReport register)
	{
		this.parent = parent;
		this.register = register;
		
		this.studentLabel = new JLabel("Student ID: ");
		this.tutorLabel = new JLabel("Lecturer ID: ");
		
		this.studentField = new JTextField();
		this.tutorField = new JTextField();
		
		this.submitButton = new JButton("Submit");//setting up buttons
		this.submitButton.addActionListener(e -> this.submit());
		this.mainMenuButton = new JButton("Main Menu");
		this.mainMenuButton.addActionListener(e->this.destroy());
		
		JPanel buttons = new JPanel();
		buttons.add(this.submitButton);
		buttons.add(this.mainMenuButton);
		
		BorderLayout border = new BorderLayout();
		setLayout(border);
		
		JPanel groupStuff = new JPanel();
		GroupLayout group = new GroupLayout(groupStuff);
		groupStuff.setLayout(group);
		group.setAutoCreateGaps(true);
		group.setAutoCreateContainerGaps(true);
		
		group.setHorizontalGroup(//setting horizontal layout of panel
				group.createSequentialGroup()
					.addGroup(group.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(this.studentLabel)
							.addComponent(this.tutorLabel))
					.addGroup(group.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(this.studentField)
							.addComponent(this.tutorField)));
					
		
		group.setVerticalGroup(//setting vertical layout of panel
				group.createSequentialGroup()
					.addGroup(group.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.studentLabel)
						.addComponent(this.studentField))
					.addGroup(group.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.tutorLabel)
						.addComponent(this.tutorField)));
						
							
		add(groupStuff,BorderLayout.CENTER);
		
		
		add(groupStuff,BorderLayout.CENTER);
		add(buttons,BorderLayout.SOUTH);
		
	}

	/**this method will actually try to submit the form
	 * it will deal with any necessary errors
	 * 
	 */
	@Override
	public void submit() 
	{
		try
		{
			register.insertNewTutorLink(this.studentField.getText(), this.tutorField.getText());//trying to register tutot
			JOptionPane.showMessageDialog(this.parent,"Tutor successfully registered");
			this.destroy();
		}
		catch(SQLException e)//if error in database
		{
			JOptionPane.showMessageDialog(this.parent,"One of the two ID's entered is invalid. Please try again","SQL Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(NumberFormatException e)//if incorrect type to insert in prepared statement
		{
			JOptionPane.showMessageDialog(this.parent,"Both fields should be a number. Please try again.","Number Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**method will destroy current panel and return to main menu
	 * 
	 */
	public void destroy()
	{
		this.parent.getContentPane().remove(this);
		this.parent.getContentPane().removeAll();
		this.parent.getContentPane().revalidate();
		this.parent.getContentPane().repaint();
		MainMenuPanel main = new MainMenuPanel(parent,register);
		this.parent.add(main);
	}
}
