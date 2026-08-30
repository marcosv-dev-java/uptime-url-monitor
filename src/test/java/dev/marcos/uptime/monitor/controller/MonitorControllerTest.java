package dev.marcos.uptime.monitor.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.marcos.uptime.monitor.configuration.AbstractIntegrationTest;
import dev.marcos.uptime.monitor.dto.request.MonitorRequest;
import dev.marcos.uptime.monitor.dto.request.RegisterRequest;
import dev.marcos.uptime.monitor.repository.MonitorRepository;
import dev.marcos.uptime.monitor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class MonitorControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MonitorRepository monitorRepository;
    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String tokenUser1;

    @BeforeEach
    void setup() throws Exception {
        monitorRepository.deleteAll();
        userRepository.deleteAll();
        tokenUser1 = registerAndLogin("user1", "password123");

    }
    private Long createMonitor(String userToken, MonitorRequest request) throws Exception {
        var result = mockMvc.perform(post("/monitors")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("id").asLong();
    }

    private String registerAndLogin(String username, String password) throws Exception {
        var result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest(username, password))))
                .andExpect(status().isCreated())
                .andReturn();
        var response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("token").asText();
    }

    @Test
    void shouldReturnOnlyMonitorsOfAuthenticatedUser() throws Exception {
        // create user 1 monitor
        MonitorRequest  request = new MonitorRequest("Monitor User1", "https://google.com", 300);
        createMonitor(tokenUser1, request);

        // create user 2 monitor
         String tokenUser2 = this.registerAndLogin("user2", "password123");
        createMonitor(tokenUser2, new MonitorRequest("Monitor User2", "https://github.com", 300));

        // user1 only can see your own monitor
        mockMvc.perform(get("/monitors")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Monitor User1"));
    }
    @Test
    void shouldDenyIdorOnGetMonitorById() throws Exception {
        Long id1 = createMonitor(tokenUser1, new MonitorRequest("Monitor User1", "https://google.com", 300));
        createMonitor(tokenUser1, new MonitorRequest("Monitor2 User1", "https://google.com", 100));
        String tokenUser2 = this.registerAndLogin("user2","1234");
        mockMvc.perform(get("/monitors/" + id1)
                .header("Authorization", "Bearer " + tokenUser2)
                .content(objectMapper.writeValueAsString(id1)))
                .andExpect(status().isForbidden());
    }
}



