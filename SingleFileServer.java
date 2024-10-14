package com.example.singlefileserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
public class SingleFileServer {

    private Connection connect() throws Exception {
        String url = "jdbc:sqlite:test.db";
        return DriverManager.getConnection(url);
    }

    @PostMapping("/vulnerable-query")
    public List<String> vulnerableQuery(@RequestBody String userInput) {
        List<String> results = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            // Vulnerable query - SQL Injection (due to direct user input in the query)
            String query = "SELECT * FROM users WHERE username = '" + userInput + "'";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                results.add(rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @GetMapping("/safe-query-int")
    public List<String> safeQueryInt(@RequestParam int id) {
        List<String> results = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            // Safe query - the external input is an integer
            String query = "SELECT * FROM users WHERE id = " + id;
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                results.add(rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @GetMapping("/safe-query-constant")
    public List<String> safeQueryConstant() {
        List<String> results = new ArrayList<>();
        String fixedUsername = "admin"; // constant value

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            // Safe query - the value is constant
            String query = "SELECT * FROM users WHERE username = '" + fixedUsername + "'";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                results.add(rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static void main(String[] args) {
        SpringApplication.run(SingleFileServer.class, args);
    }
}
