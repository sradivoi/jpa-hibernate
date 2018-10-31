package com.radivoi.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radivoi.domain.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sradivoi
 * @date 26/10/2018
 **/
@WebService
public class UserServlet extends HttpServlet {

    private EntityManagerFactory entityManagerFactory;

    @Override
    public void init() throws ServletException {
        entityManagerFactory = (EntityManagerFactory) getServletContext().getAttribute(Constant.ENTITY_MANAGER_FACTORY);
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<User> persistedUsers = new ArrayList<>();
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            persistedUsers = entityManager.createNamedQuery(User.FIND_ALL, User.class).getResultList();

            entityManager.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print(persistedUsers);

    }

    @Override
    @Transactional
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = entityManager.getTransaction();
            tx.begin();

            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            User user = new ObjectMapper().readValue(br, User.class);
            entityManager.persist(user);

            tx.commit();

            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print("{\"message\":\"Success\"}");

        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            System.out.println(ex);
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print("{\"message\":\"Failure\"}");
        } finally {
            entityManager.close();
        }


    }

    private static SessionFactory buildSessionFactory(Class clazz) {
        return new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(clazz)
                .buildSessionFactory();
    }

}
