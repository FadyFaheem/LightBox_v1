import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener {

    // START PAGE
    private JButton startButton;
    private JButton restartButton;
    private JButton connectButton;
    private JComboBox<String> portList;
    private JLabel logoPlaceHolder;
    static SerialPort ardAccess;

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
        startButton = GUI.buttonSetup("Start", 60, 700, 100, 250, 100, false); // Initates Button for setup and adds text
        startButton.addActionListener(this);
        MainWindow.add(startButton);

        restartButton = GUI.buttonSetup("Reset", 30, 750,400,150,75, true);
        restartButton.addActionListener(this);
        MainWindow.add(restartButton);

        portList = new JComboBox<>();
        portList.setBounds(700,215,250,75);
        MainWindow.add(portList);

        SerialPort[] portNames = SerialPort.getCommPorts();
        for (SerialPort portName : portNames) {
            if (portName.getSystemPortName().equals("COM3") || portName.getSystemPortName().equals("ttyUSB0") || portName.getSystemPortName().equals("ttyACM0")) {
                portList.addItem(portName.getSystemPortName());
            }
        }

        connectButton = GUI.buttonSetup("Connect", 30, 750, 305, 150, 75, (portList.getItemCount() != 0));
        connectButton.addActionListener(this);
        MainWindow.add(connectButton);





    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        Main bob = new Main();
        bob.setVisible(true);
    }

}
