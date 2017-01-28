import WayFinderExceptions.GraphNotReadYetException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MainView extends JFrame {
    private JPanel contentPane;
    private JButton openFileButton;
    private JButton saveImageButton;
    private JButton findWayButton;
    private JButton saveWayTextButton;
    private JRadioButton startCoordinates;
    private JRadioButton goalCoordinates;
    private JFormattedTextField startLatitudeField;
    private JFormattedTextField goalLatitudeField;
    private JFormattedTextField startLongitudeField;
    private JFormattedTextField goalLongitudeField;
    private JRadioButton startId;
    private JRadioButton goalId;
    private JFormattedTextField startIdField;
    private JFormattedTextField goalIdField;
    private JButton checkStartCoordinates;
    private JButton checkGoalCoordinates;
    private JButton checkStartId;
    private JButton checkGoalId;
    private JRadioButton dijkstraButton;
    private JRadioButton aStarButton;
    private JFormattedTextField latitudeClosestNodeTextField;
    private JFormattedTextField longitudeClosestNodeTextFiel;
    private JButton findTheClosestNodeButton;
    private Boolean dijkstraAlg = true;
    private Boolean isFileOpen = false;

    private GraphReader graphReader = new GraphReader();
    private WayFinder wayFinder = null;
    private HashMap<String, Node> graph = null;
    private ArrayList<Node> shortestWay = null;
    private Double[] bounds = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};

    public MainView() {
        setContentPane(contentPane);
        setResizable(false);
        setTitle("WayFinder");
        startCoordinates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startLatitudeField.setEditable(true);
                startLongitudeField.setEditable(true);
                checkStartCoordinates.setEnabled(true);
                startId.setSelected(false);
                startIdField.setEditable(false);
                checkStartId.setEnabled(false);
                startCoordinates.setSelected(true);
            }
        });
        startId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startLatitudeField.setEditable(false);
                startLongitudeField.setEditable(false);
                checkStartCoordinates.setEnabled(false);
                startCoordinates.setSelected(false);
                startIdField.setEditable(true);
                checkStartId.setEnabled(true);
                startId.setSelected(true);
            }
        });
        goalCoordinates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goalLatitudeField.setEditable(true);
                goalLongitudeField.setEditable(true);
                checkGoalCoordinates.setEnabled(true);
                goalId.setSelected(false);
                goalIdField.setEditable(false);
                checkGoalId.setEnabled(false);
                goalCoordinates.setSelected(true);
            }
        });
        goalId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goalLatitudeField.setEditable(false);
                goalLongitudeField.setEditable(false);
                checkGoalCoordinates.setEnabled(false);
                goalCoordinates.setSelected(false);
                goalIdField.setEditable(true);
                checkGoalId.setEnabled(true);
                goalId.setSelected(true);
            }
        });
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shortestWay = null;
                final JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Open file...");
                final int returnedValue = fc.showOpenDialog(contentPane);
                if(returnedValue == JFileChooser.APPROVE_OPTION){
                    final File file = fc.getSelectedFile();
                    try {
                        graphReader.openFile(file.getPath());
                        graphReader.readGraph();
                        isFileOpen = true;
                        graph = graphReader.returnGraph();
                        wayFinder  = new WayFinder(graph);
                    }catch(FileNotFoundException exc){
                        JOptionPane.showMessageDialog(contentPane, "Error: cannot open the file!");
                        isFileOpen = false;
                    }catch(GraphNotReadYetException exc){
                        JOptionPane.showMessageDialog(contentPane, "Error: something is \"no yes\" with graph reading");
                        isFileOpen = false;
                    }
                }
            }
        });
        findWayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(wayFinder == null){
                    JOptionPane.showMessageDialog(contentPane, "You did not open any file!");
                }
                else{
                    int startErrorNumber = 0;
                    int goalErrorNumber = 0;
                    double latitude = 0, longitude = 0;
                    String idStart = null;
                    String idGoal = null;
                    if(startCoordinates.isSelected()){
                        String lat, lon;
                        lat = startLatitudeField.getText();
                        if(lat.matches("[0-9]+[.]?[0-9]*")){
                            latitude = Double.parseDouble(lat);
                        }
                        else{
                            startErrorNumber +=1;
                        }
                        lon = startLongitudeField.getText();
                        if(lon.matches("[0-9]+[.]?[0-9]*")){
                            longitude = Double.parseDouble(lon);
                        }
                        else{
                            startErrorNumber +=2;
                        }
                        if(startErrorNumber == 0 && graph.values().contains(new Node(latitude, longitude))){
                            Node tmp = new Node(latitude, longitude);
                            for(Node n: graph.values()){
                                if( n.equals(tmp)){
                                    idStart = n.getId();
                                    break;
                                }
                            }
                        }
                        else{
                            startErrorNumber +=4;
                        }
                    }else if(startId.isSelected()){
                        String id = startIdField.getText();
                        if(graph.keySet().contains(id)){
                            idStart = id;
                        }
                        else{
                            startErrorNumber -=1;
                        }
                    }
                    else{
                        startErrorNumber = -2;
                    }
                    if(goalCoordinates.isSelected()){
                        String lat, lon;
                        lat = goalLatitudeField.getText();
                        if(lat.matches("-?[0-9]+[.]?[0-9]*")){
                            latitude = Double.parseDouble(lat);
                        }
                        else{
                            goalErrorNumber +=1;
                        }
                        lon = goalLongitudeField.getText();
                        if(lon.matches("-?[0-9]+[.]?[0-9]*")){
                            longitude = Double.parseDouble(lon);
                        }
                        else{
                            goalErrorNumber +=2;
                        }
                        if(goalErrorNumber == 0 && graph.values().contains(new Node(latitude, longitude))){
                            Node tmp = new Node(latitude, longitude);
                            for(Node n: graph.values()){
                                if( n.equals(tmp)){
                                    idGoal = n.getId();
                                    break;
                                }
                            }
                        }
                        else{
                            goalErrorNumber +=4;
                        }
                    }else if(goalId.isSelected()){
                        String id = goalIdField.getText();
                        if(graph.keySet().contains(id)){
                            idGoal = id;
                        }
                        else{
                            goalErrorNumber -=1;
                        }
                    }
                    else{
                        goalErrorNumber = -2;
                    }
                    if(idGoal == null || idStart == null){
                        if(startErrorNumber%2 == 1){
                            JOptionPane.showMessageDialog(contentPane, "Incorrect format of latitude (start)");
                        }
                        if(startErrorNumber == 2 || startErrorNumber == 3 || startErrorNumber == 6 || startErrorNumber == 7){
                            JOptionPane.showMessageDialog(contentPane, "Incorrect format of longitude (start)");
                        }
                        if((startErrorNumber >=4 && startErrorNumber <= 7) || startErrorNumber == -1){
                            JOptionPane.showMessageDialog(contentPane, "There's no such a Node (start)");
                        }
                        if (startErrorNumber == -2) {
                            JOptionPane.showMessageDialog(contentPane, "Choose one option of Node choosing (start)");
                        }


                        if(goalErrorNumber%2 == 1){
                            JOptionPane.showMessageDialog(contentPane, "Incorrect format of latitude (goal)");
                        }
                        if(goalErrorNumber == 2 || goalErrorNumber == 3 || goalErrorNumber == 6 || goalErrorNumber == 7){
                            JOptionPane.showMessageDialog(contentPane, "Incorrect format of longitude (goal)");
                        }
                        if((goalErrorNumber >=4 && goalErrorNumber <= 7) || goalErrorNumber == -1){
                            JOptionPane.showMessageDialog(contentPane, "There's no such a Node (goal)");
                        }
                        if(goalErrorNumber == -2){
                            JOptionPane.showMessageDialog(contentPane, "Choose one option of Node choosing (goal)");
                        }
                    }
                    else{
                        if(dijkstraAlg) {
                            wayFinder.runFinderDijkstra(idStart, idGoal);
                        }
                        else{
                            wayFinder.runFinderAStar(idStart, idGoal);
                        }
                        shortestWay = wayFinder.getFoundWay();
                        if(shortestWay == null){
                            JOptionPane.showMessageDialog(contentPane, "This way does not exist!");
                        }
                    }
                }
            }
        });
        saveWayTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(shortestWay != null) {
                    double tst = 0;
                    Node tmp = null;
                    JFileChooser fc = new JFileChooser();
                    fc.setDialogTitle("Save way as text...");
                    final int returnedValue = fc.showSaveDialog(contentPane);
                    if (returnedValue == JFileChooser.APPROVE_OPTION) {
                        final File file = fc.getSelectedFile();
                        try {
                            PrintWriter output = new PrintWriter(file);
                            for (Node n : shortestWay){
                                output.println(n);
                                if(tmp!=null){
                                    tst += n.distance(tmp);
                                }
                                tmp = n;
                            }
                            output.println(tst);
                            output.close();
                        } catch (Exception exc) {
                            JOptionPane.showMessageDialog(contentPane, "Cannot save file");
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(contentPane, "There is nothing to save");
                }
            }
        });
        saveImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (shortestWay != null && graph != null) {
                    double lat, lon;
                    for (Node n : graph.values()) {
                        lat = n.getLatitude();
                        lon = n.getLongitude();
                        if (lat < bounds[0])
                            bounds[0] = lat;
                        if (lat > bounds[2])
                            bounds[2] = lat;
                        if (lon < bounds[1])
                            bounds[1] = lon;
                        if (lon > bounds[3])
                            bounds[3] = lon;
                    }
                    int width = (int) Math.round(800 * (bounds[3] - bounds[1]) / (bounds[2] - bounds[0]));
                    ;
                    int height = 800;
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D grph = image.createGraphics();
                    grph.setColor(Color.WHITE);
                    grph.fillRect(0, 0, width, height);
                    grph.setColor(Color.BLACK);
                    int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
                    for (Node n : graph.values()) {
                        x1 = (int) Math.round(width * (n.getLongitude() - bounds[1]) / (bounds[3] - bounds[1]));
                        y1 = (int) Math.round(height - height * (n.getLatitude() - bounds[0]) / (bounds[2] - bounds[0]));
                        for (Node n2 : n.getEdges()) {
                            x2 = (int) Math.round(width * (n2.getLongitude() - bounds[1]) / (bounds[3] - bounds[1]));
                            y2 = (int) Math.round(height - height * (n2.getLatitude() - bounds[0]) / (bounds[2] - bounds[0]));
                            //if(!shortestWay.contains(n) && !shortestWay.contains(n2))
                            grph.drawLine(x1, y1, x2, y2);
                        }
                        grph.setColor(Color.RED);
                        grph.fillRect(x1 - 1, y1 - 1, 2, 2);
                        grph.setColor(Color.BLACK);
                    }
                    grph.setColor(Color.GREEN);
                    grph.setStroke(new BasicStroke(10));
                    Node n2 = null;
                    for (Node n : shortestWay) {
                        if (n2 != null) {
                            x1 = (int) Math.round(width * (n.getLongitude() - bounds[1]) / (bounds[3] - bounds[1]));
                            y1 = (int) Math.round(height - height * (n.getLatitude() - bounds[0]) / (bounds[2] - bounds[0]));
                            x2 = (int) Math.round(width * (n2.getLongitude() - bounds[1]) / (bounds[3] - bounds[1]));
                            y2 = (int) Math.round(height - height * (n2.getLatitude() - bounds[0]) / (bounds[2] - bounds[0]));
                            grph.drawLine(x1, y1, x2, y2);
                            grph.setColor(Color.BLUE);
                            grph.fillRect(x1 - 2, y1 - 2, 4, 4);
                            grph.fillRect(x2 - 2, y2 - 2, 4, 4);
                            grph.setColor(Color.GREEN);
                        }
                        n2 = n;
                    }
                    JFileChooser fc = new JFileChooser();
                    fc.setDialogTitle("Saving image...");
                    final int returnedValue = fc.showSaveDialog(contentPane);
                    if (returnedValue == JFileChooser.APPROVE_OPTION) {
                        try {
                            File file = fc.getSelectedFile();
                            ImageIO.write(image, "png", file);
                        } catch (IOException exc) {
                            JOptionPane.showMessageDialog(contentPane, "Cannot save file");
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(contentPane, "There is nothing to save");
                }
            }

        });
        checkStartCoordinates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lat, lon;
                boolean areFieldOK = true;
                lat = startLatitudeField.getText();
                lon = startLongitudeField.getText();
                if(lat.matches("-?[0-9]+[.]?[0-9]*") && lon.matches("-?[0-9]+[.]?[0-9]*")){
                    if(graph.containsValue(new Node(Double.parseDouble(lat), Double.parseDouble(lon) ))){
                        JOptionPane.showMessageDialog(contentPane, "Such a Node exists");
                    }
                    else{
                        JOptionPane.showMessageDialog(contentPane, "Such a Node does not exist");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(contentPane, "Format of Coordinates is incorrect");
                }
            }
        });
        checkGoalCoordinates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lat, lon;
                boolean areFieldOK = true;
                lat = goalLatitudeField.getText();
                lon = goalLongitudeField.getText();
                if(lat.matches("-?[0-9]+[.]?[0-9]*") && lon.matches("-?[0-9]+[.]?[0-9]*")){
                    if(graph.containsValue(new Node(Double.parseDouble(lat), Double.parseDouble(lon) ))){
                        JOptionPane.showMessageDialog(contentPane, "Such a Node exists");
                    }
                    else{
                        JOptionPane.showMessageDialog(contentPane, "Such a Node does not exist");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(contentPane, "Format of Coordinates is incorrect");
                }
            }
        });

        checkStartId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = startIdField.getText();
                if(graph.containsKey(id)){
                    JOptionPane.showMessageDialog(contentPane, "Such a Node exists");
                }
                else{
                    JOptionPane.showMessageDialog(contentPane, "Such a Node does not exist");
                }
            }
        });
        checkGoalId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = goalIdField.getText();
                if(graph.containsKey(id)){
                    JOptionPane.showMessageDialog(contentPane, "Such a Node exists");
                }
                else{
                    JOptionPane.showMessageDialog(contentPane, "Such a Node does not exist");
                }
            }
        });
        dijkstraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aStarButton.setSelected(false);
                dijkstraAlg = true;
                dijkstraButton.setSelected(true);
            }
        });
        aStarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dijkstraButton.setSelected(false);
                dijkstraAlg = false;
                aStarButton.setSelected(true);
            }
        });
        findTheClosestNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(graph!= null) {
                    String lat = latitudeClosestNodeTextField.getText();
                    String lon = longitudeClosestNodeTextFiel.getText();
                    double dst = Double.MAX_VALUE;
                    Node theClosest = null;
                    if (lat.matches("-?[0-9]+[.]?[0-9]*") && lon.matches("-?[0-9]+[.]?[0-9]*")) {
                        Node tmp = new Node(Double.parseDouble(lat), Double.parseDouble(lon));
                        for (Node n :graph.values()){
                            if(n.distance(tmp) < dst){
                                dst = n.distance(tmp);
                                theClosest = n;
                            }
                        }
                    }
                    if(theClosest!=null) {
                        JOptionPane.showMessageDialog(contentPane, "The closest Node is " + theClosest.getId());
                    }
                }
                else{
                    JOptionPane.showMessageDialog(contentPane, "The file have not been read yet");
                }
            }
        });
    }

    public static void main(String[] args) {
        MainView mainView = new MainView();
        try{
            mainView.deserialize();
        }catch (Exception e){
            System.out.println("STH IS NO YES");
        }
        mainView.pack();
        mainView.setVisible(true);
        try {
            mainView.serialize();
        }catch (Exception e){
            System.out.println("STH IS NO YES");
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void serialize() throws IOException{
        FileOutputStream fos = new FileOutputStream("./data");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(this.graphReader);
        objects.add(this.wayFinder);
        objects.add(this.graph);
        objects.add(this.shortestWay);
        objects.add(bounds);
        oos.writeObject(objects);
        oos.close();
        fos.close();
    }

    public void deserialize() throws IOException, ClassNotFoundException{
        FileInputStream fis = new FileInputStream("./data");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<Object> objects = (ArrayList<Object>) ois.readObject();
        this.graphReader = (GraphReader) objects.remove(0);
        this.wayFinder = (WayFinder) objects.remove(0);
        this.graph = (HashMap<String, Node>) objects.remove(0);
        this.shortestWay = (ArrayList<Node>) objects.remove(0);
        this.bounds = (Double[]) objects.remove(0);
        ois.close();
        fis.close();
    }
}
