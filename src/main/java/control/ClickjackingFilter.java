package control;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ClickjackingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Codice di inizializzazione se necessario
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("X-Frame-Options", "DENY");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Codice di pulizia se necessario
    }
}
