package com.example.bajajfinserv;

import com.example.bajajfinserv.dto.SolutionRequest;
import com.example.bajajfinserv.dto.WebhookRequest;
import com.example.bajajfinserv.dto.WebhookResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ChallengeRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Starting the challenge process...");

        // 1. Generate the Webhook
        WebhookResponse webhookResponse = generateWebhook();
        if (webhookResponse == null || webhookResponse.getAccessToken() == null) {
            System.err.println("Failed to generate webhook. Exiting.");
            return;
        }
        System.out.println("✅ Access Token Received: " + webhookResponse.getAccessToken());

        // 2. Solve the SQL Problem
        // ⚠️ IMPORTANT: Replace this with your actual registration number!
        String myRegNo = "22BDS0191";
        String finalQuery = solveSqlProblem(myRegNo);
        System.out.println("📖 Final SQL Query Prepared: " + finalQuery);

        // 3. Submit the solution (using fixed URL as per problem statement)
        submitSolution(webhookResponse.getAccessToken(), finalQuery);
    }

    private WebhookResponse generateWebhook() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        // ⚠️ IMPORTANT: Replace these details with your own!
        WebhookRequest requestBody = new WebhookRequest("Ayushman kumar", "22BDS0191", "kumarayushman0406@gmail.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<WebhookRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(url, entity, WebhookResponse.class);
            System.out.println("Webhook Response Status: " + response.getStatusCode());
            System.out.println("Webhook Response Body: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error generating webhook: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String solveSqlProblem(String regNo) {
        Pattern pattern = Pattern.compile("(\\d{2})$");
        Matcher matcher = pattern.matcher(regNo);
        int lastTwoDigits;

        if (matcher.find()) {
            lastTwoDigits = Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Could not extract last two digits from registration number: " + regNo);
        }

        String sqlQuery;

        if (lastTwoDigits % 2 != 0) {
            // Odd number -> Question 1
            System.out.println("RegNo ends in odd number (" + lastTwoDigits + "). Solving Question 1.");
            sqlQuery = "SELECT " +
                    "p.AMOUNT as SALARY, " +
                    "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) as NAME, " +
                    "YEAR(CURRENT_DATE) - YEAR(e.DOB) as AGE, " +
                    "d.DEPARTMENT_NAME " +
                    "FROM PAYMENTS p " +
                    "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                    "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                    "WHERE DAY(p.PAYMENT_TIME) != 1 " +
                    "ORDER BY p.AMOUNT DESC " +
                    "LIMIT 1;";
        } else {
            // Even number -> Question 2 [cite: 22]
            System.out.println("RegNo ends in even number (" + lastTwoDigits + "). Solving Question 2.");
            // ⚠️ IMPORTANT: Replace this placeholder with your actual SQL query for Question 2
            sqlQuery = "SELECT * FROM your_table WHERE condition = 'even';";
        }
        return sqlQuery;
    }

    private void submitSolution(String accessToken, String finalQuery) {
        SolutionRequest solutionBody = new SolutionRequest(finalQuery);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionBody, headers);

        try {
            // Use the correct webhook URL as per problem statement
            String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
            ResponseEntity<String> response = restTemplate.postForEntity(submitUrl, entity, String.class);
            System.out.println("🎉 Solution submitted successfully!");
            System.out.println("Response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error submitting solution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
