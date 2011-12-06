package com.ppc.commons.remote.httpclient.request.beans;

import com.ppc.commons.remote.httpclient.request.RestRequest;
import com.ppc.commons.remote.httpclient.request.utils.RestRequestBuilder;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: fabien
 * Date: 10/30/11
 * Time: 10:44 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractRequestBean implements RestRequestBean {
    static final Logger log = Logger.getLogger(AbstractRequestBean.class);

    public RestRequest buildRestRequest() {
        return new RestRequestBuilder(this).buildRestRequest();
    }
}
