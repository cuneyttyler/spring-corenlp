package com.gsu.knowledgebase.spring;

import com.gsu.knowledgebase.model.Login;
import com.gsu.knowledgebase.model.User;
import com.gsu.knowledgebase.repository.KnowledgeBaseDao;
import com.gsu.common.util.DateUtils;
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
    private KnowledgeBaseDao knowledgeBaseDao;

    public AuthenticationFailureHandler() {

    }


    public AuthenticationFailureHandler(KnowledgeBaseDao knowledgeBaseDao) {
        this.knowledgeBaseDao = knowledgeBaseDao;
    }

    public AuthenticationFailureHandler(String defaultFailureUrl, KnowledgeBaseDao knowledgeBaseDao) {
        super(defaultFailureUrl);
        this.knowledgeBaseDao = knowledgeBaseDao;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
    	onAuthFail(request, response, exception, knowledgeBaseDao, null, true, true);
    }
    
	/**
	 * An authentication failed
	 * @param request
	 * @param response
	 * @param exception
	 * @param knowledgeBaseDao
	 * @param user
	 * @throws java.io.IOException
	 * @throws javax.servlet.ServletException
	 */
	@SuppressWarnings("deprecation")
    public static void onAuthFail(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception, KnowledgeBaseDao knowledgeBaseDao,
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
            user = knowledgeBaseDao.findUserByEmail(username);
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
                		knowledgeBaseDao.updateUserLoginTryCount(user.getId(), tryCount + 1);
                	}
                	if (setResponse) {
                		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
                	}
                } else {
                	if (setResponse) {
                		response.sendError(HttpServletResponse.SC_CONFLICT, "Account is locked: ");
                	}
                }

                Login login = new Login();
                login.setUserId(user.getId());
                login.setSuccess(false);
                login.setCrDate(DateUtils.now());

                knowledgeBaseDao.saveLogin(login);
            }
        } else {
        	if (setResponse) {
        		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
        	}
        }
    }
    
}