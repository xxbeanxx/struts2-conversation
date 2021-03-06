package com.google.code.rees.scope.session;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.code.rees.scope.container.ScopeContainerProvider;
import com.google.code.rees.scope.mocks.actions.MockPojoController;
import com.google.code.rees.scope.struts2.StrutsScopeConstants;
import com.google.code.rees.scope.testutil.SerializationTestingUtil;
import com.google.code.rees.scope.testutil.StrutsScopeTestCase;
import com.google.code.rees.scope.testutil.TestConstants;
import com.google.code.struts2.test.junit.StrutsConfiguration;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.inject.Inject;

@StrutsConfiguration(locations = "struts.xml")
public class DefaultSessionConfigurationProviderTest extends
        StrutsScopeTestCase<Object> {

    
	@Inject
	ScopeContainerProvider containerProvider;
	
    SessionConfigurationProvider provider;

    @Test
    @SuppressWarnings("rawtypes")
    public void testGetSessionFieldConfig() {
    	
    	provider = containerProvider.getScopeContainer().getComponent(SessionConfigurationProvider.class);
    	
        SessionConfiguration config = provider
                .getSessionConfiguration(MockPojoController.class);
        assertNotNull(config);
        for (Class clazz : TestConstants.SESSION_FIELD_ACTION_CLASSES) {
            String failMessage = "SessionConfigurationProvider failed to provide config for class:  "
                    + clazz.getName();
            assertNotNull(failMessage, config.getFields(clazz));
        }
        for (Class clazz : TestConstants.NO_SESSION_FIELD_ACTION_CLASSES) {
            String failMessage = "SessionConfigurationProvider erroneously provided config for class:  "
                    + clazz.getName();
            assertNull(failMessage, config.getFields(clazz));
        }

    }

    @Test
    public void testSessionFieldNaming() throws Exception {
    	
    	provider = containerProvider.getScopeContainer().getComponent(SessionConfigurationProvider.class);

        SessionConfiguration config = provider
                .getSessionConfiguration(MockPojoController.class);
        assertNotNull(config);
        Set<String> fieldNames = config.getFields(MockPojoController.class)
                .keySet();
        assertTrue(fieldNames.contains("java.lang.String.sessionField"));

        // this next assertion isn't really related but i did it for the shit of
        // it
        request.addParameter("sessionField", "hola");
        ActionProxy proxy = this.getActionProxy("begin");
        proxy.execute();

        @SuppressWarnings("unchecked")
        Map<String, Object> sessionFieldMap = (Map<String, Object>) session
                .get(StrutsScopeConstants.SESSION_FIELD_MAP_KEY);
        assertTrue(sessionFieldMap
                .containsKey("java.lang.String.sessionString"));
    }

    @Test
    public void testSerialization() throws Exception {
    	
    	provider = containerProvider.getScopeContainer().getComponent(SessionConfigurationProvider.class);
    	
        provider.getSessionConfiguration(MockPojoController.class).getFields(MockPojoController.class);
        provider = SerializationTestingUtil.getSerializedCopy(provider);
        provider.getSessionConfiguration(MockPojoController.class).getFields(
                MockPojoController.class);
    }

}
