package com.ipsoft.plugins.bamboo.lync;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.ww2.BambooActionSupport;
import com.atlassian.bandana.BandanaManager;

import org.apache.commons.lang.StringUtils;

public class lyncGlobalConfiguration extends BambooActionSupport {

    public static final String PROP_LYNC_SERVER = "lyncServer";

    private String lyncServer = null;

    private BandanaManager bandanaManager = null;


    public String Execute() throws Exception
    {
        bandanaManager.setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, PROP_LYNC_SERVER, this.lyncServer);

        return SUCCESS;
    }

    @Override
    public void validate()
    {
        if (StringUtils.isEmpty(this.lyncServer)) {
            addFieldError(lyncServer, "Some relevant error message.");
        }
    }

    public void setBandanaManager(BandanaManager bandanaManager)
    {
        this.bandanaManager = bandanaManager;
    }

    public String getLyncServer()
    {
        return (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, PROP_LYNC_SERVER);
    }

    public void setLyncServer(String lyncServer)
    {
        this.lyncServer = lyncServer;
    }
}
