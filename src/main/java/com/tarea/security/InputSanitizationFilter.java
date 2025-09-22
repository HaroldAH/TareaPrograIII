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

    // Patrones básicos de ataque (puedes ampliar esta lista)
    private static final Pattern[] MALICIOUS_PATTERNS = new Pattern[]{
        Pattern.compile("<script", Pattern.CASE_INSENSITIVE),
        Pattern.compile("drop\\s+table", Pattern.CASE_INSENSITIVE),
        Pattern.compile("insert\\s+into", Pattern.CASE_INSENSITIVE),
        Pattern.compile("select\\s+.*\\s+from", Pattern.CASE_INSENSITIVE)
    };

    // Longitud máxima permitida para parámetros
    private static final int MAX_PARAM_LENGTH = 255;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        // Envuelve la request para poder leer el body varias veces
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // Valida y sanitiza parámetros de URL y formulario
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

        // Opcional: valida el body (por ejemplo, JSON)
        String body = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
        System.out.println("BODY: " + body); // <-- Agrega esto temporalmente
        if (!body.isEmpty() && containsMaliciousPattern(body)) {
            ((HttpServletResponse) res).sendError(400, "Malicious input detected in body");
            return;
        }

        // Continúa la cadena de filtros
        chain.doFilter(wrappedRequest, res);
    }

    private boolean containsMaliciousPattern(String input) {
        for (Pattern p : MALICIOUS_PATTERNS) {
            if (p.matcher(input).find()) return true;
        }
        return false;
    }
}
