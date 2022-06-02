package com.gsu.semantic.spring;

import com.gsu.common.util.DateUtils;
import com.gsu.semantic.model.User;
import com.gsu.semantic.repository.SemanticGraphDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cnyt on 19.01.2015.
 */
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private SemanticGraphDao semanticGraphDao;

    public AuthenticationSuccessHandler() {

    }

    @Autowired
    public AuthenticationSuccessHandler(SemanticGraphDao semanticGraphDao) {
        this.semanticGraphDao = semanticGraphDao;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        onAuthSuccess(request, response, authentication, semanticGraphDao, null);
    }

    public static void onAuthSuccess(HttpServletRequest request, HttpServletResponse response,
                                     Authentication authentication, SemanticGraphDao semanticGraphDao, User user) {

        // This is actually not an error, but an OK message. It is sent to avoid redirects.
        System.out.println("On Success Handler");

        if (user == null) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            user = semanticGraphDao.findUserByUsername(username);
        }

        semanticGraphDao.updateUserLoginTryCount(user.getId(), 0);

        request.getSession().setAttribute("userId", user.getId());
        request.getSession().setAttribute("username", user.getUsername());
        request.getSession().setAttribute("email", user.getEmail());

        response.setStatus(HttpServletResponse.SC_OK);
    }

}