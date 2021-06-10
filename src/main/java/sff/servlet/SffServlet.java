package sff.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author SFF
 * @version 1.0.0
 * @description
 * @date 2021/5/7 - 22:30
 */

public class SffServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("test  servlet");
        ServletOutputStream outputStream = resp.getOutputStream();
        outputStream.write("hello".getBytes());
    }
}
