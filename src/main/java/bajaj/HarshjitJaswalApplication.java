package bajaj;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class HarshjitJaswalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarshjitJaswalApplication.class, args);
    }

    @PostConstruct
    public void executeFlow() {
        RestTemplate restTemplate = new RestTemplate();

        String webhookApi = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("name", "harshjit jaswal");
        requestPayload.put("regNo", "REG");
        requestPayload.put("email", "harshjitjaswal221218@acropolis.in");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestPayload, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                webhookApi,
                HttpMethod.POST,
                entity,
                Map.class
        );

        String webhookUrl = (String) response.getBody().get("webhook");
        String accessToken = (String) response.getBody().get("accessToken");

        String finalQuery = """
            SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME,
            COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
            FROM EMPLOYEE e1
            JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID
            LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT
            AND e1.DOB > e2.DOB
            GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME
            ORDER BY e1.EMP_ID DESC
        """;

        Map<String, String> answerPayload = new HashMap<>();
        answerPayload.put("finalQuery", finalQuery);

        HttpHeaders answerHeaders = new HttpHeaders();
        answerHeaders.setContentType(MediaType.APPLICATION_JSON);
        answerHeaders.setBearerAuth(accessToken);

        HttpEntity<Map<String, String>> answerEntity = new HttpEntity<>(answerPayload, answerHeaders);

        ResponseEntity<String> answerResponse = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                answerEntity,
                String.class
        );

        System.out.println("Response from testWebhook: " + answerResponse.getBody());
    }
}
