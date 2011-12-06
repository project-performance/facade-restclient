package com.ppc.commons.remote.httpclient.request;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

// rest method types
public enum RestMethodType {
    DELETE() {
        @Override
        public HttpMethodBase createMethod(String targetUri, NameValuePair[] params) {
            DeleteMethod delMethod = new DeleteMethod(targetUri);
            // concatenate parameters to the get method if any
            if (params != null && params.length > 0) {
                delMethod.setQueryString(params);
            }

            return delMethod;
        }
    },
    GET() {
        @Override
        public HttpMethodBase createMethod(String targetUri, NameValuePair[] params) {
            GetMethod getMethod = new GetMethod(targetUri);
            // concatenate parameters to the get method if any
            if (params != null && params.length > 0) {
                getMethod.setQueryString(params);
            }

            return getMethod;
        }
    },
    POST() {
        @Override
        public HttpMethodBase createMethod(String targetUri, NameValuePair[] params) {
            PostMethod postMethod = new PostMethod(targetUri);
            // concatenate parameters to the get method if any
            if (params != null && params.length > 0) {
                postMethod.addParameters(params);
            }

            return postMethod;
        }
    },
    PUT() {
        @Override
        public HttpMethodBase createMethod(String targetUri, NameValuePair[] params) {
            PutMethod putMethod = new PutMethod(targetUri);
            // concatenate parameters to the get method if any
            if (params != null && params.length > 0) {
                putMethod.setQueryString(params);
            }

            return putMethod;
        }
    };

    public abstract HttpMethodBase createMethod(String targetUri, NameValuePair[] params);
}