package com.tarea.resolvers;
import com.tarea.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
abstract class BaseResolverTest {
    protected MockedStatic<SecurityUtils> mockedSecurityUtils;
    
    @BeforeEach
    void setUpSecurity() {
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(() -> SecurityUtils.requireView(any())).then(invocation -> null);
        mockedSecurityUtils.when(() -> SecurityUtils.requireMutate(any())).then(invocation -> null);
        mockedSecurityUtils.when(SecurityUtils::userId).thenReturn(1L);
    }
    
    @AfterEach
    void tearDownSecurity() {
        if (mockedSecurityUtils != null) {
            mockedSecurityUtils.close();
        }
    }
}