/*******************************************************************************
 * 
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 * 
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 * 
 * **********************************************************************************************************************
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 * 
 * **********************************************************************************************************************
 * 
 *  $Id: ConversationRedirectResult.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.ServletRedirectResult;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.util.RedirectUtil;

/**
 * 
 * @author rees.byars
 * 
 */
public class ConversationRedirectResult extends ServletRedirectResult {

    private static final long serialVersionUID = 2405888426696017242L;

    /**
     * Sends the redirection with the conversation IDs included as parameters
     * 
     * @param response
     *        The response
     * @param finalLocation
     *        The location URI
     * @throws IOException
     */
    @Override
    protected void sendRedirect(HttpServletResponse response,
            String finalLocation) throws IOException {

        String finalLocationWithConversationIds = RedirectUtil
                .getUrlParamString(finalLocation, ConversationAdapter
                        .getAdapter().getViewContext());

        super.sendRedirect(response, finalLocationWithConversationIds);

    }

}
