package com.courier.listener;

import com.courier.dao.CustomerDao;
import com.courier.dao.DeliveryAgentDao;
import com.courier.dao.UserDao;
import com.courier.model.Customer;
import com.courier.model.DeliveryAgent;
import com.courier.model.User;
import com.courier.util.DBConnection;
import com.courier.util.PasswordUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            runSchema();
            seedIfEmpty();
        } catch (Exception e) {
            sce.getServletContext().log("Database init failed. Create courier_db and check db.properties.", e);
        }
    }

    private void runSchema() throws Exception {
        String sql;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("schema.sql missing");
            }
            sql = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
        try (Connection c = DBConnection.getBootstrapConnection();
             Statement st = c.createStatement()) {
            for (String part : sql.split(";")) {
                String stmt = part.trim();
                if (stmt.isEmpty() || stmt.startsWith("--")) {
                    continue;
                }
                st.execute(stmt);
            }
        }
    }

    private void seedIfEmpty() throws Exception {
        UserDao users = new UserDao();
        if (users.count() > 0) {
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(PasswordUtil.hash("admin123"));
        admin.setRole("ADMIN");
        admin.setFullName("System Administrator");
        admin.setEmail("admin@courier.local");
        admin.setPhone("9999999999");
        users.insert(admin);

        User agentUser = new User();
        agentUser.setUsername("agent1");
        agentUser.setPasswordHash(PasswordUtil.hash("agent123"));
        agentUser.setRole("AGENT");
        agentUser.setFullName("Ravi Kumar");
        agentUser.setEmail("agent1@courier.local");
        agentUser.setPhone("9888888888");
        users.insert(agentUser);
        DeliveryAgent agent = new DeliveryAgent();
        agent.setUserId(agentUser.getId());
        agent.setVehicleNo("MH12AB1234");
        agent.setStatus("AVAILABLE");
        new DeliveryAgentDao().insert(agent);

        User custUser = new User();
        custUser.setUsername("customer1");
        custUser.setPasswordHash(PasswordUtil.hash("cust123"));
        custUser.setRole("CUSTOMER");
        custUser.setFullName("Anita Sharma");
        custUser.setEmail("customer1@courier.local");
        custUser.setPhone("9777777777");
        users.insert(custUser);
        Customer customer = new Customer();
        customer.setUserId(custUser.getId());
        customer.setAddress("12 MG Road");
        customer.setCity("Pune");
        customer.setPincode("411001");
        new CustomerDao().insert(customer);
    }
}
