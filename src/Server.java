import DataConverterExceptions.DataNotConvertedYetException;
import MapReaderExceptions.*;
import com.thoughtworks.xstream.XStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;

/**
 * Created by Łukasz on 2016-12-16.
 */
public class Server extends JFrame {
    private JTextField portTextField;
    private JButton runServerButton;
    private JButton openMapButton;
    private JPanel mainPanel;
    private JButton stopServerButton;
    private JLabel stateLabel;

    private Thread serverThread;
    private MapReader mapReader;
    private DataConverter dataConverter;
    private ServerSocket serverSocket;
    private ArrayList<Socket> sockets;
    private ArrayList<Thread> connectionThreads;
    private ObjectOutputStream outputStream;

    private boolean isServerRunning = false;
    private boolean isFileOpened = false;


    Server(){
        setContentPane(mainPanel);
        setResizable(false);
        setTitle("MapGraph - Server");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createListeners();
        mapReader = new MapReader();
        sockets = new ArrayList<>();
        connectionThreads = new ArrayList<>();
    }

    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new Server();
        frame.pack();
        frame.setVisible(true);

    }

    @Override
    public void dispose() {
        if(isServerRunning){
            isServerRunning = false;
            try {
                serverSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            for(Socket s: sockets){
                try {
                    s.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            for(Thread t: connectionThreads){
                try {
                    t.join();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            try{
                serverThread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        super.dispose();
    }

    private void createListeners(){
        runServerButton.addActionListener(new RunServerActionListener());
        stopServerButton.addActionListener(new StopServerActionListener());
        openMapButton.addActionListener(new OpenMapActionListener());
    }

    private class RunServerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                EventQueue.invokeLater(new RunServer(Integer.parseInt(portTextField.getText())));
            }catch (NumberFormatException exc){
                JOptionPane.showMessageDialog(mainPanel, "Wrong format of the port number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RunServer implements Runnable {
        private int port;
        RunServer(int port){
            this.port = port;
        }
        @Override
        public void run() {
            if(isFileOpened && !isServerRunning){
                try {
                    serverSocket = new ServerSocket(Integer.parseInt(portTextField.getText()));
                    serverThread = new Thread(new ServerTask());
                    isServerRunning = true;
                    serverThread.start();
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    runServerButton.setEnabled(false);
                    stopServerButton.setEnabled(false);
                }
            }
            else if(!isFileOpened){
                JOptionPane.showMessageDialog(mainPanel, "Open file first");
            }
        }
    }

    private class ServerTask implements Runnable{
        @Override
        public void run() {
            Socket s;
            ObjectInputStream inputStream;
            ObjectOutputStream outputStream;
            Thread newThread;
            while(isServerRunning){
                try {
                    s = serverSocket.accept();
                    System.out.println("sb has established the connection");
                    outputStream = new ObjectOutputStream(s.getOutputStream());
                    inputStream = new ObjectInputStream(s.getInputStream());
                    sockets.add(s);
                    newThread = new Thread(new ConnectionTask(inputStream, outputStream));
                    connectionThreads.add(newThread);
                    newThread.start();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
    }

    private class ConnectionTask implements Runnable{
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        ConnectionTask(ObjectInputStream is, ObjectOutputStream oos){
            inputStream = is;
            outputStream = oos;
        }

        @Override
        public void run() {
            String input;
            XStream xstream = new XStream();
            xstream.setMode(XStream.ID_REFERENCES);
            xstream.alias("node", Node.class);
            xstream.alias("hashMap", HashMap.class);
            try {
                String output = xstream.toXML(dataConverter.returnConvertedNodes());

                    while (isServerRunning && (input = (String) inputStream.readObject()) != null) {
                        if (input.equals("SEND")) {
                            outputStream.writeObject(output);
                        }
                    }
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (DataNotConvertedYetException e){
                JOptionPane.showMessageDialog(mainPanel, "Nothing has been processed!");
            }catch (IOException | ClassNotFoundException e){
                JOptionPane.showMessageDialog(mainPanel, "Something's wrong with streams");
            }
        }
    }

    private class StopServerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            EventQueue.invokeLater(new StopServer());
        }
    }

    private class StopServer implements Runnable{
        @Override
        public void run() {
            try{
                isServerRunning = false;
                for(Thread t: connectionThreads){
                    t.join();
                }
                connectionThreads.clear();
                for(Socket s: sockets){
                    s.close();
                }
                sockets.clear();
                serverSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            } finally {
                serverSocket = null;
                runServerButton.setEnabled(true);
                stopServerButton.setEnabled(false);
            }
        }
    }

    private class OpenMapActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            EventQueue.invokeLater(new OpenMap());
        }
    }

    private class OpenMap implements Runnable{
        @Override
        public void run() {
            JFileChooser fc = new JFileChooser("~");
            if(fc.showDialog(mainPanel, "Open file...")==JFileChooser.APPROVE_OPTION) {
                try {
                    mapReader.openFile(fc.getSelectedFile().getPath());
                    mapReader.runReader();
                    dataConverter = new DataConverter(mapReader.getNodes(), mapReader.getWays());
                    dataConverter.runConverter();
                    isFileOpened = true;
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel, "File not found!");
                    isFileOpened = false;
                }catch (BoundsAlreadyReadException | NodesAlreadyReadException | NodesNotReadYetException | WaysAlreadyReadException | WaysNotReadYetException e){
                    JOptionPane.showMessageDialog(mainPanel, "There's a problem with map file!");
                    isFileOpened = false;
                }
            }
        }
    }
}
