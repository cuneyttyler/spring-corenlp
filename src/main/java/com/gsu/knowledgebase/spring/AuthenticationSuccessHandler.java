package com.gsu.knowledgebase.spring;

import com.gsu.knowledgebase.model.Login;
import com.gsu.knowledgebase.model.User;
import com.gsu.knowledgebase.repository.KnowledgeBaseDao;
import com.gsu.common.util.DateUtils;
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

    private KnowledgeBaseDao knowledgeBaseDao;

    public AuthenticationSuccessHandler() {

    }

    @Autowired
    public AuthenticationSuccessHandler(KnowledgeBaseDao knowledgeBaseDao) {
        this.knowledgeBaseDao = knowledgeBaseDao;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        onAuthSuccess(request, response, authentication, knowledgeBaseDao, null);
    }

    public static void onAuthSuccess(HttpServletRequest request, HttpServletResponse response,
                                     Authentication authentication, KnowledgeBaseDao knowledgeBaseDao, User user) {

        // This is actually not an error, but an OK message. It is sent to avoid redirects.
        System.out.println("On Success Handler");

        if (user == null) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            user = knowledgeBaseDao.findUserByEmail(username);
        }

        knowledgeBaseDao.updateUserLoginTryCount(user.getId(), 0);

        Login login = new Login();
        login.setUserId(user.getId());
        login.setSuccess(true);
        login.setCrDate(DateUtils.now());

//        HttpSession session = request.getSession();
//        String referer = (String) session.getAttribute(Constants.CUSTOMER_REDIRECT_URL_AFTER_LOGIN_SESSION_KEY);

        knowledgeBaseDao.saveLogin(login);

        request.getSession().setAttribute("userId", user.getId());
        request.getSession().setAttribute("username", user.getUsername());
        request.getSession().setAttribute("email", user.getEmail());
        response.setStatus(HttpServletResponse.SC_OK);
    }

}