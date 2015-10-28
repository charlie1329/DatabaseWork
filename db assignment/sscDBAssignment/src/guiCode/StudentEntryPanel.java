package guiCode;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import operations.RegisterAndReport;

public class StudentEntryPanel extends JPanel implements submissionForm
{
	private Connection dbConn;
	private RegisterAndReport register;
	private JFrame parent;
	
	private JLabel studentIDLabel;
	private JLabel titleIDLabel;
	private JLabel foreNameLabel;
	private JLabel familyNameLabel;
	private JLabel dateOfBirthLabel;
	private JLabel yearOfStudyLabel;
	private JLabel registrationTypeLabel;
	private JLabel eMailLabel;
	private JLabel addressLabel;
	private JLabel nOKNameLabel;
	private JLabel nOKEmailLabel;
	private JLabel nOKAddressLabel;
	
	private JButton submitButton;
	private JButton mainMenuButton;
	
	private JTextField[] fields = new JTextField[12];
	
	/**constructor sets up all gui components and arranges them nicely
	 * 
	 * @param parent the parent frame
	 * @param register where all db ops are carried out
	 */
	public StudentEntryPanel(JFrame parent, RegisterAndReport register)
	{
		this.parent = parent;
		this.register = register;
		this.dbConn = register.getConnection();
		
		this.studentIDLabel = new JLabel("Student ID: ");//setting label names
		this.titleIDLabel = new JLabel("Title: ");
		this.foreNameLabel = new JLabel("Forename: ");
		this.familyNameLabel = new JLabel("Surname: ");
		this.dateOfBirthLabel = new JLabel("DOB: ");
		this.yearOfStudyLabel = new JLabel("Year of Study: ");
		this.registrationTypeLabel = new JLabel("Registration Description");
		this.eMailLabel = new JLabel("Email: ");
		this.addressLabel = new JLabel("Address: ");
		this.nOKNameLabel = new JLabel("Next Of Kin Name: ");
		this.nOKEmailLabel = new JLabel("Next Of Kin Email: ");
		this.nOKAddressLabel = new JLabel("Next Of Kin Address: ");
		
		for(int i = 0; i < this.fields.length; i++)
		{
			this.fields[i] = new JTextField();
		}
		
		this.submitButton = new JButton("Submit");//setting up buttons
		this.submitButton.addActionListener(e -> submit());
		this.mainMenuButton = new JButton("Main Menu");
		this.mainMenuButton.addActionListener(e -> destroy());
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(submitButton);
		buttonPanel.add(mainMenuButton);
		
		GroupLayout group = new GroupLayout(this);
		setLayout(group);
		group.setAutoCreateGaps(true);
		group.setAutoCreateContainerGaps(true);
		
		group.setHorizontalGroup(//setting horizontal layout
			group.createSequentialGroup()
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.studentIDLabel)
						.addComponent(this.titleIDLabel)
						.addComponent(this.foreNameLabel)
						.addComponent(this.familyNameLabel)
						.addComponent(this.dateOfBirthLabel)
						.addComponent(this.yearOfStudyLabel))
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.fields[0])
						.addComponent(this.fields[1])
						.addComponent(this.fields[2])
						.addComponent(this.fields[3])
						.addComponent(this.fields[4])
						.addComponent(this.fields[5]))
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.registrationTypeLabel)
						.addComponent(this.eMailLabel)
						.addComponent(this.addressLabel)
						.addComponent(this.nOKNameLabel)
						.addComponent(this.nOKEmailLabel)
						.addComponent(this.nOKAddressLabel))
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.fields[6])
						.addComponent(this.fields[7])
						.addComponent(this.fields[8])
						.addComponent(this.fields[9])
						.addComponent(this.fields[10])
						.addComponent(this.fields[11])
						.addComponent(buttonPanel)));
				
		group.setVerticalGroup(//setting vertical layout
				group.createSequentialGroup()
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.studentIDLabel)
						.addComponent(this.fields[0])
						.addComponent(this.registrationTypeLabel)
						.addComponent(this.fields[6]))
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.titleIDLabel)
						.addComponent(this.fields[1])
						.addComponent(this.eMailLabel)
						.addComponent(this.fields[7]))
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.foreNameLabel)
						.addComponent(this.fields[2])
						.addComponent(this.addressLabel)
						.addComponent(this.fields[8]))
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.familyNameLabel)
						.addComponent(this.fields[3])
						.addComponent(this.nOKNameLabel)
						.addComponent(this.fields[9]))
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.dateOfBirthLabel)
						.addComponent(this.fields[4])
						.addComponent(this.nOKEmailLabel)
						.addComponent(this.fields[10]))
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(this.yearOfStudyLabel)
						.addComponent(this.fields[5])
						.addComponent(this.nOKAddressLabel)
						.addComponent(this.fields[11]))
				.addGroup(group.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(buttonPanel)));
	}
	
	
	/**this method will try to submit the form 
	 * if not suitable errors will be thrown
	 */
	@Override
	public void submit() 
	{
		try//try to submit form
		{
			String[] toInsert = new String[12];
			for(int i = 0; i < this.fields.length; i++)
			{
				toInsert[i] = fields[i].getText();
			}
			
			this.register.insertStudent(toInsert);//try to insert student
			JOptionPane.showMessageDialog(this.parent,"Student successfully registered");
			this.destroy();
			
		}
		catch(SQLException e)
		{
			this.rollBack();
			JOptionPane.showMessageDialog(this.parent,
				    e.getMessage(),
				    "SQL Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		catch(NumberFormatException e)
		{
			this.rollBack();
			JOptionPane.showMessageDialog(this.parent, 
					"You have entered a string when there should be a number. Please review your input",
					"Number Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(IllegalArgumentException e)
		{
			this.rollBack();
			JOptionPane.showMessageDialog(this.parent, 
					"You have entered an incorrect date format. Please try again",
					"Date Error", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	/**rolls back the transaction on the database
	 * 
	 */
	public void rollBack()
	{
		try
		{
			this.dbConn.rollback();//stopping submission of transaction
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(this.parent,
				    "Problem with database; will now shut down. Sorry for any convenience.",
				    "Rollback Error",
				    JOptionPane.ERROR_MESSAGE);
			try//try to close in this scenario
			{
				this.dbConn.close();
			}
			catch(SQLException e2)
			{
				System.exit(-1);
			}
		}
	}
	
	/**destroys the current panel and takes back to main menu
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
