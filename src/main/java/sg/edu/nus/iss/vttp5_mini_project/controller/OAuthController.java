package sg.edu.nus.iss.vttp5_mini_project.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.oauth2.model.Userinfo;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.vttp5_mini_project.auth.Auth;
import sg.edu.nus.iss.vttp5_mini_project.constant.Constants;

@RestController
public class OAuthController {

    @GetMapping("/authorise")
    public RedirectView authorise() {
        String viewUrl = Auth.getAuthorisationUrl();
        return new RedirectView(viewUrl);
    }

    @GetMapping("/Callback")
    public ModelAndView callbackString(@RequestParam("code") String code, HttpSession session) throws IOException {
        ModelAndView mav = new ModelAndView("redirect:");

        TokenResponse tokenResponse = Auth.exchangeCode(code);

        Credential credential = Auth.getCredentialsFromTokenResponse(tokenResponse);
        Auth.saveCredentialsInSession(credential, session);

        Userinfo userinfo = Auth.getUserinfoFromCredential(credential);
        Auth.saveUserinfoInSession(userinfo, session);

        mav.addObject(Constants.CREDENTIAL, credential);

        return mav;
    }

}
