package org.jitsi.jicofo.rest;

import net.java.sip.communicator.util.Logger;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.jitsi.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Class that is processing HTTP requests
 */
public class RequestHandler
    extends AbstractHandler
{
    /**
     * The logger instance used by Shibboleth handler.
     */
    private static final Logger logger
        = Logger.getLogger(RequestHandler.class);

    private final List<String> supportedTargets = new ArrayList<String>();

    public RequestHandler()
    {
        supportedTargets.add("/start");
        supportedTargets.add("/stop");
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
        throws IOException, ServletException
    {
        try
        {
            if (supportedTargets.contains(target))
            {
                doHandle(target, baseRequest, request, response);
            }
            else
            {
                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Not supported path: " + target);
            }
        }
        catch(Exception e)
        {
            logger.error(e, e);

            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void dumpRequestInfo(HttpServletRequest request)
    {
        logger.info(request.getRequestURL());
        logger.debug("Headers: ");
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements())
        {
            String headerName = headers.nextElement();
            logger.debug(headerName + ": " + request.getHeader(headerName));
        }
        logger.debug("Attributes: ");
        Enumeration<String> attributes = request.getAttributeNames();
        while (attributes.hasMoreElements())
        {
            String attributeName = attributes.nextElement();
            logger.debug(
                attributeName + ": " + request.getAttribute(attributeName));
        }
    }

    private void doHandle(
        String target,
        Request baseRequest,
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException,
        ServletException
    {
        logger.info("REST request: " + target);
        dumpRequestInfo(request);

        String guid = request.getParameter("guid");
        if (StringUtils.isNullOrEmpty(guid))
        {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Missing mandatory parameter 'guid'");
            return;
        }

        String room = request.getParameter("room");
        if (StringUtils.isNullOrEmpty(room))
        {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Missing mandatory parameter 'room'");
            return;
        }


        PrintWriter responseWriter = response.getWriter();

        responseWriter.println("You've sent me:\n");
        responseWriter.println("GUID:" + guid + "\n");
        responseWriter.println("room:" + room + "\n");

        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }
}