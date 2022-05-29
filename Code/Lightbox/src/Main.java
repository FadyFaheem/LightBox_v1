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

import static java.lang.Math.ceil;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class Main extends JFrame implements ActionListener {

    // START PAGE
    private JButton startButton, restartButton, connectButton;
    private JLabel logoPlaceHolder;
    private JComboBox<String> portList;
    static SerialPort ardAccess;
    static boolean received;

    // MAIN MENU
    private int pageNumber, pageMax;
    private JButton backButton, addButton, deleteButton, backPage, nextPage;
    private JLabel topLabel, pageLabel;
    private final JButton[] mainMenuButtons = new JButton[3];
    private ArrayList<String> sensors = new ArrayList<>();

    // ADD SENSOR MENU
    private JTextField sensorTextField;
    private JButton saveButton;
    private boolean isDeleteModeEnabled;

    // SENSOR SET POINTS MENU
    private String sensorName;
    private JButton deleteSetPoint, addSetPoint, editSetPoint;
    private JButton[] setPointButtons = new JButton[3];


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
        startButton = GUI.buttonSetup("Start", 60, 700, 100, 250, 100, true); // Initiates Button for setup and adds text
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

        // MAIN MENU

        topLabel = GUI.labelSetup("Main Menu", 20, 418, 25,200,100,true);
        MainWindow.add(topLabel);

        pageLabel = GUI.labelSetup("Page # of #",20,418,450,200,100,true);
        MainWindow.add(pageLabel);

        mainMenuButtons[0] = GUI.buttonSetup("buttonOne", 25, 130, 225, 250, 125, true);
        mainMenuButtons[0].addActionListener(this);
        MainWindow.add(mainMenuButtons[0]);

        mainMenuButtons[1] = GUI.buttonSetup("buttonTwo", 25, 390, 225, 250, 125, true);
        mainMenuButtons[1].addActionListener(this);
        MainWindow.add(mainMenuButtons[1]);

        mainMenuButtons[2] = GUI.buttonSetup("buttonThree", 25, 650,225,250,125, true);
        mainMenuButtons[2].addActionListener(this);
        MainWindow.add(mainMenuButtons[2]);

        backPage = GUI.buttonSetup("<",25,20,225,100,125,true);
        backPage.addActionListener(this);
        MainWindow.add(backPage);

        nextPage = GUI.buttonSetup(">", 25, 910, 225, 100,125, true);
        nextPage.addActionListener(this);
        MainWindow.add(nextPage);

        backButton = GUI.buttonSetup("Back",15,100,35,125,75, true);
        backButton.addActionListener(this);
        MainWindow.add(backButton);

        deleteButton = GUI.buttonSetup("Delete",15,675,35,125,75, true);
        deleteButton.addActionListener(this);
        MainWindow.add(deleteButton);

        addButton = GUI.buttonSetup("Add",15,825,35,125,75, true);
        addButton.addActionListener(this);
        MainWindow.add(addButton);

        // ADD SENSOR MENU
        sensorTextField = new JTextField();
        sensorTextField.setBounds(265,200,500,50);
        MainWindow.add(sensorTextField);

        saveButton = GUI.buttonSetup("Save", 15, 445, 300,150, 75, true);
        saveButton.addActionListener(this);
        MainWindow.add(saveButton);

        // SENSOR SET POINTS

        deleteSetPoint = GUI.buttonSetup("Delete", 15, 675,35,125,75, true);
        deleteSetPoint.addActionListener(this);
        MainWindow.add(deleteSetPoint);

        addSetPoint = GUI.buttonSetup("Add", 15 ,825,35,125,75,true);
        addSetPoint.addActionListener(this);
        MainWindow.add(addSetPoint);

        editSetPoint = GUI.buttonSetup("Edit", 15, 750, 120, 125,75, true);
        editSetPoint.addActionListener(this);
        MainWindow.add(editSetPoint);

        setPointButtons[0] = GUI.buttonSetup("buttonOne", 25, 130, 225, 250, 125, true);
        setPointButtons[0].addActionListener(this);
        MainWindow.add(setPointButtons[0]);

        setPointButtons[1] = GUI.buttonSetup("buttonTwo", 25, 390, 225, 250, 125, true);
        setPointButtons[1].addActionListener(this);
        MainWindow.add(setPointButtons[1]);

        setPointButtons[2] = GUI.buttonSetup("buttonThree", 25, 650,225,250,125, true);
        setPointButtons[2].addActionListener(this);
        MainWindow.add(setPointButtons[2]);

        // DEBUGGING
        //disableStartMenu();
        disableMainMenu();
        disableAddSensor();
        disableSetPointMenu();
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
        backPage.setVisible(false);
        nextPage.setVisible(false);
        pageLabel.setVisible(false);
    }

    public void enableMainMenu(){
        topLabel.setVisible(true);
        topLabel.setText("Main Menu");
        backButton.setVisible(true);
        deleteButton.setVisible(true);
        addButton.setVisible(true);
        pageLabel.setVisible(true);

        if (sensors.size() == 0) {
            deleteButton.setEnabled(false);
            tryReadingMainMenuConfig();
        }

        pageNumber = 1;
        pageMax = (int) ceil((sensors.size()) / 3.0);
        pageLabel.setText("Page " + pageNumber + " of " + pageMax);

        if (pageMax == 0) {
            nextPage.setVisible(false);
            pageLabel.setVisible(false);
        } else if (pageMax != 1) {
            nextPage.setVisible(true);
        }

        if (sensors.size() != 0) {
            deleteButton.setEnabled(true);
            for (int i = 0; i < mainMenuButtons.length; i++){
                if (sensors.size() > i) {
                    mainMenuButtons[i].setVisible(true);
                    mainMenuButtons[i].setText(sensors.get(i * pageNumber));
                }
            }
        }


    }

    public void enableAddSensor(){
        topLabel.setVisible(true);
        topLabel.setText("Add Sensor");
        backButton.setVisible(true);
        sensorTextField.setVisible(true);
        saveButton.setVisible(true);
    }

    public void disableAddSensor(){
        topLabel.setVisible(false);
        backButton.setVisible(false);
        sensorTextField.setVisible(false);
        sensorTextField.setText("");
        saveButton.setVisible(false);
    }

    public void enableSetPointMenu(String sensorName) {
        topLabel.setVisible(true);
        topLabel.setText(sensorName + " Menu");
        backButton.setVisible(true);
        addSetPoint.setVisible(true);
        deleteSetPoint.setVisible(true);
        editSetPoint.setVisible(true);
        setPointButtons[0].setVisible(true);
        setPointButtons[1].setVisible(true);
        setPointButtons[2].setVisible(true);

        try {
            File sensorFile = new File(sensorName + "Data.txt");
            sensorFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void disableSetPointMenu() {
        topLabel.setVisible(false);
        sensorName = "";
        backButton.setVisible(false);
        deleteSetPoint.setVisible(false);
        addSetPoint.setVisible(false);
        editSetPoint.setVisible(false);
        setPointButtons[0].setVisible(false);
        setPointButtons[1].setVisible(false);
        setPointButtons[2].setVisible(false);
    }

    public void tryReadingMainMenuConfig(){
        try {
            File mainMenu = new File("MainMenuData.txt");
            mainMenu.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader("MainMenuData.txt"));
            String data;
            while ((data = br.readLine()) != null){
                sensors.add(data);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void tryWriteMainMenuConfig(){
        try {
            File mainMenu = new File("MainMenuData.txt");
            mainMenu.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter("MainMenuData.txt"));
            for (String sensor : sensors) {
                bw.write(sensor + "\n");
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

    public void deleteMode(int button) {
        for (int i = 0; i < sensors.size(); i++) {
            if (mainMenuButtons[button].getText().equals(sensors.get(i))){
                //noinspection SuspiciousListRemoveInLoop
                sensors.remove(i);
                tryWriteMainMenuConfig();
            }
        }

        for (int i = 0; i < mainMenuButtons.length; i++) {
            mainMenuButtons[i].setVisible(false);
            mainMenuButtons[i].setText(i + "");
        }

        if (sensors.size() != 0) {
            deleteButton.setEnabled(true);
            for (int i = 0; i < mainMenuButtons.length; i++){
                if (sensors.size() > (i + ((pageNumber - 1) * 3))) {
                    mainMenuButtons[i].setVisible(true);
                    mainMenuButtons[i].setText(sensors.get(i + ((pageNumber - 1) * 3)));
                }
            }
        }

        pageMax = (int) ceil((sensors.size()) / 3.0);
        pageLabel.setText("Page " + pageNumber + " of " + pageMax);

        if (pageNumber > pageMax) {
            backPageFunc();
            nextPage.setVisible(false);
        }

        if (pageMax == 1){
            nextPage.setVisible(false);
        }

        if (pageMax == 0) {
            pageLabel.setVisible(false);
        }

    }

    public void backPageFunc() {
        if (pageNumber > 1) {
            pageNumber--;
        }
        if (pageNumber == 1) {
            backPage.setVisible(false);
            nextPage.setVisible(false);
        }
        if (pageNumber < pageMax) {
            nextPage.setVisible(true);
        }

        pageLabel.setText("Page " + pageNumber + " of " + pageMax);

        if (sensors.size() != 0) {
            deleteButton.setEnabled(true);
            for (int i = 0; i < mainMenuButtons.length; i++){
                if (sensors.size() > (i + ((pageNumber - 1) * 3))) {
                    mainMenuButtons[i].setVisible(true);
                    mainMenuButtons[i].setText(sensors.get(i + ((pageNumber - 1) * 3)));
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {


        if (e.getSource() == addSetPoint){
            System.out.println(1);
        }

        if (e.getSource() == editSetPoint){
            System.out.println(2);
        }

        if (e.getSource() == deleteSetPoint) {
            System.out.println(3);
        }

        if (e.getSource() == deleteButton) {
            if (addButton.isEnabled()){
                isDeleteModeEnabled = true;
                topLabel.setText("Delete Mode: ON");
                addButton.setEnabled(false);
                backButton.setEnabled(false);
            } else if (!addButton.isEnabled()) {
                topLabel.setText("Main Menu");
                isDeleteModeEnabled = false;
                addButton.setEnabled(true);
                backButton.setEnabled(true);

                if (sensors.size() == 0) {
                    deleteButton.setEnabled(false);
                }

                pageMax = (int) ceil((sensors.size()) / 3.0);
                pageLabel.setText("Page " + pageNumber + " of " + pageMax);

                nextPage.setVisible(pageMax != 1 && pageMax != 0);
            }
        }

        if  (e.getSource() == backPage) {
            backPageFunc();
        }

        if (e.getSource() == nextPage) {
            if (pageNumber < pageMax) {
                pageNumber++;
            }

            if (pageNumber > 1) {
                backPage.setVisible(true);
            }
            if (pageNumber == pageMax) {
                nextPage.setVisible(false);
            }

            if (sensors.size() != 0) {
                mainMenuButtons[0].setVisible(false);
                mainMenuButtons[1].setVisible(false);
                mainMenuButtons[2].setVisible(false);
                deleteButton.setEnabled(true);
                for (int i = 0; i < mainMenuButtons.length; i++){
                    if (sensors.size() > (i + ((pageNumber - 1) * 3))) {
                        mainMenuButtons[i].setVisible(true);
                        mainMenuButtons[i].setText(sensors.get(i + ((pageNumber - 1) * 3)));
                    }
                }
            }

            pageLabel.setText("Page " + pageNumber + " of " + pageMax);
        }

        if (e.getSource() == mainMenuButtons[0]) {
            if (isDeleteModeEnabled) {
                deleteMode(0);
            } else {
                disableMainMenu();
                sensorName = mainMenuButtons[0].getText();
                enableSetPointMenu(sensorName);
            }
        }

        if (e.getSource() == mainMenuButtons[1]) {
            if (isDeleteModeEnabled) {
                deleteMode(1);
            } else {
                disableMainMenu();
                sensorName = mainMenuButtons[1].getText();
                enableSetPointMenu(sensorName);
            }
        }

        if (e.getSource() == mainMenuButtons[2]) {
            if (isDeleteModeEnabled) {
                deleteMode(2);
            } else {
                disableMainMenu();
                sensorName = mainMenuButtons[2].getText();
                enableSetPointMenu(sensorName);
            }
        }

        if (e.getSource() == addButton) {
            disableMainMenu();
            enableAddSensor();
        }

        if (e.getSource() == saveButton) {
            sensors.add(sensorTextField.getText());
            tryWriteMainMenuConfig();
            disableAddSensor();
            enableMainMenu();
        }

        if (e.getSource() == backButton) {
            if (topLabel.getText().equals("Main Menu")) {
                disableMainMenu();
                enableStartMenu();
            } else if (topLabel.getText().equals("Add Sensor")) {
                disableAddSensor();
                enableMainMenu();
            } else if (topLabel.getText().equals(sensorName + " Menu")) {
                disableSetPointMenu();
                enableMainMenu();
                sensorName = "";
            }
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
