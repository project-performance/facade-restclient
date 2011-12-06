package com.ppc.commons.remote.httpclient.request.beans;

import com.ppc.commons.remote.httpclient.request.RestMethodType;

import java.io.File;
import java.util.HashMap;

public interface RestRequestBean {
    public String getRequestBaseURI();
    public String getRequestPath();
    public RestMethodType getRequestMethod();
    public HashMap<String,String> getURITokenReplacements();
    public HashMap<String,String> getRequestHeaders();
    public HashMap<String,String> getPostedParams();
    public File getPostedFile();
    public HashMap<String,String> getQueryStringParameters();
    public String getCookies();
    public String[] getRequestBody();
}