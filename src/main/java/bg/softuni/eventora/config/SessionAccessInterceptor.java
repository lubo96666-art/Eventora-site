package bg.softuni.eventora.config;

import bg.softuni.eventora.exception.EventoraAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object userId = request.getSession().getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("/visualization?auth=login");
            return false;
        }

        if (request.getRequestURI().startsWith("/admin/")
                && !"ADMIN".equals(request.getSession().getAttribute("role"))) {
            throw new EventoraAccessDeniedException("Само администратор може да управлява събития.");
        }
        return true;
    }
}
