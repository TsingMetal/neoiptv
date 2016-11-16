package com.tsing.iptv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;

import java.util.Date;

public class IptvView extends JFrame implements ViewInterface {
  
  private JDialog inputDialog; // to receive input
  private JTextField snField; // to receive SN input
  private JTextField macField; // to receive Mac input

  private JTextPane infoArea; // to show test infomation
  private JPanel infoPanel; // to show xml string return from STB
	private JLabel resultLabel; // to show test result(fail or pass)

  private JToolBar toolBar;

  private MacWriter macWriter;
  private XmlParser xmlParser;
  private Logger logger;
  private DBConnector dbConnector;
  private XmlWriter xmlWriter;

  private boolean showRet = false; //default not show ret XML in infoArea

  private int tested = 0; // to record the number of products tested
  private int passed = 0;
  private int skipped = 0;
  private int failed = 0;
  private double passRate = 0;

  public IptvView() {
    xmlParser = new IptvXmlParser(); // initialize xmlParser 
    dbConnector = new IptvDBConnector(); //~~ DBConnector unimplemented yet 
    macWriter = new MacWriter(xmlParser, dbConnector); // initialize macWriter
    
    xmlWriter = new IptvXmlWriter(); //initialize xmlWriter 
    logger = new IptvLogger(xmlWriter); //initialize logger 

    macWriter.addMacWritingListener(this); // register IptvView with macWriter
    macWriter.addMacWritingListener(logger); // register logger with macWriter
		
    EventQueue.invokeLater(() -> {
      setUI();
    });
  }

  private void setUI() {
    setTitle("Iptv Mac Writer  repair mode: false"); // set frame title
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    int screenWidth = screenSize.width; // get screen width;
    int screenHeight = screenSize.height; // get screen height;
    setSize(screenWidth*5 / 7, screenHeight*3 / 4); // set frame size;
    setLocation(screenWidth*1 / 8, screenHeight*1 / 8); //set initial position;

    setMainPanel();
    setToolBar();
    setMenuBar();
    setInfoPanel();
		
		inputDialog = new InputDialog(this, false);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
		inputDialog.setVisible(true);
  }

  private void setMainPanel() {
    // set infomation area:
    JPanel panel = new JPanel(new BorderLayout(5, 10));

    infoArea = new JTextPane();
		infoArea.setEditable(false);
		infoArea.setBackground(Color.BLACK);
    infoArea.setBorder(
        BorderFactory.createTitledBorder("Test Information Area"));
    
    infoPanel = new JPanel();

		infoPanel.setBackground(Color.GRAY);
    infoPanel.setBorder( BorderFactory.createTitledBorder("Yield Status"));
    infoPanel.setVisible(false); // default not show infoPanel;

		resultLabel = new JLabel("WAIT...", SwingConstants.CENTER);
		resultLabel.setFont(new Font("Serif", Font.BOLD, 48));
		resultLabel.setForeground(Color.BLUE);

		JScrollPane scrollPane = new JScrollPane(infoArea);
    panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(resultLabel, BorderLayout.SOUTH);
    panel.add(infoPanel, BorderLayout.NORTH);

    add(panel, BorderLayout.CENTER);  // add panel to frame
  }

  private void setToolBar() {
    // set toolbar:
    toolBar = new JToolBar();
    toolBar.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    toolBar.setEnabled(false);
    toolBar.setVisible(false);
    
    JButton getButton = new JButton("Get Mac");
    getButton.setToolTipText("get SN and Mac from STB");
    getButton.addActionListener(event -> {
      infoArea.setText("");
      macWriter.getSN();
    });
    toolBar.add(getButton);

    JButton getAdvButton = new JButton("Get Adv");
    getAdvButton.setToolTipText("get adv-security from STB");
    getAdvButton.addActionListener(event -> {
      infoArea.setText("");
      macWriter.checkAdv();
    });
    toolBar.add(getAdvButton);

    JButton setAdvButton = new JButton("Enable Adv");
    setAdvButton.setToolTipText("Enable adv-security");
    setAdvButton.addActionListener(event -> {
      infoArea.setText("");
      macWriter.setAdv();
    });
    toolBar.add(setAdvButton);

    JButton setButton = new JButton("Set Mac");
    setButton.setToolTipText("Write Mac and SN to STB");
    setButton.addActionListener(event -> {
      // unimplemented
    });
    setButton.setEnabled(false); // Inable setButton
    toolBar.add(setButton);

    JButton eraseButton = new JButton("Erase Mac");
    eraseButton.setToolTipText("Erase mac from STB");
    eraseButton.addActionListener(event -> {
      infoArea.setText("");
      macWriter.eraseMac();
    });
    toolBar.add(eraseButton);

    JButton rebootSTBButton = new JButton("Reboot STB");
    rebootSTBButton.setToolTipText("Reboot STB");
    rebootSTBButton.addActionListener(event -> {
      infoArea.setText("");
      macWriter.rebootSTB();
    });
    toolBar.add(rebootSTBButton);

    toolBar.addSeparator();

    JCheckBox showInputDialog = new JCheckBox("Show Input Dialog");
    showInputDialog.setToolTipText("Display the SN and Mac input dialog");
    showInputDialog.addActionListener(event -> {
      inputDialog.setVisible(showInputDialog.isSelected());
    });
    toolBar.add(showInputDialog);

    JCheckBox showRetBox = new JCheckBox("Show Ret");
    showRetBox.setToolTipText("show ret XML in the infamation area");
    showRetBox.addActionListener(event -> {
      showRet = (showRetBox.isSelected());
    });
    toolBar.add(showRetBox);

    add(toolBar, BorderLayout.NORTH); //add toolbar to frame
  }

  private void setMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // set Operation menu:
    JMenu operationMenu = new JMenu("Operation");
    JRadioButtonMenuItem repairMode = 
      new JRadioButtonMenuItem("Restoration Mode");
    repairMode.addActionListener(event -> {
      if (repairMode.isSelected()) {
        String password = JOptionPane.showInputDialog(menuBar,
            "Enter password to authenrize yourself:");
        if (password.equals("tsing")) { 
          macWriter.setRepairMode(repairMode.isSelected());
          toolBar.setVisible(macWriter.isRepairMode());
          setTitle("Mac Writer  repair mode: " + 
            (new Boolean(repairMode.isSelected()).toString()));
        } else {
          JOptionPane.showMessageDialog(operationMenu, "Wrong password!");
          repairMode.setSelected(false);
          macWriter.setRepairMode(false);
        }
      } else {
        macWriter.setRepairMode(false);
        toolBar.setVisible(false);
        setTitle("Mac Writer  repair mode: " + 
           new Boolean(repairMode.isSelected()).toString());
      }
    });

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(event -> System.exit(0));

    operationMenu.add(repairMode);
    operationMenu.addSeparator();
    operationMenu.add(exitItem);

    // set Setting Menu
    JMenu settingMenu = new JMenu("Setting");
    JCheckBoxMenuItem onTop = new JCheckBoxMenuItem("OnTop");
    onTop.addActionListener(event -> {
      setAlwaysOnTop(onTop.isSelected());
    });

    JCheckBoxMenuItem showInfoPanel = 
      new JCheckBoxMenuItem("Show Yield Infomation");
    showInfoPanel.addActionListener(event -> {
      infoPanel.setVisible(showInfoPanel.isSelected());
    });

    settingMenu.add(onTop);
    settingMenu.add(showInfoPanel);
    
    //set Help menu
    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About");
    aboutItem.addActionListener(event -> {
      JOptionPane.showMessageDialog(helpMenu,
          "Mac Writer\nversion 1.0.0.0\nAuthor Tsing\n" + "Tel: 15285647630");
    });
    helpMenu.add(aboutItem);

    menuBar.add(operationMenu);
    menuBar.add(settingMenu);
    menuBar.add(helpMenu);

    setJMenuBar(menuBar);
  }
  
  JTextField testedField; 
  JTextField passedField;
  JTextField skippedField;
  JTextField failedField;
  JTextField passRateField;
  private void setInfoPanel() {
    JLabel testedLabel = new JLabel("Tested:");
    testedLabel.setForeground(Color.BLUE);
    testedField = new JTextField(6);
    testedField.setForeground(Color.BLUE);
    testedField.setEditable(false);
    infoPanel.add(testedLabel);
    infoPanel.add(testedField);

    JLabel passedLabel = new JLabel("Passed:");
    passedLabel.setForeground(Color.GREEN);
    passedField = new JTextField(6);
    passedField.setForeground(Color.GREEN);
    passedField.setEditable(false);
    infoPanel.add(passedLabel);
    infoPanel.add(passedField);

    JLabel skippedLabel = new JLabel("Skipped:");
    skippedLabel.setForeground(Color.YELLOW);
    skippedField = new JTextField(6);
    skippedField.setForeground(Color.YELLOW);
    skippedField.setEditable(false);
    infoPanel.add(skippedLabel);
    infoPanel.add(skippedField);

    JLabel failedLabel = new JLabel("Failed:");
    failedLabel.setForeground(Color.RED);
    failedField = new JTextField(6);
    failedField.setForeground(Color.RED);
    failedField.setEditable(false);
    infoPanel.add(failedLabel);
    infoPanel.add(failedField);

    JLabel passRateLabel = new JLabel("Pass Rate:");
    passRateLabel.setForeground(Color.PINK);
    passRateField = new JTextField(6);
    passRateField.setForeground(Color.PINK);
    passRateField.setEditable(false);
    infoPanel.add(passRateLabel);
    infoPanel.add(passRateField);

    JButton resetButton = new JButton("Reset");
    resetButton.setToolTipText("Reset yield data");
    resetButton.addActionListener(event -> {
      tested = 0;
      passed = 0;
      skipped = 0;
      failed = 0;
      passRate = 0;
      testedField.setText("");
      passedField.setText("");
      skippedField.setText("");
      failedField.setText("");
      passRateField.setText("");
    });
		infoPanel.add(resetButton);
  }

	public void macWritingPerformed(MacWritingEvent e) {
    String cmd = e.getCmd();
    String status = e.getStatus();
    String sn = e.getSN(); 

    EventQueue.invokeLater(() -> {
      String ret = e.getRetXml();
      if (showRet && !ret.equals("N/A")) {
        String info = String.format("\n%20s\t\t\t%20s\n", "ret", ret);
        showInfo(info, Color.BLUE);
      }

      showInfo(cmd, status);

      if (status.equals("PASS") 
          || status.equals("FAIL") 
          || status.equals("invalid")
          || status.equals("skip")) // test terminates in eigher case
      {
        if (status.equals("PASS")) {
          resultLabel.setForeground(Color.GREEN);
          showInfo(String.format("\n%20s\t\t\t\t%20s", "SN", sn), Color.GREEN);
          passed++;
        } else if (status.contains("skip")) {
          resultLabel.setForeground(Color.YELLOW);
          showInfo(String.format("\n%20s\t\t\t\t%20s", "SN", sn), Color.YELLOW);
          skipped++;
        } else { //invalid or fail
          resultLabel.setForeground(Color.RED);
          showInfo(String.format("\n%20s\t\t\t\t%20s", "SN", sn), Color.RED);
          failed++;
        }

        tested++;

        resultLabel.setText(status);
        updateInfoPanel();
        
        snField.requestFocus(true);
        snField.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
        macField.setBorder(null);
        snField.setText("");
        macField.setText("");

        showInfo("\n\nTest Ended\t\t\t", Color.WHITE, 24);
        showInfo(new Date().toLocaleString(), Color.WHITE, 18);
      } 
    });
  }

  private void showInfo(String cmd, String status) {
    String info = String.format("\n%20s\t\t\t\t%20s\n", cmd, status);
    Document doc = infoArea.getDocument();
    SimpleAttributeSet attrSet = new SimpleAttributeSet();
    try {
			status = status.toLowerCase();
      if (status.equals("pass")) {
        StyleConstants.setForeground(attrSet, Color.GREEN);
      } else if (status.equals("fail")) {
        StyleConstants.setForeground(attrSet, Color.RED);
      } else if (status.equals("skip")) {
        StyleConstants.setForeground(attrSet, Color.YELLOW);
      } else {
        StyleConstants.setForeground(attrSet, Color.CYAN);
      }

      doc.insertString(doc.getLength(), info, attrSet);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void showInfo(String str, Color color) {
    Document doc = infoArea.getDocument();
    SimpleAttributeSet attrSet = new SimpleAttributeSet();
    try {
      StyleConstants.setForeground(attrSet, color);
      doc.insertString(doc.getLength(), str, attrSet);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

	private void showInfo(String str, Color color, int fontSize) {
		Document doc = infoArea.getDocument();
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		try {
			StyleConstants.setForeground(attrSet, color);
			StyleConstants.setFontSize(attrSet, fontSize);
			doc.insertString(doc.getLength(), str, attrSet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

  private void updateInfoPanel() {
    testedField.setText(tested + "");
    passedField.setText(passed + "");
    skippedField.setText(skipped + "");
    failedField.setText(failed + "");
    passRateField.setText((new Double(passed) / tested) + "%");
  }

	public class InputDialog extends JDialog {

		public InputDialog(Frame parent, boolean modal) {
			super(parent, modal);

			JPanel inputPanel = new JPanel();
			inputPanel.setLayout(new GridLayout(2, 2, 0, 10));

			JLabel snLabel = new JLabel("Input SN: ", SwingConstants.CENTER);
			snField = new JTextField(25);
			snField.setBorder(BorderFactory.createLineBorder(Color.RED, 5));

			JLabel macLabel = new JLabel("Input mac: ", SwingConstants.CENTER);
			macField = new JTextField(25);
			
			inputPanel.add(snLabel);
			inputPanel.add(snField);
			inputPanel.add(macLabel);
			inputPanel.add(macField);
			
			JButton closeButton = new JButton("X");
			closeButton.addActionListener(event -> {
				this.setVisible(false);
			});

			this.setLayout(new BorderLayout());
			this.add(inputPanel, BorderLayout.CENTER);
			this.add(closeButton, BorderLayout.SOUTH);

			this.pack();
			
			snField.addActionListener(new SnListener());
			macField.addActionListener(new MacListener());
		}

		class SnListener implements ActionListener {
			String sn;

			public void actionPerformed(ActionEvent e) {
				sn = snField.getText().trim();

				if (sn == null || sn.length() != 20) {
					JOptionPane.showMessageDialog(InputDialog.this,
							"Invalid SN: " + sn + ",\n please check!",
							"Wrong SN",
							JOptionPane.WARNING_MESSAGE);
          snField.setText("");
					return;
				}

        snField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
				macField.setFocusable(true);
				macField.requestFocus(true);
        macField.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
			}
		}

    class MacListener implements ActionListener {
      String sn;
      String mac;

      public void actionPerformed(ActionEvent e) { 
        sn = snField.getText();
        mac = macField.getText().trim();

        if (mac == null || mac.length() != 12) {
          JOptionPane.showMessageDialog(InputDialog.this,
              "Invalid Mac: " + mac + ",\n please check!",
              "Wrong Mac",
              JOptionPane.WARNING_MESSAGE);
          macField.setText("");
          return;
        } 

        macField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
        
        infoArea.setText("");

				showInfo("Test Start...\n\n", Color.WHITE, 24);

        resultLabel.setForeground(Color.BLUE);
        resultLabel.setText("Testing...");
        
        WriteThread thread = new WriteThread(sn, mac);
        Thread writeThread = new Thread(thread);
        writeThread.start();
      }
    }

    class WriteThread implements Runnable {
      String sn;
      String mac;

      public WriteThread(String sn, String mac) {
        this.sn = sn;
        this.mac = mac;
      }

      public void run() {
				boolean nextStep = true;

				// 1st step: check and ennable adv-security
				nextStep = macWriter.checkAdv();
				if (nextStep == false) return;

				// 2nd step: check if sn is valid
				nextStep = macWriter.checkSN(sn);
				if (nextStep == false) return;

				// 3rd step: check mac
				nextStep = macWriter.checkMac(sn, mac);
				if (nextStep == false) return;

				// 4th step: write mac to stb
				nextStep = macWriter.setMac(sn, mac);
      }
    }
  }

	public static void main(String[] args) {
		IptvView view = new IptvView();
	}
}
