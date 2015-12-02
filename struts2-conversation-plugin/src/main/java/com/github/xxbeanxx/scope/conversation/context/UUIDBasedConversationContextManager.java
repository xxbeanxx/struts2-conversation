package com.github.xxbeanxx.scope.conversation.context;

import java.util.UUID;

import com.google.code.rees.scope.conversation.context.TimeoutConversationContextManager;

/**
 * @author Greg Baker
 */
@SuppressWarnings("serial")
public class UUIDBasedConversationContextManager extends TimeoutConversationContextManager {

	@Override
	protected synchronized String getNextId() {
		return UUID.randomUUID().toString();
	}
	
}
