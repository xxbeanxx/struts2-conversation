package com.github.xxbeanxx.scope.conversation.context;

import javax.servlet.http.HttpSession;

import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.DefaultHttpConversationContextManagerProvider;
import com.google.code.rees.scope.conversation.context.HttpConversationUtil;
import com.google.code.rees.scope.util.monitor.ScheduledExecutorTimeoutMonitor;
import com.google.code.rees.scope.util.monitor.TimeoutMonitor;

/**
 * @author Greg Baker
 */
@SuppressWarnings("serial")
public class UUIDBasedHttpConversationContextManagerProvider extends DefaultHttpConversationContextManagerProvider {

	@Override
	protected ConversationContextManager createContextManager(HttpSession session) {
		UUIDBasedConversationContextManager contextManager = new UUIDBasedConversationContextManager();
    	contextManager.setMaxInstances(super.maxInstances);
    	contextManager.setContextFactory(super.conversationContextFactory);
        TimeoutMonitor<ConversationContext> timeoutMonitor = ScheduledExecutorTimeoutMonitor.spawnInstance(this, super.monitoringFrequency);
        contextManager.setTimeoutMonitor(timeoutMonitor);
        HttpConversationUtil.setContextManager(session, contextManager);
        HttpConversationUtil.setTimeoutMonitor(session, timeoutMonitor);
        return contextManager;
	}
	
}
