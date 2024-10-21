package com.algonquin.loggy.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.algonquin.loggy.dao.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.algonquin.loggy.model.Log;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@WebServlet("/loggy-lab3")
public class LogServlet extends HttpServlet {
    private List<Log> logs = new ArrayList<>(); // memory  for logs
    private int logIdCounter = 1; // Counter to generate unique IDs for logs
    private LogDAO instlogDAO; // DAO for database interactions

    
    
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //fix driver missing on startup
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loggydb", "user1", "password1");
            instlogDAO = new LogDAO(connection); // Initialize LogDAO with a database connection
            logs = instlogDAO.getAllLogs();
            
            if (!logs.isEmpty()) {
            	logIdCounter = logs.get(logs.size()-1).getId() + 1;
            }

            
        } catch (SQLException e) {
            throw new ServletException(e); // Handle database connection errors
        } catch (ClassNotFoundException e) {
            throw new ServletException("MySQL JDBC Driver not found", e);
        }
    }
		/*
		    @Override
		    public void init() throws ServletException {
		        try {
		            InitialContext ctx = new InitialContext();
		            DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/loggydb");
		            Connection connection = ds.getConnection();
		            logDAO = new LogDAO(connection); 
		        } catch (Exception e) {
		            throw new ServletException(e);
		        }
		    }
		*/
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        
        if (request.getParameter("delete") != null) {
            int idToDelete = Integer.parseInt(request.getParameter("id"));
            try {
            	instlogDAO.deleteLog(idToDelete);
                logs.removeIf(log -> log.getId() == idToDelete);
            } catch (SQLException e) {
            	System.out.println("Failed to delete " + idToDelete);
            	}
            response.sendRedirect("/loggy-lab3/loggy-lab3"); // Redirect to the main log page
        }
        
        if (request.getParameter("edit") != null) {
            int idToEdit = Integer.parseInt(request.getParameter("id"));
            Log logToEdit = logs.stream().filter(log -> log.getId() == idToEdit).findFirst().orElse(null);
            
            if (logToEdit != null) { //populates with log values
                out.println("<form method='post'>");
                out.println("Title: <input type='text' name='title' value='" + logToEdit.getTitle() + "' maxlength='60'/><br>");
                out.println("Content: <textarea name='content' maxlength='120'>" + logToEdit.getContent() + "</textarea><br>");
                out.println("<input type='hidden' name='id' value='" + logToEdit.getId() + "'/>");
                out.println("<input type='submit' value='Update'/>");
                out.println("</form>");
            }
        } else {
	        // Displaying the form to submit a log entry
	        out.println("<html><body>");
	        out.println("<h2>Submit Your Log</h2>");
	        out.println("<form method='post'>");
	        out.println("Title: <input type='text' name='title' maxlength='60'/><br>");
	        out.println("Content: <textarea name='content' maxlength='120'></textarea><br>");
	        out.println("<input type='submit' value='Submit'/>");
	        out.println("</form>");
        }

        // Displaying previous logs
        out.println("<h2>Previous Logs</h2>");
        for (Log log : logs) {
            out.println("<p>ID: " + log.getId() + "</p>");
            out.println("<p>Title: " + log.getTitle() + "</p>");
            out.println("<p>Content: " + log.getContent() + "</p>");
            out.println("<p>Timestamp: " + log.getTimestamp() + "</p>");
            out.println("<a href='?delete=true&id=" + log.getId() + "'>Delete</a> ");
            out.println("<a href='?edit=true&id=" + log.getId() + "'>Edit</a>");
            out.println("<hr>");
        }
        out.println("</body></html>");


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	int id = 0;
    	String title = request.getParameter("title");
        String content = request.getParameter("content");
        
        //existence of ID in post lets the logic know that it is EDIT rather than NEW
        try {
        	if(request.getParameter("id") != null) {
        		id = Integer.parseInt(request.getParameter("id"));
        	}
        } catch (NumberFormatException e) {}
        if (title != null && content != null) {
        	
        	if (id != 0) {
                Iterator<Log> iterator = logs.iterator();
                while (iterator.hasNext()) {
                    Log log = iterator.next();
                    if (log.getId() == id) {
                        iterator.remove(); // Safe removal
                    }
                }
        	}
        	/*for (Log log : logs) {  OLD remove causing errors
        		if(log.getId() == id) {
        			logs.remove(log);
        		}
        	}*/
        	
        	if (id != 0) { //id != 0 means that it is an update
                Log newLog = new Log(id, title, content);
                try {
                	instlogDAO.updateLog(newLog);
                	logs.add(newLog);
                } catch (SQLException e) {
                	System.out.println("Failed to edit " + id);
                }
        	} else { 
        		Log newLog = new Log(logIdCounter, title, content);
        		System.out.println(newLog.getId());
        		System.out.println(newLog.getTitle());
        		System.out.println(newLog.getContent());
        		try {
        			instlogDAO.createLog(newLog);
                	logs.add(newLog);
                	logIdCounter++;
        		} catch (SQLException e) {
        			System.out.println("Failed to create log " + logIdCounter);
        		}
        	}

            response.sendRedirect("/loggy-lab3/loggy-lab3"); // Redirect to the GET method to display the updated list
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Title and Content are required!"); // Handle missing inputs
        }
    }
}