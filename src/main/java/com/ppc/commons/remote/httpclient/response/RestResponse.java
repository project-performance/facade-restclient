package com.ppc.commons.remote.httpclient.response;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: fabien
 * Date: 10/27/11
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class RestResponse {
    static final Logger log = Logger.getLogger(RestResponse.class);

    private Object responseData;
    private int statusCode;
    private String statusMessage;
    private Map<String, String> responseHeaders = new HashMap<String, String>(16, 1.0f);

    public RestResponse(Object responseData, int statusCode, String statusMessage) {
        this.responseData = responseData;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    /**
     * Gets the response.
     *
     * @return the data stream from the response object - will be null on error
     *         or if the response has already been streamed to an OutputStream.
     */
    public Object getResponseData()
    {
        return this.responseData;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Allows for response headers to be stored onto the status
     *
     * @param headerName name of the header
     * @param headerValue value of the header
     */
    public void addResponseHeader(String headerName, String headerValue)
    {
        this.responseHeaders.put(headerName, headerValue);
    }

    /**
     * Retrieves response headers
     *
     * @return map of response headers
     */
    public Map<String, String> getResponseHeader()
    {
        return this.responseHeaders;
    }
}