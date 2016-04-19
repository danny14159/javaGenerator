package codegen.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.StringUtils;

import codegen.utils.DbHelper;
import codegen.utils.Strings;
import codegen.utils.SystemUtils;

@WebServlet("/index")
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
				className = request.getParameter("className"),
				url_host = request.getParameter("url_host"),
				url_port = request.getParameter("url_port"),
				url_db = request.getParameter("url_db"),
				pk = request.getParameter("pk");
		
		url = "jdbc:mysql://"+url_host+":"+url_port+"/"+url_db+"?characterEncoding=utf8";
		if(Strings.isEmpty(url_host)){
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
			return ;
		}
		
		db.setDB_URL(url);
		db.setDB_PASSWORD(password);
		db.setDB_USERNAME(username);
		request.setAttribute("exportPath", exportPath);
		request.setAttribute("package_", package_);
		request.setAttribute("url", url);
		request.setAttribute("username", username);
		request.setAttribute("password", password);
		request.setAttribute("table", table);
		request.setAttribute("pk", pk);
		request.setAttribute("url_host", url_host);
		request.setAttribute("url_port", url_port);
		request.setAttribute("url_db", url_db);
		
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
