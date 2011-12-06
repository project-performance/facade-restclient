package com.ppc.commons.remote.httpclient.request.utils;

import com.ppc.commons.remote.httpclient.request.RestMethodType;
import com.ppc.commons.remote.httpclient.request.RestRequest;
import com.ppc.commons.remote.httpclient.request.beans.RestRequestBean;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.HashMap;
import java.util.Map;

public class RestRequestBuilder {
    static final Logger log = Logger.getLogger(RestRequestBuilder.class);

    private RestRequestBean restRequestBean = null;

    public RestRequestBuilder(RestRequestBean restRequestBean) {
        this.restRequestBean = restRequestBean;
    }

    public RestRequestBean getRestRequestBean() {
        return restRequestBean;
    }

    public RestRequest buildRestRequest(){
        final boolean isDebug = log.isDebugEnabled();

        if(null == this.restRequestBean){
            throw new IllegalArgumentException("Requestbean must not be null.");
        }

        String transformedRequestPath = getRestRequestBean().getRequestPath();
        if(isDebug)
            log.debug("Request Path = " + transformedRequestPath);

        if(transformedRequestPath == null){
            throw new IllegalArgumentException("The request path is mandatory.");
        }

        RestMethodType method = getRestRequestBean().getRequestMethod();
        if(isDebug)
            log.debug("RestRequest.HttpMethodType = " + method);

        if(method == null){
            throw new IllegalArgumentException("The request method is mandatory.");
        }

        //transform the targetURI
        if(StringUtils.isNotEmpty(transformedRequestPath)){
            HashMap<String,String> tokens = getRestRequestBean().getURITokenReplacements();
            if(null != tokens){
                for(Map.Entry<String,String> pair : tokens.entrySet()){
                    transformedRequestPath = transformedRequestPath.replace(pair.getKey(), pair.getValue());
                }
            }

            if(isDebug)
                log.debug("Transformed Request Path = " + transformedRequestPath);
        }

        String requestURI = transformedRequestPath;
        if(StringUtils.isNotEmpty(getRestRequestBean().getRequestBaseURI()))
            requestURI = getRestRequestBean().getRequestBaseURI() + requestURI;

        if(isDebug)
            log.debug("requestURI = " + requestURI);

        RestRequest restRequest = new RestRequest(method, requestURI, null);

        if(null != getRestRequestBean().getRequestHeaders()){
            if(isDebug){
                log.debug("Adding Headers");
                for(Map.Entry<String,String> pair : getRestRequestBean().getRequestHeaders().entrySet()){
                    log.debug("[" + pair.getKey() + "=" + pair.getValue() + "]");
                }
            }
            restRequest.addRequestHeaders(toNameValuePairs(getRestRequestBean().getRequestHeaders()));
        }

        //if it is a post, try to add the posted params
        if(method == RestMethodType.POST){
            if(isDebug){
                if(null != getRestRequestBean().getPostedParams()){
                    log.debug("Adding posted params");
                    for(Map.Entry<String,String> postPair : getRestRequestBean().getPostedParams().entrySet()){
                        log.debug("[" + postPair.getKey() + "=" + postPair.getValue() + "]");
                    }
                }
                if(null != getRestRequestBean().getPostedFile())
                    log.debug("with file: " + getRestRequestBean().getPostedFile().getName());
            }

            restRequest.addRequestMultiPartBody(getRestRequestBean().getPostedFile(), toNameValuePairs(getRestRequestBean().getPostedParams()));
        }

        if(null != getRestRequestBean().getQueryStringParameters()){
            if(isDebug){
                log.debug("Adding QueryStringParameters");
                for(Map.Entry<String,String> pair : getRestRequestBean().getQueryStringParameters().entrySet()){
                    log.debug("[" + pair.getKey() + "=" + pair.getValue() + "]");
                }
            }
            restRequest.setRequestQueryString(toNameValuePairs(getRestRequestBean().getQueryStringParameters()));
        }
        if(null != getRestRequestBean().getCookies()){
            if(isDebug)
                log.debug("Adding Cookies = " + getRestRequestBean().getCookies());
            restRequest.addRequestCookies(getRestRequestBean().getCookies());
        }

        if(null != getRestRequestBean().getRequestBody() && getRestRequestBean().getRequestBody().length == 3){
            if(isDebug)
                log.debug("Adding Request Body [\n" + getRestRequestBean().getRequestBody()[0] + "\n" + getRestRequestBean().getRequestBody()[1]  + "\n" +  getRestRequestBean().getRequestBody()[2] + "\n]");
            restRequest.addRequestStringBody(getRestRequestBean().getRequestBody()[0], getRestRequestBean().getRequestBody()[1], getRestRequestBean().getRequestBody()[2]);
        }
        else if(log.isEnabledFor(Priority.WARN) && null != getRestRequestBean().getRequestBody() && getRestRequestBean().getRequestBody().length < 3)
            log.warn("Request body is not valid...");

        return restRequest;
    }

    private NameValuePair[] toNameValuePairs(HashMap<String,String> params){
        NameValuePair[] nameValuePairs = null;
        if(null != params){
            nameValuePairs = new NameValuePair[params.size()];

            int index = 0;
            for(Map.Entry<String,String> pair : params.entrySet()){
                nameValuePairs[index] = new NameValuePair(pair.getKey(), pair.getValue());
                index++;
            }
        }
        return nameValuePairs;
    }
}
