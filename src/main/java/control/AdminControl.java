package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.OrderBean;
import model.OrderModel;
import model.ProductBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/AdminControl")
public class AdminControl extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(AdminControl.class);

    public AdminControl() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (request.getParameter("logout") != null) {
                HttpSession session = request.getSession();  
                session.invalidate();
                
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
            else if (request.getParameter("vendi") != null) {
                response.sendRedirect(request.getContextPath() + "/vendita.jsp");
            }
            else if (request.getParameter("ordini") != null) {
                String email = request.getParameter("email");
                OrderModel model = new OrderModel();
                Collection<OrderBean> ordini = null;
                
                try {
                    ordini = model.getOrders(email);
                    ordini = model.getRecensioni(ordini, email);
                    request.getSession().setAttribute("listaOrdini", ordini);
                    request.getSession().setAttribute("ControlOrd", false);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/controllo-ordini.jsp?email=" + email);
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            // Logging dell'errore senza informazioni sensibili
            logger.error("Si è verificato un errore interno del server", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
