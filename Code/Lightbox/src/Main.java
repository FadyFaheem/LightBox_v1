import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
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

        connectButton = GUI.buttonSetup("Connect", 20, 750, 305, 150, 75, (portList.getItemCount() != 0));
        connectButton.addActionListener(this);
        MainWindow.add(connectButton);

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

        if (ardAccess.isOpen()){ // Checks for connection before returning to main menu to ensure good connection
            connectButton.setText("Disconnect");
            portList.setEnabled(false);
            startButton.setEnabled(true);
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
            //arduinoWirte("on");
        }
    }

    public static void main(String[] args) {
        //Main bob = new Main();
        //bob.setVisible(true);

    }

}
