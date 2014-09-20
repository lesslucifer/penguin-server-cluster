/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SERVLET;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libCore.LogUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
/**
 *
 * @author LinhTA
 */
public class ServerServlet extends HttpServlet{
    
    private static Logger logger_ = Logger.getLogger(ServerServlet.class);

    
    protected void echo(Object text, HttpServletResponse response) {
        PrintWriter out = null;
         
        try {
            response.setContentType("text/html;charset=UTF-8");
            
            out = response.getWriter();
            out.print(text.toString());
        } catch (IOException ex) {
            logger_.error(ex.getMessage());
           
        } finally {
            out.close();
        }
    }

     protected void out(Object content, HttpServletResponse response) {

        PrintWriter out = null;
        try {
            prepareHeader(response);
            out = response.getWriter();
            out.print(content);
            out.close();
        } catch (Exception ex) {
            //logger_.error("CampainAction.out:" + ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
     
     protected void prepareHeader(HttpServletResponse resp) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.addHeader("Server", "canhcutvuive");
    }
     
     @Override
     protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         doProcess(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    protected void doProcess(HttpServletRequest req, HttpServletResponse resp) 
    {
        
    }
}

    
    

