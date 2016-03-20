package com.ipsoft.plugins.bamboo.lync;


import com.atlassian.bamboo.deployments.results.DeploymentResult;
import com.atlassian.bamboo.notification.Notification;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.plan.cache.ImmutablePlan;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.variable.CustomVariableContext;

import org.apache.log4j.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class lyncNotificationTransport implements NotificationTransport{
    private static final Logger log = Logger.getLogger(lyncNotificationTransport.class);

    private final String lyncServer;

    @Nullable
    private final ImmutablePlan plan;
    @Nullable
    private final ResultsSummary resultsSummary;
    @Nullable
    private final DeploymentResult deploymentResult;

    public lyncNotificationTransport(String lyncServer,
                                     @Nullable ImmutablePlan plan,
                                     @Nullable ResultsSummary resultsSummary,
                                     @Nullable DeploymentResult deploymentResult,
                                     CustomVariableContext customVariableContext)
    {
        this.lyncServer = customVariableContext.substituteString(lyncServer);
        this.plan = plan;
        this.resultsSummary = resultsSummary;
        this.deploymentResult = deploymentResult;
    }

    public void sendNotification(@NotNull Notification notification)
    {
        //I do nothing yet but I should eventually send notifications..or stuff.
    }
}
