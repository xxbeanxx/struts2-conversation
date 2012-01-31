package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.util.ScopeUtil;

public class DefaultConversationManager implements ConversationManager, ConversationPostProcessor, Serializable {
	
	private static final long serialVersionUID = -5155592774429186182L;
	private static final Logger LOG = LoggerFactory.getLogger(DefaultConversationManager.class);
	protected ConversationConfigurationProvider configurationProvider;
	
	@Override
	public void init(ConversationConfigurationProvider configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

	@Override
	public void processConversations(ConversationAdapter conversationAdapter) {
		Object action = conversationAdapter.getAction();
		Collection<ConversationConfiguration> actionConversationConfigs = this.configurationProvider.getConfigurations(action.getClass());
		if (actionConversationConfigs != null) {
			for (ConversationConfiguration conversationConfig : actionConversationConfigs) {
				processConversation(conversationConfig, conversationAdapter, action);
			}
		}
	}
	
	protected void processConversation(ConversationConfiguration conversationConfig, ConversationAdapter conversationAdapter, Object action) {
		
		String actionId = conversationAdapter.getActionId();
		Map<String, Object> sessionContext = conversationAdapter.getSessionContext();
		String conversationName = conversationConfig.getConversationName();
		String conversationId = (String) conversationAdapter.getRequestContext().get(conversationName);
		
		if (conversationId != null) {
			
			if (conversationConfig.containsAction(actionId)) {
				
				@SuppressWarnings("unchecked")
				Map<String, Object> conversationContext = (Map<String, Object>) sessionContext.get(conversationId);

				if (conversationContext != null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("In Conversation " + conversationName + ".  Setting Conversation Field values for method "
								+ actionId + " of class " + action.getClass());
					}
					Map<String, Field> actionConversationFields = conversationConfig.getFields();
					if (actionConversationFields != null) {
						ScopeUtil.setFieldValues(action, actionConversationFields, conversationContext);
					}
				}
				
				if (conversationConfig.isEndAction(actionId)) {
					conversationAdapter.dispatchPostProcessor(new ConversationEndProcessor(), conversationConfig, conversationId);
				} else {
					conversationAdapter.dispatchPostProcessor(this, conversationConfig, conversationId);
				}
			}
		} else if (conversationConfig.isBeginAction(actionId)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Beginning new " + conversationName + " conversation.");
			}
			conversationId = java.util.UUID.randomUUID().toString();
			conversationAdapter.dispatchPostProcessor(this, conversationConfig, conversationId);
		}
	}

	@Override
	public void injectConversationFields(Object target, ConversationAdapter conversationAdapter) {
		Collection<ConversationConfiguration> actionConversationConfigs = this.configurationProvider.getConfigurations(target.getClass());
		if (actionConversationConfigs != null) {
			Map<String, Object> session = conversationAdapter.getSessionContext();
			for (ConversationConfiguration conversation : actionConversationConfigs) {
				String conversationId = conversationAdapter.getRequestContext().get(conversation.getConversationName());
				if (conversationId != null) {
					@SuppressWarnings("unchecked")
					Map<String, Object> conversationContext = (Map<String, Object>) session.get(conversationId);
					if (conversationContext != null) {
						Map<String, Field> actionConversationFields = conversation.getFields();
						if (actionConversationFields != null) {
							ScopeUtil.setFieldValues(target, actionConversationFields, conversationContext);
						}
					}
				}
			}
		}
	}

	@Override
	public void extractConversationFields(Object target, ConversationAdapter conversationAdapter) {
		Collection<ConversationConfiguration> actionConversationConfigs = this.configurationProvider.getConfigurations(target.getClass());
		if (actionConversationConfigs != null) {
			for (ConversationConfiguration conversation : actionConversationConfigs) {
				
				Map<String, Field> actionConversationFields = conversation.getFields();
				String conversationName = conversation.getConversationName();
				String conversationId = conversationAdapter.getRequestContext().get(conversationName);
				
				if (conversationId == null) {
					conversationId = java.util.UUID.randomUUID().toString();
				}
				
				if (actionConversationFields != null) {
					
					Map<String, Object> sessionContext = conversationAdapter.getSessionContext();
					@SuppressWarnings("unchecked")
					Map<String, Object> conversationContext = (Map<String, Object>) sessionContext.get(conversationId);
					if (conversationContext == null) {
						conversationContext = conversationAdapter.createConversationContext(conversationId, sessionContext);
					}
					conversationContext.putAll(ScopeUtil.getFieldValues(target, actionConversationFields));
				}
				
				conversationAdapter.addConversation(conversationName, conversationId);
			}
		}
	}

	@Override
	public void postProcessConversation(
			ConversationAdapter conversationAdapter,
			ConversationConfiguration conversationConfig, String conversationId) {
		
		Object action = conversationAdapter.getAction();
		
		Map<String, Field> actionConversationFields = conversationConfig.getFields();
		
		if (actionConversationFields != null) {
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Getting conversation fields for Conversation " + conversationConfig.getConversationName() 
						+ " following execution of action " + conversationAdapter.getActionId());
			}
			
			Map<String, Object> sessionContext = conversationAdapter.getSessionContext();
			@SuppressWarnings("unchecked")
			Map<String, Object> conversationContext = (Map<String, Object>) sessionContext.get(conversationId);
			if (conversationContext == null) {
				conversationContext = conversationAdapter.createConversationContext(conversationId, sessionContext);
			}
			conversationContext.putAll(ScopeUtil.getFieldValues(action,actionConversationFields));
			
		}
		
		conversationAdapter.addConversation(conversationConfig.getConversationName(), conversationId);
	}

}