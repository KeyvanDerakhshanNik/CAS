package authenticationadmin;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class Response {

    private String command;
    private String result;

    public Response() {
    }

    public String getCommand() {
        return command;
    }

    @XmlElement
    public void setCommand(String command) {
        this.command = command;
    }

    public String getResult() {
        return result;
    }

    @XmlElement
    public void setResult(String result) {
        this.result = result;
    }

}
