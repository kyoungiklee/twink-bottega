package org.opennuri.study.security.core.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("local")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("홈 페이지 요청 시")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("홈 페이지는 권한없이 접근이 가능한다.")
    void home() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "MANAGER", "USER"} )
    @DisplayName("홈은 모든 권한에서 요청이 가능하다.")
    void home_access_all_authorities(String role) throws Exception {
        mockMvc.perform(get("/").with(user("mail+1@gmail.com").roles(role)))
                .andDo(print())
                .andExpect(status().isOk())
                ;
    }
}