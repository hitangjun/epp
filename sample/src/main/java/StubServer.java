import com.verisign.epp.serverstub.Server;

/**
 * Created by hihexo on 2017/4/23.
 */
public class StubServer {
    public static void main(String[] args) {
        String configFile = "epp.config";
        new Server(configFile);
    }
}
