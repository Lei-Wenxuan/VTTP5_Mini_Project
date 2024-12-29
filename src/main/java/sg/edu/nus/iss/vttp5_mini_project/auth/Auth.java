package sg.edu.nus.iss.vttp5_mini_project.auth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.vttp5_mini_project.constant.Constants;
import sg.edu.nus.iss.vttp5_mini_project.utils.StaticPropertyHolder;

public class Auth {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final Collection<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/youtube.download",
            "https://www.googleapis.com/auth/youtube.readonly",
            "https://www.googleapis.com/auth/youtube",
            "https://www.googleapis.com/auth/youtube.force-ssl",
            "https://www.googleapis.com/auth/youtubepartner",
            "https://www.googleapis.com/auth/youtubepartner-channel-audit",
            "https://www.googleapis.com/auth/youtube.upload",
            "https://www.googleapis.com/auth/youtube.channel-memberships.creator",
            "https://www.googleapis.com/auth/youtube.third-party-link.creator");

    private static GoogleClientSecrets getSecrets() {
        InputStream is = new ByteArrayInputStream(StaticPropertyHolder.getStaticClientSecrets().getBytes());
        try {
            return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(is));
        } catch (IOException e) {
            System.err.println(">>> Error with loading google client secrets");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static GoogleAuthorizationCodeFlow buildCodeFlow(GoogleClientSecrets googleClientSecrets) {
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                googleClientSecrets,
                SCOPES).setAccessType("offline").build();
    }

    public static TokenResponse exchangeCode(String code) {
        GoogleClientSecrets googleClientSecrets = getSecrets();
        GoogleAuthorizationCodeFlow codeFlow = buildCodeFlow(googleClientSecrets);

        try {
            return codeFlow.newTokenRequest(code)
                    .setRedirectUri(googleClientSecrets.getDetails().getRedirectUris().get(0))
                    .execute();
        } catch (IOException e) {
            System.err.println(">>> Error with token exchange");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Credential getCredentialsFromTokenResponse(TokenResponse tokenResponse) {
        GoogleClientSecrets googleClientSecrets = getSecrets();
        GoogleAuthorizationCodeFlow codeFlow = buildCodeFlow(googleClientSecrets);

        try {
            return codeFlow.createAndStoreCredential(tokenResponse, "user");
        } catch (IOException e) {
            System.err.println(">>> Error with creating credentials");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Userinfo getUserinfoFromCredential(Credential credential) {
        Oauth2 oauth2 = new Oauth2.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                credential)
                .setApplicationName(Constants.USERINFO)
                .build();
        try {
            return oauth2.userinfo().get().execute();
        } catch (IOException e) {
            System.err.println(">>> Error with creating credentials");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String getAuthorisationUrl() {
        GoogleClientSecrets googleClientSecrets = getSecrets();
        GoogleAuthorizationCodeFlow codeFlow = buildCodeFlow(googleClientSecrets);

        return codeFlow.newAuthorizationUrl()
                .setRedirectUri(googleClientSecrets.getDetails().getRedirectUris().get(0))
                .build();
    }

    public static void saveCredentialsInSession(Credential credential, HttpSession session) {
        session.setAttribute(Constants.CREDENTIAL, credential);
    }

    public static Credential getCredentialsFromSession(HttpSession session) {
        return (Credential) session.getAttribute(Constants.CREDENTIAL);
    }

    public static void saveUserinfoInSession(Userinfo userinfo, HttpSession session) {
        session.setAttribute(Constants.USERINFO, userinfo);
    }

    public static Userinfo getUserinfoFromSession(HttpSession session) {
        return (Userinfo) session.getAttribute(Constants.USERINFO);
    }

}
