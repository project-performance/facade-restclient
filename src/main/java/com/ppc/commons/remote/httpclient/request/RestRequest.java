package com.ppc.commons.remote.httpclient.request;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

public class RestRequest {
    static final Logger log = Logger.getLogger(RestRequest.class);

    static final String HTTP_HEADER_COOKIE = "Cookie";

    public static final String FILE_PARAM_PREFIX = "filedata";

    private HttpMethodBase restmethod = null;

    public RestRequest(RestMethodType method, String targetUri, NameValuePair[] params){
        this.restmethod = createHttpMethod(method, targetUri, params);
    }

    public HttpMethodBase getRestmethod() {
        return restmethod;
    }

    public void setRestmethod(HttpMethodBase restmethod) {
        this.restmethod = restmethod;
    }

    private HttpMethodBase createHttpMethod(String methodName, String targetUri, NameValuePair[] params) throws UnsupportedOperationException {
        RestMethodType method = null;
        if(StringUtils.isEmpty(methodName)) {
            throw new UnsupportedOperationException("Method name must be specified.");
        }
        try {
            method = RestMethodType.valueOf(methodName.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("Could not find method matching the specified name: "+methodName,e);
        }
        return createHttpMethod(method, targetUri, params);
    }

    private HttpMethodBase createHttpMethod(RestMethodType method, String targetUri, NameValuePair[] params) throws UnsupportedOperationException {
        if(log.isDebugEnabled())
            log.debug("Creating method request: [" + method + "][" + targetUri + "]");

        // URL encode the URI
        targetUri = targetUri.replace(" ","%20");
        if(method == null) {
            throw new UnsupportedOperationException("Unknown HTTP method");
        }

        return method.createMethod(targetUri, params);
    }


    public void addRequestStringBody(String requestbody, String bodycontentType, String bodycharset){
        if(this.restmethod == null)
            throw new UnsupportedOperationException("Cannot add a request body when the http method is null. Make sure the http method is created first.");

        if (!(this.restmethod instanceof EntityEnclosingMethod))
            throw new UnsupportedOperationException("Cannot add a entity request body for a http method that does not support it.");

        RequestEntity bodyentity = null;
        try{
            bodyentity = new StringRequestEntity(requestbody, bodycontentType, bodycharset);
        } catch (UnsupportedEncodingException exc){
            log.warn("Could not encode the request body...", exc);
        }

        if(bodyentity != null){
            ((EntityEnclosingMethod)this.restmethod).setRequestEntity(bodyentity);
        }
    }

    public void addPostedParams(NameValuePair[] params){
        addRequestMultiPartBody((File[])null, params);
    }

    public void addRequestMultiPartBody(File file, NameValuePair[] params){
        File[] files = null;
        if(null != file){
            files = new File[1];
            files[0]=file;
        }
        addRequestMultiPartBody(files, params);
    }

    public void addRequestMultiPartBody(File[] files, NameValuePair[] params){
        Vector<Part> parts = new Vector<Part>();
        if(null != files && files.length > 0){
            if(files.length == 1){
                try{
                    parts.add(new FilePart(FILE_PARAM_PREFIX, files[0]));
                }catch (FileNotFoundException fne){
                    fne.printStackTrace();
                    log.warn("File " + files[0].getName() + " could not be found", fne);
                }
            } else {
                for (int i = 0; i < files.length; i++) {
                    try{
                        parts.add(new FilePart(FILE_PARAM_PREFIX + i, files[i]));
                    }catch (FileNotFoundException fne){
                        fne.printStackTrace();
                        log.warn("File " + files[i].getName() + " could not be found", fne);
                    }
                }
            }
        }

        if(null != params && params.length > 0){
            for(NameValuePair prm: params){
                if (log.isDebugEnabled())
                    log.debug(prm.getName() + ":" + prm.getValue());
                parts.add(new StringPart(prm.getName(), prm.getValue()));
            }
        }

        addRequestMultiPartBody(parts.toArray(new Part[parts.size()]));
    }

    public void addRequestMultiPartBody(Part[] parts){
        if(this.restmethod == null)
            throw new UnsupportedOperationException("Cannot add a request body when the http method is null. Make sure the http method is created first.");

        if (!(this.restmethod instanceof EntityEnclosingMethod))
            throw new UnsupportedOperationException("Cannot add a entity request body for a http method that does not support it.");

        if(null != parts && parts.length > 0){
            MultipartRequestEntity bodyentity = new MultipartRequestEntity(parts, this.restmethod.getParams());
            if(bodyentity != null){
                ((EntityEnclosingMethod)this.restmethod).setRequestEntity(bodyentity);
            }
        }
    }

    public void addRequestCookies(String cookies){
        // add cookies to header
        if (StringUtils.isNotEmpty(cookies)) {
            this.restmethod.addRequestHeader(HTTP_HEADER_COOKIE,cookies);
        }
    }

    public void addRequestCookies(List<Cookie> cookies){
        // add cookies to header
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                this.restmethod.addRequestHeader(HTTP_HEADER_COOKIE,cookie.toExternalForm());
            }
        }
    }

    public void setRequestQueryString(NameValuePair[] qs_params){
        // set query string params
        if (qs_params != null) {
            this.restmethod.setQueryString(qs_params);
        }
    }

    public void addRequestHeaders(NameValuePair param){
        // add headers to the method
        if (param != null) {
            this.restmethod.addRequestHeader(param.getName(), (String) param.getValue());
        }
    }

    public void addRequestHeaders(NameValuePair[] params){
        // add headers to the method
        if (params != null && params.length > 0) {
            for(NameValuePair pair : params){
                this.restmethod.addRequestHeader(pair.getName(), (String) pair.getValue());
            }
        }
    }
}
