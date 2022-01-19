import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Scanner;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

@SuppressWarnings("ALL")
public class Main extends JFrame implements ActionListener{

    private JLabel logoPlaceHolder;
    private JButton startButton;
    private JButton restartButton;
    private JButton connectButton;
    private JComboBox<String> portList;
    private JButton dlmButton;
    private JButton xpButton;
    private JButton xrpButton;
    private JButton backButton;
    private JLabel topLabel;
    private JButton wlButton;
    private JButton uvButton;
    private JButton lumButton;
    String selectedDevice;
    String lightType;
    Integer page = 0;
    private JLabel pageNumber;
    private String returnInfo;
    static SerialPort ardAccess;
    static boolean received;
    Color btnDefault;
    private JButton adjDown;
    private JButton adjUp;
    private JLabel adjText;
    Integer adjNum = 0;
    String selected;
    
    String[][] DLMWL = {
            {"1", "60"},
            {"2", "80"},
            {"10", "90"},
            {"100", "100"},
            {"150", "120"},
            {"175", "200"}
    };

    String[][] DLMXRPXPUV = {
            {"100", "62"},
            {"700", "79"},
            {"1000", "84"},
            {"2500", "113"},
            {"4300", "168"},
            {"5600", "218"}
    };

    String[][] XRPXPWL = {
            {"1.0", "1"},
            {"2.0", "1"},
            {"10.0", "1"},
            {"100.0", "1"},
            {"150.0", "1"},
            {"175.0", "1"}
    };

    String[][] DLMXRPLUM = { // LUM IS BROKEN
            {"100", "1"},
            {"1900", "1"},
            {"30k", "1"},
            {"60k", "1"},
            {"100k", "1"}
    };

    JButton[] arrayOfButtons = {null, null, null};
    private JButton nextPage;
    private JButton backPage;



    public Main(){
        WindowSetup();
    }

    public void WindowSetup() {


        Container window = getContentPane(); //need this to make JFrame work well
        window.setLayout(null); //do not use any layout managers
        window.setBackground(Color.white); //make the background of the window dark gray
        setDefaultCloseOperation(EXIT_ON_CLOSE); //actually end the program when clicking the close button
        setTitle("NTexcal Lightbox");//text for the window's title bar
        setResizable(false);//don't allow the user to resize the window
        setSize(800,480);//set the size of the window to half the screen width and half the screen height//where to position the top left corner of the window

        //Main Menu

        //Logo creation
        ImageIcon logoNTexcal = new ImageIcon(ClassLoader.getSystemResource("NTexCalLogo.png"));
        logoPlaceHolder = new JLabel(logoNTexcal);
        logoPlaceHolder.setBounds(75,100,350,231);
        window.add(logoPlaceHolder);

        //Start Button
        startButton = new JButton("Start"); // Initates Button for setup and adds text
        buttonSetup(startButton, "Start", 60, 550, 145, 200, 100); // Used to simplify the amount of code needed to setup
        startButton.setEnabled(false); // Disables button until connection has been made due to start button
        window.add(startButton); // Adds button to window to be visible

        //Restart Button
        restartButton = new JButton();
        buttonSetup(restartButton, "Reset", 20, 650,375,100,50);
        window.add(restartButton);

        //Connection button to arduino
        connectButton = new JButton();
        buttonSetup(connectButton, "Connect", 11, 650, 255, 100, 50);

        window.add(connectButton);

        //Creates Port list box
        portList = new JComboBox<>();
        portList.setBounds(550,255,100,50);
        window.add(portList);

        //Populates port list
        SerialPort[] portNames = SerialPort.getCommPorts();
        for (SerialPort portName : portNames) {
            if (portName.getSystemPortName().equals("COM3") || portName.getSystemPortName().equals("ttyUSB0") || portName.getSystemPortName().equals("ttyACM0")) {
                portList.addItem(portName.getSystemPortName());
            }
        }

        //Main Menu

        //Button for DLM
        dlmButton = new JButton();
        buttonSetup(dlmButton, "DLM", 25, 65, 175, 200, 100);
        window.add(dlmButton);

        //XP Button
        xpButton = new JButton();
        buttonSetup(xpButton, "XP", 25, 295, 175, 200, 100);
        window.add(xpButton);

        //XRP Button
        xrpButton = new JButton();
        buttonSetup(xrpButton, "XRP", 25, 520,175,200,100);
        window.add(xrpButton);

        //Back button from Device Select
        backButton = new JButton();
        buttonSetup(backButton, "Back", 15,30,25,100,50);
        window.add(backButton);

        //### Selected Menu

        //Top Label
        topLabel = new JLabel("### Selected", SwingConstants.CENTER);
        topLabel.setBounds(292,25,200,100);
        topLabel.setFont(new Font("Myriad", Font.PLAIN, 25));
        window.add(topLabel);

        //White Light button
        wlButton = new JButton();
        buttonSetup(wlButton, "W/L", 25, 65, 175, 200,100);
        window.add(wlButton);

        //UV button
        uvButton = new JButton();
        buttonSetup(uvButton, "UV", 25, 295, 175, 200,100);
        window.add(uvButton);

        //Lumnens button
        lumButton = new JButton();
        buttonSetup(lumButton, "Lum", 25, 520, 175,200,100);
        window.add(lumButton);

        //Light Menu

        arrayOfButtons[0] = new JButton();
        buttonSetup(arrayOfButtons[0],"info1", 25, 70,175,200,100);
        window.add(arrayOfButtons[0]);

        arrayOfButtons[1] = new JButton();
        buttonSetup(arrayOfButtons[1], "info2", 25,295,175,200,100);
        window.add(arrayOfButtons[1]);

        arrayOfButtons[2] = new JButton();
        buttonSetup(arrayOfButtons[2], "info3", 25, 515,175,200,100);
        window.add(arrayOfButtons[2]);

        nextPage = new JButton();
        buttonSetup(nextPage, ">", 15, 725,175,50,100);
        window.add(nextPage);

        backPage = new JButton();
        buttonSetup(backPage, "<", 15,10,175,50,100);
        window.add(backPage);

        pageNumber = new JLabel("Page # of #", SwingConstants.CENTER);
        pageNumber.setBounds(292, 355, 200, 100);
        pageNumber.setFont(new Font("Myriad", Font.PLAIN, 20));
        window.add(pageNumber);

        adjDown = new JButton();
        buttonSetup(adjDown, "<", 15, 295, 300, 50, 50);
        window.add(adjDown);

        adjUp = new JButton();
        buttonSetup(adjUp, ">", 15, 445,300,50,50);
        window.add(adjUp);

        adjText = new JLabel("-+#", SwingConstants.CENTER);
        adjText.setBounds(345, 275, 100, 100);
        adjText.setFont(new Font("Myriad", Font.PLAIN, 20));
        window.add(adjText);






        disableMainMenu();
        //disableStartMenu(); // REMOVE AFTER FINISHED 3RD MENU
        disableSelectMenu();
        disableLightMenu();
    }


    // Basic Button setup
    public void buttonSetup(JButton button,String buttonText, int fontSize, int x, int y, int width, int height){
        button.setText(buttonText); // sets Button Text
        button.setBounds(x,y,width,height);  // Creates bounds for button
        button.setFont(new Font("Myriad", Font.PLAIN, fontSize)); // Sets font to Myriad (because it looks better)
        button.setForeground(Color.WHITE); // Sets text font to white
        button.setFocusPainted(false); // removes ugly box around text
        button.setBackground(Color.decode("#ff8719")); // Changes color button to orange >:)
        button.addMouseListener(hoverColor); // Changes the button pressed color to a darker orange instead of Metal L&F
        button.addActionListener(this);// Adds ActionListener so buttons are *clickable*
    }

    // Public voids for disabling and enabling start menu
    public void disableStartMenu(){
        logoPlaceHolder.setVisible(false);
        startButton.setVisible(false);
        connectButton.setVisible(false);
        portList.setVisible(false);
        restartButton.setVisible(false);
    }

    public void enableStartMenu(){
        logoPlaceHolder.setVisible(true);
        startButton.setVisible(true);
        connectButton.setVisible(true);
        portList.setVisible(true);
        restartButton.setVisible(true);

       if (ardAccess.isOpen()){ // Checks for connection before returning back to main menu to ensure good connection
            connectButton.setText("Disconnect");
            portList.setEnabled(false);
            startButton.setEnabled(true);
        }
    }

    // Disabling and enabling of main menu
    public void disableMainMenu(){
        topLabel.setVisible(false);
        dlmButton.setVisible(false);
        xpButton.setVisible(false);
        xrpButton.setVisible(false);
        backButton.setVisible(false);
    }

    public void enableMainMenu(){
        topLabel.setVisible(true);
        topLabel.setText("Main Menu");
        dlmButton.setVisible(true);
        xpButton.setVisible(true);
        xrpButton.setVisible(true);
        backButton.setVisible(true);
    }

    // enabling and disabling of select menu (DLM/XP/XRP)
    public void disableSelectMenu(){
        backButton.setVisible(false);
        topLabel.setVisible(false);
        wlButton.setVisible(false);
        uvButton.setVisible(false);
        lumButton.setVisible(false);
    }

    public void enableSelectMenu(){
        if (selectedDevice.equals(dlmButton.getText()) || selectedDevice.equals(xrpButton.getText())){
            topLabel.setText(selectedDevice + " Selected");
            topLabel.setVisible(true);
            wlButton.setVisible(true);
            uvButton.setVisible(true);
            lumButton.setVisible(true);
            backButton.setVisible(true);
            wlButton.setBounds(65,175,200,100);
            uvButton.setBounds(295,175,200,100);
            lumButton.setBounds(520,175,200,100);

        } else if (selectedDevice.equals(xpButton.getText())) {
            topLabel.setText(selectedDevice + " Selected");
            topLabel.setVisible(true);
            wlButton.setVisible(true);
            uvButton.setVisible(true);
            backButton.setVisible(true);
            wlButton.setBounds(165,175,200,100);
            uvButton.setBounds(395,175,200,100);

        }
    }

    // Enabling and disabling of light menu (set points)
    public void disableLightMenu(){
        adjNum = 0;
        selected = "";
        adjText.setVisible(false);
        adjUp.setVisible(false);
        adjDown.setVisible(false);
        topLabel.setVisible(false);
        backButton.setVisible(false);
        backPage.setVisible(false);
        nextPage.setVisible(false);
        arrayOfButtons[0].setVisible(false);
        arrayOfButtons[1].setVisible(false);
        arrayOfButtons[2].setVisible(false);
        page = 0;
        pageNumber.setVisible(false);
    }

    public void enableLightMenu(){
        adjNum = 0;
        selected = "";
        adjText.setText("0");
        adjText.setVisible(true);
        adjUp.setVisible(true);
        adjDown.setVisible(true);
        backButton.setVisible(true);
        backPage.setVisible(true);
        nextPage.setVisible(true);
        arrayOfButtons[0].setVisible(true);
        arrayOfButtons[1].setVisible(true);
        arrayOfButtons[2].setVisible(true);
        pageSetup();
        pageNumber.setVisible(true);
    }

    public void pageSetup(){ // Used to setup buttons used for UV, W/L & LUM
        if(lightType.equals(wlButton.getText()) && selectedDevice.equals(dlmButton.getText())){ // If statement for activating DLM W/L Buttons
            if (((DLMWL.length / 3) - 1) >= page){ // Checks that its not going over the max pages
                for (int i = 0; i < 3; i++){ // Array used to input text in buttons
                    arrayOfButtons[i].setText(DLMWL[i + (page * 3)][0]); // Sets text of buttons using page number to make sure its on the right track
                }
            } else {
                page = ((DLMWL.length / 3) - 1); // Stops int page from going over
            }
            pageNumber.setText("Page " + (page + 1) + " of " + (DLMWL.length / 3)); // Used to change page number
        } else if (lightType.equals(uvButton.getText())){ // If statement used for activating UV for all Devices
            if (((DLMXRPXPUV.length / 3) - 1) >= page){
                for (int i = 0; i < 3; i++){
                    arrayOfButtons[i].setText(DLMXRPXPUV[i + (page * 3)][0]);
                }
            } else {
                page = ((DLMXRPXPUV.length / 3) - 1);
            }
            pageNumber.setText("Page " + (page + 1) + " of " + (DLMXRPXPUV.length / 3));
        } else if (lightType.equals(wlButton.getText()) && selectedDevice.equals(xrpButton.getText()) || lightType.equals(wlButton.getText()) && selectedDevice.equals(xpButton.getText())){ // If statement used for activating XRP & XP W/L only
            if (((XRPXPWL.length / 3) - 1) >= page){
                for (int i = 0; i < 3; i++){
                    arrayOfButtons[i].setText(XRPXPWL[i + (page * 3)][0]);
                }
            } else {
                page = ((XRPXPWL.length / 3) - 1);
            }
            pageNumber.setText("Page " + (page + 1) + " of " + (XRPXPWL.length / 3));
        } else if (lightType.equals(lumButton.getText()) && selectedDevice.equals(xrpButton.getText()) || lightType.equals(lumButton.getText()) && selectedDevice.equals(dlmButton.getText())){ // If statement used for activating Lumen for DLM and XRP usage only
            if (page == 0){
                arrayOfButtons[2].setVisible(true);
                for (int i = 0; i < 3; i++){
                    arrayOfButtons[i].setText(DLMXRPLUM[i + (page * 3)][0]);
                }
            } else if (page == 1){
                arrayOfButtons[2].setVisible(false);
                for (int i = 0; i < 2; i++){
                    arrayOfButtons[i].setText(DLMXRPLUM[i + (page * 3)][0]);
                }
            } else {
                page = ((DLMXRPLUM.length / 3));
            }
            pageNumber.setText("Page " + (page + 1) + " of " + ((DLMXRPLUM.length / 3) + 1));
        }
    }

    //Writes to arduino!!!
    public void arduinoWirte(String a){
        try{Thread.sleep(5);} catch(Exception c){}
        PrintWriter send = new PrintWriter(ardAccess.getOutputStream());
        send.print(a);
        send.flush();
    }

    //Reads information back from last write (DEBUG ONLY)
    public void arduinoRead(){
        Scanner info = new Scanner(ardAccess.getInputStream());
        PrintWriter out = new PrintWriter(ardAccess.getOutputStream(),true);
        ardAccess.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                String input = "";

                input = info.nextLine();

                System.out.println(input);
                received=true;
            }
        });
    }

    // To access channels from arduino to digipot AD5254 use-
    // "[," Channel 1,
    // "]," Channel 2,
    // "{," Channel 3,
    // "}," Channel 4
    // This will grant access to all Channels!

    public void adjustmentSet(){ // Used to make ArrayOfButtons[] usuable
        topLabel.setVisible(true);
        if(lightType.equals(wlButton.getText()) && selectedDevice.equals(dlmButton.getText())){ // Checks for which page your on
            for (int i = 0; i < DLMWL.length; i++) { // Runs through array of DLMWL and etc..
                if (selected.equals(DLMWL[i][0])){
                    int a = Integer.parseInt(DLMWL[i][1]) + adjNum;
                    arduinoWirte(a + "[,");
                    adjText.setText(adjNum + "");
                }
            }
        } else if (lightType.equals(uvButton.getText())){
            for (int i = 0; i < DLMXRPXPUV.length; i++) {
                if (selected.equals(DLMXRPXPUV[i][0])){
                    int a = Integer.parseInt(DLMXRPXPUV[i][1]) + adjNum;
                    arduinoWirte(a + "],");
                    adjText.setText(adjNum + "");
                }
            }
        } else if (lightType.equals(wlButton.getText()) && selectedDevice.equals(xrpButton.getText()) || lightType.equals(wlButton.getText()) && selectedDevice.equals(xpButton.getText())){
            for (int i = 0; i < XRPXPWL.length; i++) {
                if (selected.equals(XRPXPWL[i][0])){
                    int a = Integer.parseInt(XRPXPWL[i][1]) + adjNum;
                    arduinoWirte(a + "[,");
                    adjText.setText(adjNum + "");
                }
            }
        } else if (lightType.equals(lumButton.getText()) && selectedDevice.equals(xrpButton.getText()) || lightType.equals(lumButton.getText()) && selectedDevice.equals(dlmButton.getText())){
            for (int i = 0; i < DLMXRPLUM.length; i++) {

            }
        }
    }

    public void buttonRun(int arrayButtonNum){ // Used to make ArrayOfButtons[] usuable
        topLabel.setVisible(true);
        if(lightType.equals(wlButton.getText()) && selectedDevice.equals(dlmButton.getText())){ // Checks for which page your on
            for (int i = 0; i < DLMWL.length; i++) { // Runs through array of DLMWL and etc..
                if (arrayOfButtons[arrayButtonNum].getText().equals(DLMWL[i][0])) { // Condition check to make sure it's the same info
                    selected = DLMWL[i][0];
                    topLabel.setText(DLMWL[i][0] + " Selected");
                    arduinoWirte(DLMWL[i][1] + "[,");
                }
            }
        } else if (lightType.equals(uvButton.getText())){
            for (int i = 0; i < DLMXRPXPUV.length; i++) {
                if (arrayOfButtons[arrayButtonNum].getText().equals(DLMXRPXPUV[i][0])) {
                    topLabel.setText(DLMXRPXPUV[i][0] + " Selected");
                    selected = DLMXRPXPUV[i][0];
                    arduinoWirte(DLMXRPXPUV[i][1] + "],");
                }
            }
        } else if (lightType.equals(wlButton.getText()) && selectedDevice.equals(xrpButton.getText()) || lightType.equals(wlButton.getText()) && selectedDevice.equals(xpButton.getText())){
            for (int i = 0; i < XRPXPWL.length; i++) {
                if (arrayOfButtons[arrayButtonNum].getText().equals(XRPXPWL[i][0])) {
                    topLabel.setText(XRPXPWL[i][0] + " Selected");
                    selected = XRPXPWL[i][0];
                    arduinoWirte(XRPXPWL[i][1] + "[,");
                }
            }
        } else if (lightType.equals(lumButton.getText()) && selectedDevice.equals(xrpButton.getText()) || lightType.equals(lumButton.getText()) && selectedDevice.equals(dlmButton.getText())){
            for (int i = 0; i < DLMXRPLUM.length; i++) {
                if (arrayOfButtons[arrayButtonNum].getText().equals(DLMXRPLUM[i][0])) {
                    topLabel.setText(DLMXRPLUM[i][0] + " Selected");
                    arduinoWirte(DLMXRPLUM[i][1] + "[,");
                }
            }
        }
    }

    // Button pressed color setup
    MouseAdapter hoverColor = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent m){
            UIManager.put("Button.select", Color.decode("#ff8719").darker());
        }
    };

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == restartButton){
            System.exit(0);
        }

        if(e.getSource() == connectButton) { // Checks and connects to arduino using portlist selected item
            if (connectButton.getText().equals("Connect")) {
                ardAccess = SerialPort.getCommPort(Objects.requireNonNull(portList.getSelectedItem()).toString());
                ardAccess.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                if (ardAccess.openPort()) {
                    connectButton.setText("Disconnect");
                    portList.setEnabled(false);
                    startButton.setEnabled(true);
                }
            } else if (connectButton.getText().equals("Disconnect")){ // Disconnects arduino from code
                connectButton.setText("Connect");
                ardAccess.closePort();
                portList.setEnabled(true);
                startButton.setEnabled(false);
            }
        }

        if (e.getSource() == startButton){
            disableStartMenu();
            enableMainMenu();
        }


        if (e.getSource() == dlmButton){
            selectedDevice = dlmButton.getText();
            topLabel.setText(selectedDevice + " Selected");
            disableMainMenu();
            enableSelectMenu();
        }

        if (e.getSource() == xpButton){
            selectedDevice = xpButton.getText();
            topLabel.setText(selectedDevice + " Selected");
            disableMainMenu();
            enableSelectMenu();
        }

        if (e.getSource() == xrpButton){
            selectedDevice = xrpButton.getText();
            topLabel.setText(selectedDevice + " Selected");
            disableMainMenu();
            enableSelectMenu();
        }

        if (e.getSource() == backButton){
            if (dlmButton.isVisible()){
                disableMainMenu();
                enableStartMenu();
            } else if (wlButton.isVisible()){
                disableSelectMenu();
                enableMainMenu();
            } else if (arrayOfButtons[0].isVisible()){
                disableLightMenu();
                enableSelectMenu();
                arduinoWirte("1[,1],1{,1},");
            }
        }

        if (e.getSource() == wlButton){
            lightType = wlButton.getText();
            disableSelectMenu();
            enableLightMenu();
        }

        if (e.getSource() == uvButton){
            lightType = uvButton.getText();
            disableSelectMenu();
            enableLightMenu();
        }

        if (e.getSource() == lumButton){
            lightType = lumButton.getText();
            disableSelectMenu();
            enableLightMenu();
        }

        if (e.getSource() == backPage){
            if (page > 0){
                page--;
            }
            pageSetup();
        }

        if (e.getSource() == nextPage){
            page++;
            pageSetup();
        }

        if (e.getSource() == arrayOfButtons[0]){
            buttonRun(0);
            adjNum = 0;
            adjText.setText("0");
        }

        if (e.getSource() == arrayOfButtons[1]){
            buttonRun(1);
            adjNum = 0;
            adjText.setText("0");
        }

        if (e.getSource() == arrayOfButtons[2]){
           buttonRun(2);
            adjNum = 0;
            adjText.setText("0");
        }

        if (e.getSource() == adjUp){
            adjNum++;
            adjustmentSet();
        }

        if (e.getSource() == adjDown){
            adjNum--;
            adjustmentSet();
        }

    }

    public static void main(String[] args) {
        Main bob = new Main();
        bob.setVisible(true);
    }
}