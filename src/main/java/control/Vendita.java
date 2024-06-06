package control;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import model.ProductBean;
import model.ProductModel;

/**
 * Servlet implementation class Vendita
 */
@WebServlet("/Vendita")
public class Vendita extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Vendita() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // This servlet is designed to handle only POST requests, so all GET requests
        // are redirected
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductBean product = new ProductBean();
        product.setEmail((String) request.getSession().getAttribute("email"));

        String UPLOAD_DIRECTORY = request.getServletContext().getRealPath("/") + "img/productIMG/";

        if (ServletFileUpload.isMultipartContent(request)) {
            try {
                List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory())
                        .parseRequest(new ServletRequestContext(request));

                for (FileItem item : multiparts) {
                    if (!item.isFormField()) {
                        String name = new File(item.getName()).getName();
                        item.write(new File(UPLOAD_DIRECTORY + File.separator + name));
                        product.setImmagine(name);
                    } else {
                        String fieldName = item.getFieldName();
                        String fieldValue = item.getString("UTF-8");
                        // Sanitize input
                        fieldValue = sanitizeInput(fieldValue);
                        if (fieldName.equals("nome")) {
                            product.setNome(fieldValue);
                        } else if (fieldName.equals("prezzo")) {
                            product.setPrezzo(Double.parseDouble(fieldValue));
                        } else if (fieldName.equals("spedizione")) {
                            product.setSpedizione(Double.parseDouble(fieldValue));
                        } else if (fieldName.equals("tipologia")) {
                            product.setTipologia(fieldValue);
                        } else if (fieldName.equals("tag")) {
                            product.setTag(fieldValue);
                        } else if (fieldName.equals("descrizione")) {
                            product.setDescrizione(fieldValue);
                        }
                    }
                }

                request.setAttribute("message", "File Uploaded Successfully");
            } catch (Exception ex) {
                ex.printStackTrace();
                request.setAttribute("message", "File Upload Failed due to " + ex);
            }
        } else {
            request.setAttribute("message", "Sorry this Servlet only handles file upload request");
        }

        ProductModel model = new ProductModel();
        try {
            model.doSave(product);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.getSession().setAttribute("refreshProduct", true);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private String sanitizeInput(String input) {
        // Sanitize input to prevent XSS attacks
        String cleanedInput = input.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        cleanedInput = stripScriptTags(cleanedInput);
        return cleanedInput;
    }

    private String stripScriptTags(String input) {
        // Strip <script> tags and their content
        return input.replaceAll("(?i)<script.*?>.*?</script.*?>", "");
    }
}
