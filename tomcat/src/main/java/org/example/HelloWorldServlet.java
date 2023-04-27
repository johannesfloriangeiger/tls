package org.example;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class HelloWorldServlet extends HttpServlet {

    private static final String MESSAGE = "Hello Tomcat!";

    @Override
    protected void doGet(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType(MediaType.TEXT_PLAIN);
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setContentLength(MESSAGE.length());

        try (final var printWriter = httpServletResponse.getWriter()) {
            printWriter.write(MESSAGE);
        }
    }
}