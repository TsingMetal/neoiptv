package com.tsing.iptv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.util.Date;

public class IptvView extends JFrame implements ViewInterface {
  
  private JDialog inputDialog; // to receive input
  private JTextField snField; // to receive SN input
  private JTextField macField; // to receive Mac input

  private JTextPane infoArea; // to show test infomation
  private JPanel infoPanel; // to show pass rate etc.
  private JLabel resultLabel; // to show test result(fail or pass)

  private JToolBar toolBar;

  private MacWriter macWriter;
  private XmlParser xmlParser;
  private Logger logger;
  private DBConnector localConnector;
  private DBConnector sfcConnector;
  private XmlWriter xmlWriter;

  private boolean showRet = false; //default not show ret XML in infoArea

  private int tested = 0; // to record the number of products tested
  private int passed = 0;
  private int skipped = 0;
  private int failed = 0;
  private double passRate = 0;

  private boolean keepHistory = true; // don't clear infoArea after each test

  public IptvView() {
    xmlParser = new IptvXmlParser(); // initialize xmlParser 
    sfcConnector = new SFCConnector(); 
    macWriter = new MacWriter(xmlParser, sfcConnector); // initialize macWriter
    
    xmlWriter = new IptvXmlWriter(); //initialize xmlWriter 
    logger = new IptvLogger(xmlWriter); //initialize logger 

    macWriter.addMacWritingListener(this); // register IptvView with macWriter
    macWriter.addMacWritingListener(logger); // register logger with macWriter
    
    EventQueue.invokeLater(() -> {
      setUI();
    });
  }

  private void setUI() {
    setTitle("Iptv Mac Writer  repair mode: false"); 
    
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
    scrollPane.setAutoscrolls(true);
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
      macWriter.checkAdv("N/A in repair mode");
    });
    toolBar.add(getAdvButton);

    JButton setAdvButton = new JButton("Enable Adv");
    setAdvButton.setToolTipText("Enable adv-security");
    setAdvButton.addActionListener(event -> {
      infoArea.setText("");
      macWriter.setAdv("N/A in repair mode");
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
    showInputDialog.setSelected(true);
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

  JRadioButtonMenuItem dbItem;
  JRadioButtonMenuItem sfcItem;
  private void setMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // set Operation menu:
    JMenu operationMenu = new JMenu("Operation");
    JRadioButtonMenuItem repairMode = 
      new JRadioButtonMenuItem("Restoration Mode");
    repairMode.addActionListener(event -> {
      if (repairMode.isSelected()) {
        String password = JOptionPane.showInputDialog(menuBar,
            "Enter password to authorize yourself:");
        if (password.equals("tsing")) { 
          macWriter.setRepairMode(repairMode.isSelected());
          toolBar.setVisible(macWriter.isRepairMode());
          dbItem.setEnabled(repairMode.isSelected());
          sfcItem.setEnabled(repairMode.isSelected());
          setTitle("Mac Writer  repair mode: " + 
            (new Boolean(repairMode.isSelected()).toString()));
        } else {
          JOptionPane.showMessageDialog(operationMenu, "Wrong password!");
          repairMode.setSelected(false);
          macWriter.setRepairMode(false);
          dbItem.setEnabled(false);
          sfcItem.setEnabled(false);
        }
      } else {
        macWriter.setRepairMode(false);
        toolBar.setVisible(false);
        dbItem.setEnabled(false);
        sfcItem.setEnabled(false);
        setTitle("Mac Writer  repair mode: " + 
           new Boolean(repairMode.isSelected()).toString());
      }
    });

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(event -> System.exit(0));

    dbItem = 
      new JRadioButtonMenuItem("Connect to Local DB");
    dbItem.setEnabled(false);
    sfcItem = 
      new JRadioButtonMenuItem("Connect to SFC");
    sfcItem.setSelected(true);
    sfcItem.setEnabled(false);

    dbItem.addActionListener(event -> {
      if (localConnector == null)
        localConnector = new IptvDBConnector();
      macWriter.setConnector(localConnector);
    });

    sfcItem.addActionListener(event -> {
      if (sfcConnector == null) 
        sfcConnector = new SFCConnector();
      macWriter.setConnector(sfcConnector);
    });

    ButtonGroup group = new ButtonGroup();
    group.add(dbItem);
    group.add(sfcItem);

    operationMenu.add(repairMode);
    operationMenu.addSeparator();
    operationMenu.add(dbItem);
    operationMenu.add(sfcItem);
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

    JCheckBoxMenuItem keepHistoryItem =
      new JCheckBoxMenuItem("Keep Test History");
    keepHistoryItem.setSelected(true);
    keepHistoryItem.addActionListener(event -> {
      keepHistory = keepHistoryItem.isSelected();
    });

    settingMenu.add(onTop);
    settingMenu.addSeparator();
    settingMenu.add(showInfoPanel);
    settingMenu.add(keepHistoryItem);
    
    //set Help menu
    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About");
    aboutItem.addActionListener(event -> {
      JOptionPane.showMessageDialog(helpMenu,
          "Mac Writer\nVersion: 1.0.0.0\nAuthor: Tsing\n" + 
          "Tel: 15285647630\n" + "Find me at: https://github.com/TsingMetal");
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
      testedField.setText("0");
      passedField.setText("0");
      skippedField.setText("0");
      failedField.setText("0");
      passRateField.setText("0");
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
          || status.contains("FAIL") // to include other "FAILS"
          || status.equals("invalid")
          || status.equals("used")) // test terminates in eigher case
      {
        if (status.equals("PASS")) {
          resultLabel.setForeground(Color.GREEN);
          showInfo(String.format("\n%20s\t\t\t\t%20s", "SN", sn), Color.GREEN);
          passed++;
        } else if (status.contains("used")) {
          resultLabel.setForeground(Color.YELLOW);
          showInfo(String.format("\n%20s\t\t\t\t%20s", "SN", sn), Color.YELLOW);
          skipped++;
        } else { //invalid or fail
          resultLabel.setForeground(Color.RED);
          showInfo(String.format("\n%20s\t\t\t\t%20s", "SN", sn), Color.RED);
          failed++;
        }

        tested++;

        resultLabel.setText(status.toUpperCase());
        updateInfoPanel();
        
        snField.requestFocus(true);
        snField.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
        macField.setBorder(null);
        snField.setText("");
        macField.setText("");

        showInfo("\n\nTest Ended\t\t\t", Color.WHITE, 24);
        showInfo(new Date().toLocaleString()+ "\n\n", Color.WHITE, 18);
        inputDialog.setVisible(true);
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
      } else if (cmd.equals("CHANGE_DB")) {
        StyleConstants.setForeground(attrSet, Color.RED);
      } else if (status.equals("used")) {
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
    passRateField.setText(
        String.format("%6.4f", (double)passed/tested));
  }

  public class InputDialog extends JDialog {

    public InputDialog(JFrame parent, boolean modal) {
      super(parent, modal);

      this.setResizable(false);

      JPanel inputPanel = new JPanel();
      inputPanel.setLayout(new GridLayout(2, 2, 0, 10));

      JLabel snLabel = new JLabel("Input SN: ", SwingConstants.CENTER);
      snField = new JTextField(25);
      snField.setBorder(BorderFactory.createLineBorder(Color.RED, 5));

      JLabel macLabel = new JLabel("Input Mac: ", SwingConstants.CENTER);
      macField = new JTextField(25);
      macField.setEditable(false);

      addDocumentListeners(); // add document listeners 
      
      inputPanel.add(snLabel);
      inputPanel.add(snField);
      inputPanel.add(macLabel);
      inputPanel.add(macField);
      
      this.setLayout(new BorderLayout());
      this.add(inputPanel, BorderLayout.CENTER);

      this.pack();
      
      snField.addActionListener(new SnListener());
      macField.addActionListener(new MacListener());
    }

    private void addDocumentListeners() {
      Document snDoc = snField.getDocument();
      snDoc.addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
          int textLength = snDoc.getLength();
          if (textLength == 20) {
            snField.setBorder(
                BorderFactory.createLineBorder(Color.GREEN, 5));
            snField.setForeground(Color.GREEN);
          } else {
            snField.setBorder(
                BorderFactory.createLineBorder(Color.RED, 5));
            snField.setForeground(Color.RED);
          }
        }

        public void removeUpdate(DocumentEvent e) {
          insertUpdate(e);
        }

        public void changedUpdate(DocumentEvent e) {
          insertUpdate(e);
        }
      });

      Document macDoc = macField.getDocument();
      macDoc.addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
          int textLength = macDoc.getLength();
          if (textLength == 15) {
            macField.setBorder(
                BorderFactory.createLineBorder(Color.GREEN, 5));
            macField.setForeground(Color.GREEN);
          } else {
            macField.setBorder(
                BorderFactory.createLineBorder(Color.RED, 5));
            macField.setForeground(Color.RED);
          }
        }

        public void removeUpdate(DocumentEvent e) {
          insertUpdate(e);
        }

        public void changedUpdate(DocumentEvent e) {
          insertUpdate(e);
        }
      });
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

        macField.setEditable(true);
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

        if (mac == null || mac.length() != 15) {
          JOptionPane.showMessageDialog(InputDialog.this,
              "Invalid Mac: " + mac + ",\n please check!",
              "Wrong Mac",
              JOptionPane.WARNING_MESSAGE);
          macField.setText("");
          return;
        } 

        macField.setEditable(false);
        inputDialog.setVisible(false);

        if (keepHistory == false)
          infoArea.setText("");

        showInfo("\nTest Start...\t\t\t", Color.WHITE, 24);
        showInfo("No." + (tested + 1) + "\n\n", Color.WHITE, 18);

        resultLabel.setForeground(Color.BLUE);
        resultLabel.setText("Testing...");
        
        // mac = mac.substring(3, 15);
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
        nextStep = macWriter.checkAdv(sn);
        if (nextStep == false) return;

        // 2nd step: check if sn is valid
        nextStep = macWriter.checkSN(sn);
        if (nextStep == false) return;

        // 3rd step: check mac
        nextStep = macWriter.checkMac(sn, mac);
        if (nextStep == false) return;

        // 4th step: write mac to stb
        mac = mac.substring(3, 15);
        nextStep = macWriter.setMac(sn, mac);
      }
    }
  }

  public static void main(String[] args) {
    IptvView view = new IptvView();
  }
}
