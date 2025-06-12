package de.ckollmeier.burgerexpress.backend;

import de.ckollmeier.burgerexpress.backend.configuration.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Import(SecurityConfig.class)
class BackendApplicationTests {

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void contextLoads() {
        assert(true);
    }

}
