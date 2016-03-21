package com.ipsoft.plugins.bamboo.lync;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.bamboo.deployments.results.DeploymentResult;
import com.atlassian.bamboo.notification.Notification;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.plan.cache.ImmutablePlan;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.variable.CustomVariableContext;

import com.ipsoft.plugins.bamboo.lync.client.lyncClient;
import com.ipsoft.plugins.bamboo.lync.client.lyncConstants;

import org.apache.log4j.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.codehaus.jackson.map.ObjectMapper;


public class lyncNotificationTransport implements NotificationTransport {
    private static final Logger log = Logger.getLogger(lyncNotificationTransport.class);
    private static lyncClient client;
    private static ObjectMapper mapper;

    static {
        client = new lyncClient();
        mapper = new ObjectMapper();
        client.prepareClient();
    }

    private final String subject;

    @Nullable
    private final ImmutablePlan plan;
    @Nullable
    private final ResultsSummary resultsSummary;
    @Nullable
    private final DeploymentResult deploymentResult;

    public lyncNotificationTransport(String subject,
                                     @Nullable ImmutablePlan plan,
                                     @Nullable ResultsSummary resultsSummary,
                                     @Nullable DeploymentResult deploymentResult,
                                     CustomVariableContext customVariableContext) {

        this.subject = customVariableContext.substituteString(subject);
        this.plan = plan;
        this.resultsSummary = resultsSummary;
        this.deploymentResult = deploymentResult;
    }


    public void sendNotification(@NotNull Notification notification) {
        String message = notification.getIMContent();
        Map<String, String> responseMap = new HashMap<String, String>();

        try {
            if (!client.peekAuthenticationMap()) {
                String clientId = client.createApplication();
                log.info("clientId: " + clientId);
                responseMap = tryToSendMessage(subject, message);
            } else {
                synchronized (client) {
                    responseMap = tryToSendMessage(subject, message);
                    if (Integer.valueOf(responseMap.get("ResponseCode")) != lyncConstants.HTTP_RESPONSE_CODE_CREATED) {
                        client.removeTimedOutToken();
                        client.createApplication();
                        responseMap = tryToSendMessage(subject, message);
                    }
                }
            }
            log.info(mapper.writeValueAsString(responseMap));
        } catch (Exception e) {
            log.trace(e.getStackTrace());
        }
    }

    public Map<String, String> tryToSendMessage(String subject, String message) {
        int responseCode = 500;
        responseCode = client.sendMessage(subject, message);
        Map<String, String> responseMap = new HashMap<String, String>();
        responseMap.put("Subject", subject);
        responseMap.put("Message", message);
        responseMap.put("ResponseCode", String.valueOf(responseCode));
        return responseMap;
    }
}
