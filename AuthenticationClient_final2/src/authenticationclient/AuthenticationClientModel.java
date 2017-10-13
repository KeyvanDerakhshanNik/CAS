package authenticationclient;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import static java.lang.Integer.parseInt;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Model Section of Client Panel
 *
 * @author Keyvan Derakhshan Nik
 */
public class AuthenticationClientModel {

    /**
     * define Server properties file to keep information about server
     */
    private final String configFile = "client.properties";
    /**
     * Keep Server IP
     */
    private String serverIp;
    /**
     * Keep client IP
     */
    private String clientIp;
    /**
     * Keep Server port
     */
    private int ServerPort;

    /**
     * Send user object which is saved as XML File and send to server
     */
    void send(User u) {
        getInfo();
        getIp();
        System.out.println(clientIp);
         u.setCommand("authenticate");
        try {
            Socket socket = new Socket(serverIp, ServerPort);
            System.out.println("Connecting to server ...");
            try {
                sendMarshaledData(socket, u);
                //read from socket
                Response serverMessage = (Response) unmarshallData(socket,"Response");
                JOptionPane.showMessageDialog(null, "Result is:" + serverMessage.getResult(), "InfoBox", JOptionPane.INFORMATION_MESSAGE);
            } catch (HeadlessException e) {
                JOptionPane.showMessageDialog(null, "Stream error", "ErrorBox", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Can not connect to server", "ErrorBox", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    /**
     * read Information from server.properties
     */
    public void getInfo() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(configFile));
            serverIp = p.getProperty("ServerIp");
            ServerPort = parseInt(p.getProperty("ServerPort"));
        } catch (IOException | NumberFormatException e) {
            System.out.println("there are some problems in getInfo part");
        }
    }

    /**
     * Obtain Client IP Address
     */
    public void getIp() {
        try {
            clientIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("there is a problem in getIp part");
        }
    }
 
    /**
     * Unmarshall data and return as response Object
     */
    public Object unmarshallData(Socket socket, String className) {
         try {
            final BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final StringReader dataReader = new StringReader(socketReader.readLine());
            switch (className) {
                case "Response": {
                    JAXBContext contect = JAXBContext.newInstance(Response.class);
                    Unmarshaller unmarshaller = contect.createUnmarshaller();
                    @SuppressWarnings("unchecked")
                    Response result = (Response) unmarshaller.unmarshal(dataReader);
                    return result;
                }
                case "User": {
                    JAXBContext contect = JAXBContext.newInstance(User.class);
                    Unmarshaller unmarshaller = contect.createUnmarshaller();
                    @SuppressWarnings("unchecked")
                    User result = (User) unmarshaller.unmarshal(dataReader);
                    return result;
                }
                default: {
                    return null;
                }
            }
        } catch (IOException | JAXBException ex) {
            return null;
        }
    }

      /**
     * send marshaled instance object over socket 
     */
    public void sendMarshaledData(Socket socket, Object objectInstance) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(objectInstance.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            final StringWriter dataRespondWriter = new StringWriter();
            marshaller.marshal(objectInstance, dataRespondWriter);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(dataRespondWriter.toString());
            out.newLine();
            out.flush();
        } catch (IOException | JAXBException ex) {
        }
    }
}
