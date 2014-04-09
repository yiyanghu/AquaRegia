
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;



public class WalletBackground extends JFrame{
	
	public WalletBackground(){
		initUI();
	}
	
	private void initUI(){
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		
		panel.setLayout(null);
		
		JButton quitButton = new JButton("Cancel");
		quitButton.setBounds(420,520,80,30);
		
		quitButton.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				System.exit(0);
			}
		});
		
		panel.add(quitButton);
		
		setTitle("Aqua Regia");
		setSize(700,600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		
	}
	
	private void addTab(){
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent send = makeTextPanel("The contents of send");
		
	}
	
	protected JComponent makeTextPanel( String text){
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1,1));
		panel.add(filler);
		return panel;
		
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				WalletBackground ex = new WalletBackground();
				ex.setVisible(true);
			}
		});
	}

}
