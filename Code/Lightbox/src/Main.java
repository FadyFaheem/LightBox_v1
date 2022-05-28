import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main extends JFrame implements ActionListener {

    // START PAGE
    private JButton startButton;
    private JButton restartButton;
    private JButton connectButton;
    private JLabel logoPlaceHolder;
    private JComboBox<String> portList;
    static SerialPort ardAccess;
    static boolean received;

    // MAIN MENU
    private int pageNumber;
    private JButton backButton;
    private JButton addButton;
    private JButton deleteButton;
    private JLabel topLabel;
    private JButton[] mainMenuButtons = new JButton[3];

    private ArrayList<String> sensors = new ArrayList<String>();

    public Main() {
        GUISetup();
    }

    public void GUISetup(){


        // CONTAINER SETUP

        Container MainWindow = getContentPane(); //need this to make JFrame work well
        MainWindow.setLayout(null); //do not use any layout managers
        MainWindow.setBackground(Color.decode("#212426")); //make the background of the window dark gray
        setDefaultCloseOperation(EXIT_ON_CLOSE); //actually end the program when clicking the close button
        setTitle("NTexcal Lightbox");//text for the window's title bar
        setResizable(false);//don't allow the user to resize the window
        setSize(1024,600);//set the size of the window to half the screen width and half the screen height//where to position the top left corner of the window


        // START MENU

        // LOGO
        ImageIcon logoNTexcal = new ImageIcon(ClassLoader.getSystemResource("NTexCalLogo.png"));
        logoPlaceHolder = new JLabel(logoNTexcal);
        logoPlaceHolder.setBounds(-25,50,700,462);
        MainWindow.add(logoPlaceHolder);

        // BUTTONS
        startButton = GUI.buttonSetup("Start", 60, 700, 100, 250, 100, false); // Initiates Button for setup and adds text
        startButton.addActionListener(this);
        MainWindow.add(startButton);

        restartButton = GUI.buttonSetup("Reset", 30, 750, 400, 150, 75, true);
        restartButton.addActionListener(this);
        MainWindow.add(restartButton);

        // USB PORT LIST
        portList = new JComboBox<>();
        portList.setBounds(700,215,250,75);
        MainWindow.add(portList);

        // Used for searching usb ports for Arduino
        SerialPort[] portNames = SerialPort.getCommPorts();
        for (SerialPort portName : portNames) {
            if (portName.getSystemPortName().equals("COM3") || portName.getSystemPortName().equals("ttyUSB0") || portName.getSystemPortName().equals("ttyACM0")) {
                portList.addItem(portName.getSystemPortName());
            }
        }

        // Must stay here due to port list doesn't get populated until for loop
        connectButton = GUI.buttonSetup("Connect", 20, 750, 305, 150, 75, (portList.getItemCount() != 0));
        connectButton.addActionListener(this);
        MainWindow.add(connectButton);

        // DEBUGING
        disableStartMenu();

        // MAIN MENU

        topLabel = GUI.labelSetup("Main Menu", 30, 420, 25,200,100,true);
        MainWindow.add(topLabel);

        mainMenuButtons[0] = GUI.buttonSetup("buttonOne", 25, 55, 200, 300, 150, true);
        mainMenuButtons[0].addActionListener(this);
        MainWindow.add(mainMenuButtons[0]);

        mainMenuButtons[1] = GUI.buttonSetup("buttonTwo", 25, 365, 200, 300, 150, true);
        mainMenuButtons[1].addActionListener(this);
        MainWindow.add(mainMenuButtons[1]);

        mainMenuButtons[2] = GUI.buttonSetup("buttonThree", 25, 675,200,300,150, true);
        mainMenuButtons[2].addActionListener(this);
        MainWindow.add(mainMenuButtons[2]);

        backButton = GUI.buttonSetup("Back",15,100,35,125,75, true);
        backButton.addActionListener(this);
        MainWindow.add(backButton);

        deleteButton = GUI.buttonSetup("Delete",15,675,35,125,75, true);
        deleteButton.addActionListener(this);
        MainWindow.add(deleteButton);

        addButton = GUI.buttonSetup("Add",15,825,35,125,75, true);
        addButton.addActionListener(this);
        MainWindow.add(addButton);

        disableMainMenu();
        enableMainMenu();
    }

    // SWITCHING PAGES

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

        if (ardAccess != null){// Checks for connection before returning to main menu to ensure good connection
            if (ardAccess.isOpen()) {
                connectButton.setText("Disconnect");
                portList.setEnabled(false);
                startButton.setEnabled(true);
            }
        }
    }

    public void disableMainMenu(){
        topLabel.setVisible(false);
        mainMenuButtons[0].setVisible(false);
        mainMenuButtons[1].setVisible(false);
        mainMenuButtons[2].setVisible(false);
        backButton.setVisible(false);
        addButton.setVisible(false);
        deleteButton.setVisible(false);
    }

    public void enableMainMenu(){
        topLabel.setVisible(true);
        topLabel.setText("Main Menu");
        backButton .setVisible(true);
        deleteButton.setVisible(true);
        addButton.setVisible(true);

        if (sensors.size() == 0) {
            deleteButton.setEnabled(false);
        }


        if (sensors.size() != 0) {
            for (int i = 0; i < mainMenuButtons.length; i++){
                if (sensors.size() > i) {
                    mainMenuButtons[i].setVisible(true);
                    mainMenuButtons[i].setText(sensors.get(i ));
                }
            }
        }
    }

    public void enableAddSensor(){
        topLabel.setVisible(true);
        topLabel.setText("Add Sensor");
        backButton.setVisible(true);

    }

    public void tryReadingMainMenuConfig(){
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/MainMenuSensors.txt"));
            String data;
            while ((data = br.readLine()) != null){
                sensors.add(data);
            }
            br.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void tryWriteMainMenuConfig(){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/MainMenuSensors.txt"));
            for (int i = 0; i < sensors.size(); i++){
                bw.write(sensors.get(i) + "\n");
            }
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void arduinoWrite(String a){
        try{Thread.sleep(5);} catch(Exception ignored){}
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
                String input;

                input = info.nextLine();

                System.out.println(input);
                received=true;
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == deleteButton) {

        }

        if (e.getSource() == addButton) {
            disableMainMenu();
            enableAddSensor();
        }

        if (e.getSource() == backButton) {
            disableMainMenu();
            enableStartMenu();
        }

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
    }

    public static void main(String[] args) {
        Main bob = new Main();
        bob.setVisible(true);
    }

}
