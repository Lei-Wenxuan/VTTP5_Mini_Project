package sg.edu.nus.iss.vttp5_mini_project.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StaticPropertyHolder {

    private static String CLIENT_SECRETS;

    @Value("${clientsecrets}")
    public void setStaticClientSecrets(String clientsecrets) {
        CLIENT_SECRETS = clientsecrets;
    }

    public static String getStaticClientSecrets() {
        return CLIENT_SECRETS;
    }

}
