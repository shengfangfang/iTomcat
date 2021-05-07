package sff;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.RequestFilterValve;
import org.apache.juli.logging.Log;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author SFF
 * @version 1.0.0
 * @description
 * @date 2021/4/21 - 23:06
 */

public class TestValve extends RequestFilterValve {
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        System.out.println("TestValve");
        getNext().invoke(request,response);
    }

    @Override
    protected Log getLog() {
        return null;
    }
}
