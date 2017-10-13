package authenticationserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import static java.lang.Integer.parseInt;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Control Section of Server
 *
 * @author Keyvan Derakhshan Nik
 */
public class AuthenticationServerControl {

    /**
     * Counter for Number of Client which are connected
     */
    private static int clientCounter = 0;
    /**
     * define Server properties file to keep information about server
     */
    private final String configFile = "server.properties";
    /**
     * Keep Server IP
     */
    private String serverIp;
    /**
     * Keep Server port
     */
    private int serverPort;
    /**
     * Keep timeout
     */
    private int timeOut;
    /**
     * Keep Date Information
     */
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * An Array of Sessions which are created
     */
    static ArrayList<Session> list = new ArrayList<Session>();

    /**
     * read Information from server.properties
     */
    public void getInfo() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(configFile));
            setServerIp(p.getProperty("ServerIp"));
            setServerPort(parseInt(p.getProperty("ServerPort")));
            setTimeOut(parseInt(p.getProperty("TimeOut")));
        } catch (IOException | NumberFormatException e) {
            System.out.println("There is a problem in getInfo part");
        }
    }

    /**
     * getter method for ServerIP
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * setter method for ServerIP
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * getter method for ServerPort
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * setter method for ServerPort
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * getter method for TimeOut
     */
    public int getTimeOut() {
        return timeOut;
    }

    /**
     * setter method for TimeOut
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * Listen to received request from client or admin panel
     */
    public void listen() {
        getInfo();
        try {
            ServerSocket listener = new ServerSocket(getServerPort());
            System.out.println("Server is waiting for requests...");
            while (true) {
                Socket socket = listener.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.out.println("There is a problem in Listen part and socket");
        }
    }

    /**
     * Create Thread Handler for Client and Admin application
     */
    private class ClientHandler extends Thread {
        private final Socket incoming;
        private String userName;
        private String password;
        private String command;
        private String instruction;

        /**
         * Constructor take and set Socket
         */
        ClientHandler(Socket incoming) {
            this.incoming = incoming;
        }

        /**
         * Run this Method for Each Thread
         */
        @Override
        @SuppressWarnings("empty-statement")
        public void run() {
            clientCounter++;
            try {
                System.out.println("Number of live clients:" + clientCounter);
                //read the incomming String
                User clientMessage = (User) unmarshallData(incoming, "User");
                switch (clientMessage.getCommand()) {
                    case "authenticate":
                        //extract userName and Password and set them in their variable
                        if (new AuthenticationServerModel().checkUsernamePassword(clientMessage.getUserNameString(), clientMessage.getPasswordString())) {
                            log(clientMessage.getUserNameString() + " Logged in From " + incoming.getInetAddress() + " in " + dateFormat.format(new Date()) + "  " + System.currentTimeMillis() / 60000);
                            Session profile = createSession(clientMessage.getUserNameString());
                            //write first Response
                            Response res = new Response();
                            res.setCommand("authenticated");
                            res.setResult(profile.getSessionId());
                            sendMarshaledData(incoming, res);
                            //Wait for a reach minutes to timeout minutes
                            while ((System.currentTimeMillis() / 60000 - profile.getLastReferenceTime() < getTimeOut()));
                            for (Session s : list) {
                                synchronized (list) {
                                    if (new Date().getTime() / 60000 - s.getLastReferenceTime() >= getTimeOut()) {
                                        try {
                                            list.remove(s);
                                        } catch (Exception e) {
                                            System.out.println("There is no Array List to remove");
                                        }
                                        clientCounter--;
                                        log(s.getUserName() + " Logged out " + "in " + dateFormat.format(new Date()) + "  " + System.currentTimeMillis() / 60000);
                                    }
                                }
                            }
                        }
                        break;
                    case "authorize":
                        //exteract userName and Password and set them in their variable
                        Response res = new Response();
                        Session profile = createSession(clientMessage.getUserNameString());
                        if (new AuthenticationServerModel().checkUsernamePassword(clientMessage.getUserNameString(), clientMessage.getPasswordString())) {
                            //Read and check the Session ID
                            if (profile.getRole().contains("admin")) {
                                res.setResult("permit");
                                sendMarshaledData(incoming, res);
                                System.out.println("It is permitted");
                                doIt(incoming);
                            } else {
                                res.setResult("unpermit");
                                sendMarshaledData(incoming, res);
                                System.out.println("It is not permitted");
                            }
                        } else {
                            res.setResult("username not found");
                            sendMarshaledData(incoming, res);
                        }
                        incoming.close();
                        break;
                    default:
                        System.out.println("Error in received XML file!!!");
                        break;
                }
            } catch (IOException e) {
                System.out.println("I can not Read or Write on Socket");
            }
        }

        /**
         * Extract special answer between special tags
         */
        String extract(String s, String tag) {
            String[] part1 = s.split("<" + tag + ">");
            String[] part2 = part1[1].split("</" + tag + ">");
            return (part2[0]);
        }

        /**
         * Create Session for Individual user if it does not have Session
         */
        private Session createSession(String u) {
            for (Session s : list) {
                if (s.getUserName().equals(u)) {
                    s.setLastReferenceTime(new Date().getTime() / 60000);
                    return s;
                }
            }
            String roles;
            String sId = UUID.randomUUID().toString();
            for (Session s : list) {
                if (s.getSessionId().equals(sId)) {
                    sId = UUID.randomUUID().toString();
                }
            }
            //give Roles of the User
            roles = new AuthenticationServerModel().findRoles(u);
            Session ySession = new Session(sId, u, roles, new Date().getTime() / 60000);
            synchronized (list) {
                list.add(ySession);
            }
            return ySession;
        }

        /**
         * Take Log for String . it is worked when Session Created and Removed
         */
        public void log(String s) {
            try (PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter("server.log", true)))) {
                p.println(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * just try to read request and write response for admin request
         */
        void doIt(Socket s) {
            String temp = new String();
            try {
                Response adminRequest = (Response) unmarshallData(s, "Response");
                setInstruction(adminRequest.getCommand());
                System.out.println("I am Doing " + instruction);
                while (!instruction.equals("close")) {
                    if (instruction.contains("newUser")) {
                        String u = extract(instruction, "newUser");
                        String p = extract(instruction, "newPassword");
                        String r = extract(instruction, "newRole");
                        temp = new AuthenticationServerModel().addUser(u, p, r);
                    } else if (instruction.contains("onLineUsers")) {
                        StringBuilder onLineUsers = new StringBuilder();
                        synchronized (list) {
                            for (Session s1 : list) {
                                onLineUsers.append(s1.getUserName() + "\t");
                            }
                        }
                        temp = onLineUsers.toString();
                    } else if (instruction.contains("serverLog")) {
                        String line;
                        StringBuilder serverLog = new StringBuilder();
                        BufferedReader logReader = new BufferedReader(new FileReader("server.log"));
                        while ((line = logReader.readLine()) != null) {
                            serverLog.append(line + "\t");
                        }
                        temp = serverLog.toString();
                    } else if (instruction.contains("serverInfo")) {
                        StringBuilder serverInfo = new StringBuilder();
                        serverInfo.append("Available processors (cores): " + Runtime.getRuntime().availableProcessors() + "\t");
                        serverInfo.append("Free memory (bytes): " + Runtime.getRuntime().freeMemory() + "\t");
                        serverInfo.append("Total memory available to JVM (bytes): " + Runtime.getRuntime().totalMemory() + "\t");
                        temp = serverInfo.toString();
                    }
                    adminRequest.setResult(temp);
                    sendMarshaledData(s, adminRequest);
                    adminRequest = (Response) unmarshallData(s, "Response");
                    setInstruction(adminRequest.getCommand());
                }
                adminRequest.setResult("");
                sendMarshaledData(s, adminRequest);
                System.out.println("Admin logged out");
            } catch (IOException ex) {
                Logger.getLogger(AuthenticationServerControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * getter method for UserName
         */
        public String getUserName() {
            return userName;
        }

        /**
         * setter method for UserName
         */
        public void setUserName(String userName) {
            this.userName = userName;
        }

        /**
         * getter method for Password
         */
        public String getPassword() {
            return password;
        }

        /**
         * setter method for password
         */
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * getter method for command
         */
        public String getCommand() {
            return command;
        }

        /**
         * setter method for command
         */
        public void setCommand(String command) {
            this.command = command;
        }

        /**
         * getter method for Instruction
         */
        public String getInstruction() {
            return instruction;
        }

        /**
         * setter method for Instruction
         */
        public void setInstruction(String instruction) {
            this.instruction = instruction;
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
