package com.ipsoft.plugins.bamboo.lync;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.deployments.notification.DeploymentResultAwareNotificationRecipient;
import com.atlassian.bamboo.deployments.results.DeploymentResult;
import com.atlassian.bamboo.notification.NotificationRecipient;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.notification.recipients.AbstractNotificationRecipient;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.cache.ImmutablePlan;
import com.atlassian.bamboo.plugin.descriptor.NotificationRecipientModuleDescriptor;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.template.TemplateRenderer;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.error.SimpleErrorCollection;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.atlassian.bandana.BandanaManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class lyncNotificationRecipient extends AbstractNotificationRecipient implements DeploymentResultAwareNotificationRecipient,
                                                                                        NotificationRecipient.RequiresPlan,
                                                                                        NotificationRecipient.RequiresResultSummary{


    private static final String USERNAME = "username";
    private String username = null;

    private TemplateRenderer templateRenderer;

    private ImmutablePlan plan;
    private ResultsSummary resultsSummary;
    private DeploymentResult deploymentResult;
    private CustomVariableContext customVariableContext;
    private BandanaManager bandanaManager;

    @Override
    public void populate(@NotNull Map<String, String[]> params)
    {
        this.username = getParam(USERNAME, params);
    }

    @Override
    public void init(@Nullable String configurationData)
    {
        //Skip if there's nothing to process
        if (configurationData == null || configurationData.length() == 0) {
            return;
        }

        SAXBuilder sb = new SAXBuilder();
        try {
            Document doc = sb.build(new StringReader(configurationData));
            Element root = doc.getRootElement();

            username = root.getChildText(USERNAME);
        } catch (JDOMException e) {
            //Ignore
        } catch (IOException e) {
            //Ignore
        }
    }

    @NotNull
    @Override
    public String getRecipientConfig()
    {
        //Root
        Document doc = new Document();
        Element root = new Element(lyncNotificationRecipient.class.getName());
        doc.addContent(root);

        //Username
        Element xmlUSERNAME = new Element(USERNAME);
        if (this.username != null) xmlUSERNAME.setText(this.username);
        root.addContent(xmlUSERNAME);

        //Serialize
        Format prettyFormat = Format.getPrettyFormat();
        prettyFormat.setOmitDeclaration(true);
        XMLOutputter outputter = new XMLOutputter(prettyFormat);

        return outputter.outputString(doc);
    }

    @NotNull
    @Override
    public String getEditHtml()
    {
        String editTemplateLocation = ((NotificationRecipientModuleDescriptor)getModuleDescriptor()).getEditTemplate();
        return templateRenderer.render(editTemplateLocation, populateContext());
    }

    @NotNull
    @Override
    public String getViewHtml()
    {
        String viewTemplateLocation = ((NotificationRecipientModuleDescriptor)getModuleDescriptor()).getViewTemplate();
        return templateRenderer.render(viewTemplateLocation, populateContext());
    }

    private Map<String, Object> populateContext()
    {
        Map<String, Object> context = Maps.newHashMap();

        if (username != null)
        {
            context.put(USERNAME, username);
        }

        return context;
    }

    public ErrorCollection validate(@NotNull Map<String, String[]> params)
    {
        ErrorCollection errorCollection = new SimpleErrorCollection();

        // Master Config should be done already
        String lyncServer = (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, lyncGlobalConfiguration.PROP_LYNC_SERVER);

        if (StringUtils.isEmpty(lyncServer)){
            errorCollection.addErrorMessage("Please validate your Lync Server Settings.");
        }

        //Username Exists
        String[] roomArray;
        roomArray = params.get(USERNAME);
        if ((roomArray == null) || (roomArray.length == 0)) {
            errorCollection.addError(USERNAME, "You must enter a username");
            return errorCollection;
        }

        return errorCollection;
    }

    @NotNull
    public List<NotificationTransport> getTransports()
    {
        List<NotificationTransport> list = Lists.newArrayList();

        //Global Config
        String lyncServer = (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, lyncGlobalConfiguration.PROP_LYNC_SERVER);

        //Transport
        list.add(new lyncNotificationTransport(lyncServer, plan, resultsSummary, deploymentResult, customVariableContext));

        return list;
    }

    public void setDeploymentResult(DeploymentResult deploymentResult)
    {
        this.deploymentResult = deploymentResult;
    }

    public void setPlan(final @Nullable Plan plan)
    {
        this.plan = plan;
    }

    public void setPlan(@Nullable final ImmutablePlan plan)
    {
        this.plan = plan;
    }

    public void setResultsSummary(ResultsSummary resultsSummary)
    {
        this.resultsSummary = resultsSummary;
    }

    public void setTemplateRenderer(TemplateRenderer templateRenderer)
    {
        this.templateRenderer = templateRenderer;
    }

    public void setCustomVariableContext(CustomVariableContext customVariableContext)
    {
        this.customVariableContext = customVariableContext;
    }

    public void setBandanaManager(BandanaManager bandanaManager)
    {
        this.bandanaManager = bandanaManager;
    }
}
