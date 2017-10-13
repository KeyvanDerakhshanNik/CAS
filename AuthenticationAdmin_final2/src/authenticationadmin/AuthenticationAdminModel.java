package authenticationadmin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Model Section of Admin Panel
 *
 * @author Keyvan Derakhshan Nik
 */
public class AuthenticationAdminModel {

    /**
     * Static Session Id to hold Session Id which obtain in transfer method and
     * used in send request
     */
    static String sessionId;
    /**
     * Static Socket s1 to keep available Socket which is created in transfer
     * method and use in send request on the same socket
     */
    static Socket s1;

    /**
     * Send userName and password on port of ip as a XML File which saveASXml
     * method saved and return the answer which transfer method returned
     */
    public boolean send(String ip, String port, String userName, String password) {
         User u=new User(userName,password,"authorize");
        try {
            final Socket socket = new Socket(ip, Integer.parseInt(port));
            s1 = socket;
            System.out.println("Connecting to server ...");
            try {
                //send message
                sendMarshaledData(socket,u);
                // read message
                Response serverMessage = (Response) unmarshallData(socket,"Response");
                if (!serverMessage.getResult().contains("username not found")) {
                    if (serverMessage.getResult().contains("permit")) {
                        return true;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Stream error", "ErrorBox", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can not connect to server", "ErrorBox", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

     /**
     * Send Requests for any thing which Admin required and return the result
     */
    public String sendRequest(String instruction) {
        Response req=new Response();
        req.setCommand(instruction);
        sendMarshaledData(s1,req);
        Response serverResponse = (Response) unmarshallData(s1,"Response");
        return serverResponse.getResult();
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
