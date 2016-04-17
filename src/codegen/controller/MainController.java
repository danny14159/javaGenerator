package codegen.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codegen.utils.DbHelper;
import codegen.utils.Strings;
import codegen.utils.SystemUtils;

@WebServlet("/")
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	DbHelper db = new DbHelper();
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String table = request.getParameter("table");
		String url = request.getParameter("url"),
				username = request.getParameter("username"),
				password = request.getParameter("password"),
				desktop = request.getParameter("desktop"),
				exportPath = request.getParameter("exportPath"),
				package_ = request.getParameter("package_"),
				className = request.getParameter("className");
		
		db.setDB_URL(url);
		db.setDB_PASSWORD(password);
		db.setDB_USERNAME(username);
		request.setAttribute("exportPath", exportPath);
		request.setAttribute("package_", package_);
		request.setAttribute("url", url);
		request.setAttribute("username", username);
		request.setAttribute("password", password);
		request.setAttribute("table", table);
		
		if(Strings.isNotBlank(table)){
			className = SystemUtils.getClassNameFromTableName(table, "t");
		}
		
		request.setAttribute("className", className);
		if(Strings.isNotBlank(desktop)){
			request.setAttribute("exportPath", SystemUtils.getDesktopPath());
		}
		
		try {
			request.setAttribute("tables", db.getTables());
			if(table!=null){
				request.setAttribute("columns", db.getColumns(table));
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("exception", e.getMessage());
		}
		finally{
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
	}

}
