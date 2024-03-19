package org.opennuri.study.security.core.adapter.in.web.front;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opennuri.study.security.core.application.port.in.ResourceManageUseCase;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security 테스트 방법<p/>
 * 만약 SecurityConfiguration에 다른 빈의 의존성이 있다면, @WebMvcTest로 띄울 시 빈을 찾을 수 없다는 예외가 발생한다. 이 경우 아래와 같이 수동으로 제외시켜준다<p/>
 * <pre> {@code
 *     @WebMvcTest(
 *          controllers = AccountController.class,
 *          excludeFilters = {
 *              @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)}
 *      )
 *      final class AccountControllerTest {
 *      }
 *  }</pre> <p/>
 * <p>
 * 만약, 아예 Controller 테스트시 SpringSecurity 설정을 제외하고 싶다면 아래와 같이 사용하면 된다.<p/>
 * <pre>
 *      {@code
 *       @WebMvcTest(
 *          controllers = AccountController.class,
 *          excludeAutoConfiguration = SecurityAutoConfiguration.class,
 *          excludeFilters = {
 *              @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)}
 *        )
 *      }
 *  </pre>
 * <p/>
 * 또는 테스트용 예외 설정을 따로 추가하는 방법도 있다.<p/>
 * <pre>
 *      {@code
 * @WebMvcTest(AccountController.class)
 * final class AccountControllerTest {
 *
 *     @TestConfiguration
 *     static class DefaultConfigWithoutCsrf extends WebSecurityConfigurerAdapter {
 *
 *         @Override
 *         protected void configure(final HttpSecurity http) throws Exception {
 *             super.configure(http);
 *             http.csrf().disable();
 *         }
 *
 *         public void configure(WebSecurity web) throws Exception {
 *             web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
 *             web.ignoring().antMatchers("/api/auth/**"); // 테스트용 API의 Security 비활성화
 *         }
 *     }
 *
 *     @Test
 *     void test() {...}
 * }
 *      }
 *  </pre>
 * 1. {@code @WithAnonymousUser} - 익명유저의 인증정보를 설정하기 위한 어노테이션<p/>
 * 2. {@code @WithUserDetails} - UserDetailsService를 통해서 유저정보를 취득하여 설정하기 위한 어노테이션<p/>
 * 3. {@code @WithMockUser} - 별도의 UserDetailsService와 같은 스텁을 제공하지 않아도 간단하게 인증정보를 설정하기 위한 어노테이션<p/>
 * {@code @WithMockUser} 사용법은 mockMvc에서 .with(user) 로 등록하거나, @WithMockUser, @WithAnonymousUser로 추가한다.
 * <pre> {@code
 * @Test
 * public void index_anonymous() throws Exception {
 *     mockMvc.perform(get("/").with(anonymous()))
 *             .andDo(print())
 *             .andExpect(status().isOk());
 * }
 *
 * @WithAnonymousUser
 * public void index_anonymous() throws Exception {
 *     mockMvc.perform(get("/"))
 *             .andDo(print())
 *             .andExpect(status().isOk());
 * }
 *
 * @Test
 * public void index_user() throws Exception {
 *     mockMvc.perform(get("/")
 *             .with(user("jake").roles("USER")))
 *             .andDo(print())
 *             .andExpect(status().isOk());
 * }
 *
 * @Test
 * public void admin_user() throws Exception {
 *     mockMvc.perform(get("/admin")
 *             .with(user("jake").roles("USER")))
 *             .andDo(print())
 *             .andExpect(status().isForbidden());
 * }
 *
 * @Test
 * @WithMockUser(username = "jake", roles = "USER")
 * public void index_user() throws Exception {
 *     mockMvc.perform(get("/"))
 *             .andDo(print())
 *             .andExpect(status().isOk());
 * }
 *
 * @Test
 * @WithMockUser(username = "jake", roles = "USER")
 * public void admin_user() throws Exception {
 *     mockMvc.perform(get("/admin"))
 *             .andDo(print())
 *             .andExpect(status().isForbidden());
 * }
 * }</pre>
 * <p>
 * 매번 똑같은 유저정보를 입력하는게 번거롭다면, 테스트용 어노테이션을 아래와 같이 만들어서 사용해도 된다.
 * <pre> {@code
 * @Retention(RetentionPolicy.RUNTIME)
 * @WithMockUser(username = "jake", roles = "USER")
 * public @interface WithUser {
 * }
 *
 * @Test
 * @WithUser
 * public void index_user() throws Exception {
 *    	mockMvc.perform(get("/"))
 *     	.andDo(print())
 *         .andExpect(status().isOk());
 * }
 * }</pre>
 * <p/>
 * 잘 사용은 안하지만, @WithUserDeatils 는 아래와 같이 테스트용 빈을 별도로 추가해주어야한다.<p/>
 * <pre>
 *     {@code
 * @Bean
 * @Profile("test")
 * public UserDetailsService userDetailsService() {
 *     return new UserDetailsService() {
 *         @Override
 *         public UserDetails loadUserByUsername(String username)
 *             throws UsernameNotFoundException {
 *             return User.withUsername(username).password("password")
 *                 .authorities(new SimpleGrantedAuthority("ROLE_USER")).build();
 *         }
 *     };
 * }
 *     }
 * </pre> <p/>
 * 해당 UserDeatilsService를 적용시키기 위해서는, 아래와 같이 @WithUserDetails(유저명)를 적어주면 된다.
 * <pre>
 *     {@code
 * @Test
 * @WithUserDetails("test_user") // "test_user"로 접근시 위에서 설정한 UserDetails가 반환됨.
 * public void some_test() { ... }
 *     }
 * </pre>
 */
@ActiveProfiles("local")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("페이지 요청 시")
class PageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RoleManageUseCase roleManageUseCase;

    @Autowired
    ResourceManageUseCase resourceManageUseCase;

    @Test
    @DisplayName("MAMAGER 권한으로 messages페이지 요청시 정상이다.")
    @WithMockUser(roles = {"MANAGER"})
    void message_ok() throws Exception {
        mockMvc.perform(get("/messages"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("MANAGER 권한이 아닌 경우 denied 페이지로 이동한다.")
    @WithMockUser(username = "mail+1@gmail.com", roles = "USER")
    void message_redirect_denied_page_wrong_authorities() throws Exception {
        mockMvc.perform(get("/messages"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/denied*"))
                ;
    }

    @Test
    @DisplayName("사용 권한 없이 messages 페이지 요청시 login 페이지도 이동한다.")
    void message_redirect_login_page_without_authorities() throws Exception {
        mockMvc.perform(get("/messages"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"))
        ;
    }

    @Test
    @DisplayName("ADMIN 권한으로 config 페이지 요청시 정상이다.")
    void config_with_authorities() throws Exception {
        mockMvc.perform(get("/config").with(user("mail+1@gmail.com").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN 권한이 아닌경우 denied 페이지로 이동한다.")
    @WithMockUser(username = "mail+1@gmail.com", roles = {"USER", "MANAGER"})
    void config_redirect_denied_without_admin_authorities() throws Exception {
        mockMvc.perform(get("/config"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/denied*"))
                ;
    }

    @Test
    @DisplayName("USER 권한으로 messages 페이지 요청 시 정상이다.")
    @WithMockUser(username = "mail+1@gmail.com", roles = "USER")
    void mypage_with_manager_authorities() throws Exception {
        mockMvc.perform(get("/mypage"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("USER 권하이 아닌경우 denied 페이지로 이동한다.")
    @WithMockUser(username = "mail+1@gmail.com", roles = {"ADMIN"})
    void mypage_redirect_denied_without_manager_authorities() throws Exception {

        mockMvc.perform(get("/mypage"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/denied*"))
        ;

    }

}