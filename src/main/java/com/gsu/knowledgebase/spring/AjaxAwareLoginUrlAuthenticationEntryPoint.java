package com.gsu.knowledgebase.spring;

import com.gsu.knowledgebase.repository.KnowledgeBaseDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAwareLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	@Autowired
	private KnowledgeBaseDao knowledgeBaseDao;

	private static Logger logger = Logger.getLogger(AjaxAwareLoginUrlAuthenticationEntryPoint.class);

    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {


        if (authException instanceof InsufficientAuthenticationException && request.getServletPath().contains("/api")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        } else if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        } else {
            super.commence(request, response, authException);
        }
    }

    public AjaxAwareLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }
}
