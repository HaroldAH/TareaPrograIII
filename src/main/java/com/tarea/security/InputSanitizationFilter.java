package com.tarea.security;

import com.tarea.security.InputSanitizationUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class InputSanitizationFilter extends GenericFilter {

     
    private static final Pattern[] MALICIOUS_PATTERNS = new Pattern[]{
        Pattern.compile("<script", Pattern.CASE_INSENSITIVE),
        Pattern.compile("drop\\s+table", Pattern.CASE_INSENSITIVE),
        Pattern.compile("insert\\s+into", Pattern.CASE_INSENSITIVE),
        Pattern.compile("select\\s+.*\\s+from", Pattern.CASE_INSENSITIVE)
    };

     
    private static final int MAX_PARAM_LENGTH = 255;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

         
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

         
        for (String param : wrappedRequest.getParameterMap().keySet()) {
            for (String value : wrappedRequest.getParameterValues(param)) {
                if (value.length() > MAX_PARAM_LENGTH) {
                    ((HttpServletResponse) res).sendError(400, "Input too long");
                    return;
                }
                if (InputSanitizationUtils.containsMaliciousPattern(value)) {
                    ((HttpServletResponse) res).sendError(400, "Malicious input detected");
                    return;
                }
            }
        }

         
        String body = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
        System.out.println("BODY: " + body);  
        if (!body.isEmpty() && containsMaliciousPattern(body)) {
            ((HttpServletResponse) res).sendError(400, "Malicious input detected in body");
            return;
        }

         
        chain.doFilter(wrappedRequest, res);
    }

    private boolean containsMaliciousPattern(String input) {
        for (Pattern p : MALICIOUS_PATTERNS) {
            if (p.matcher(input).find()) return true;
        }
        return false;
    }
}
