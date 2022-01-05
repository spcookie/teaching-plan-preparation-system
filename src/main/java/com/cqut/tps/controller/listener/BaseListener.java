package com.cqut.tps.controller.listener; /**
 * @author Augenstern
 * @date 2021/12/30
 */

import com.cqut.tps.entity.BasicInformation;
import com.cqut.tps.entity.Curriculum;
import com.cqut.tps.entity.PrerequisiteRelationship;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@WebListener
public class BaseListener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

    public BaseListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /* This method is called when the servlet context is initialized(when the Web application is deployed). */
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /* This method is called when the servlet Context is undeployed or Application Server shuts down. */
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        session.setAttribute("BasicInformation", new BasicInformation());
        session.setAttribute("Curriculums", new ArrayList<Curriculum>());
        session.setAttribute("PrerequisiteRelationship", new ArrayList<PrerequisiteRelationship>());
        session.setAttribute("timeTable", new HashMap<String, List<Curriculum>>());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        /* Session is destroyed. */
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is added to a session. */
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is removed from a session. */
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is replaced in a session. */
    }
}
