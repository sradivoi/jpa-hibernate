package com.radivoi.webservice;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author sradivoi
 * @date 31/10/2018
 **/
public class ContextListener implements ServletContextListener {


    public void contextInitialized(ServletContextEvent servletContextEvent) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("unit");
        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute(Constant.ENTITY_MANAGER_FACTORY, entityManagerFactory);

    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        ServletContext servletContext = servletContextEvent.getServletContext();
        EntityManagerFactory entityManagerFactory = (EntityManagerFactory) servletContext.getAttribute(Constant.ENTITY_MANAGER_FACTORY);
        entityManagerFactory.close();

    }
}
