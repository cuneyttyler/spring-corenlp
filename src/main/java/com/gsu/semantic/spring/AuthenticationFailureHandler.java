package com.gsu.semantic.spring;

import com.gsu.common.util.DateUtils;
import com.gsu.semantic.model.User;
import com.gsu.semantic.repository.SemanticGraphDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bahadirt@showroomist.co on 19.01.2015.
 */
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private SemanticGraphDao semanticGraphDao;

    public AuthenticationFailureHandler() {

    }

    public AuthenticationFailureHandler(SemanticGraphDao semanticGraphDao) {
        this.semanticGraphDao = semanticGraphDao;
    }

    public AuthenticationFailureHandler(String defaultFailureUrl, SemanticGraphDao semanticGraphDao) {
        super(defaultFailureUrl);
        this.semanticGraphDao = semanticGraphDao;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
    	onAuthFail(request, response, exception, semanticGraphDao, null, true, true);
    }
    
	/**
	 * An authentication failed
	 * @param request
	 * @param response
	 * @param exception
	 * @param user
	 * @throws IOException
	 * @throws ServletException
	 */
	@SuppressWarnings("deprecation")
    public static void onAuthFail(HttpServletRequest request, HttpServletResponse response,
                                  AuthenticationException exception, SemanticGraphDao semanticGraphDao,
                                  User user, boolean updateTryCount, boolean setResponse) throws IOException, ServletException {
		//On fail, set captcha required

    	if (exception == null
                || exception.getAuthentication() == null
                || exception.getAuthentication().getPrincipal() == null) {
            System.out.println("Login failure null");
            return;
        }
    	
    	if (user == null) {
    		String username = (String) exception.getAuthentication().getPrincipal();
            user = semanticGraphDao.findUserByUsername(username);
    	}
        
        if (user != null) {
        	int tryCount = user.getLoginTryCount() == null ? 0 : user.getLoginTryCount();

            if (tryCount >= 3) {
            	if (setResponse) {
            		response.sendError(HttpServletResponse.SC_GONE, "Account is locked: ");
            	}
            } else {
                // Hesap onaylanmamıssa try count'u arttırma
                if (user.getStatus() != 0) {
                	if (updateTryCount) {
                		semanticGraphDao.updateUserLoginTryCount(user.getId(), tryCount + 1);
                	}
                	if (setResponse) {
                		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
                	}
                } else {
                	if (setResponse) {
                		response.sendError(HttpServletResponse.SC_CONFLICT, "Account is locked: ");
                	}
                }
            }
        } else {
        	if (setResponse) {
        		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
        	}
        }
    }
    
}