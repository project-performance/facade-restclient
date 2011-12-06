package com.ppc.commons.remote.httpclient;

import com.ppc.commons.remote.httpclient.request.RestRequest;
import com.ppc.commons.remote.httpclient.response.RestResponse;
import com.ppc.commons.remote.httpclient.response.RestResponseProcessor;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;

/*
 * 
 * Created by Fabien Sanglier
 * 
 */
public class RestClient {
    static final Logger log = Logger.getLogger(RestClient.class);

    /**
     * the default maximum connect time
     */
    private static final int CONNECT_TIMEOUT = 5000; // 5 seconds

    /**
     * the default maximum read time
     */
    private static final int READ_TIMEOUT = 20000;   // 20 seconds

    /**
     * the default maximum number of connections per host
     */
    private static final int MAX_HOST_CONNECTIONS = 300;

    // share http client connection manager and proxy hosts
    private static HttpConnectionManager s_httpConnectionManager;

    //this is the base url of the endpoint for this restclient
    private String baseEndpont;

    /**
     * Initialise the HTTP clients
     */
    static
    {
        final HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
        connectionManagerParams.setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, MAX_HOST_CONNECTIONS);
        connectionManagerParams.setMaxTotalConnections(MAX_HOST_CONNECTIONS);
        connectionManagerParams.setConnectionTimeout(CONNECT_TIMEOUT);
        connectionManagerParams.setSoTimeout(READ_TIMEOUT);
        connectionManagerParams.setTcpNoDelay(true);
        s_httpConnectionManager = new MultiThreadedHttpConnectionManager();
        s_httpConnectionManager.setParams(connectionManagerParams);
    }

    public RestClient(String baseEndpont) {
        this.baseEndpont = baseEndpont;
    }

    /**
     * Create and configure an HTTP Client based on the multi-threaded connection manager
     *
     */
    private static HttpClient createHttpClient()
    {
        return new HttpClient(s_httpConnectionManager);
    }

    /*
     * Note : It is recommended to consume the HTTP response body as a stream of bytes/characters using HttpMethod#getResponseBodyAsStream method.
     * The use of HttpMethod#getResponseBody and HttpMethod#getResponseBodyAsString are strongly discouraged.
     *
     * That's why we did change all the calls to use a Reader rather than a Buffered String
     */
    public RestResponse execute(RestRequest restRequest, RestResponseProcessor restResponseProcessor) throws IOException, HttpException, ConnectException, AuthenticationException {
        RestResponse restResponse = null;

        // execute a HTTP method call
        HttpMethodBase restMethod = restRequest.getRestmethod();

        try {
            //execute the method
            int statusCode = executeHttpMethod(restMethod);

            //parse response code for possible authentication errors
            if (statusCode == HttpServletResponse.SC_FORBIDDEN
                    || statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
                throw new AuthenticationException("A authentication error response code was returned: " + statusCode);
            }

            //get the response stream
            InputStream responseStream = restMethod.getResponseBodyAsStream();

            //it is possible that the repsonse stream be null, in the case of an action that does not return content
            Object responseData = null;
            if(null != responseStream){
                Reader responseResult = new InputStreamReader(restMethod.getResponseBodyAsStream(), restMethod.getRequestCharSet());

                //processing response
                responseData = restResponseProcessor.processRestResponseStream(responseResult);
            }

            restResponse = new RestResponse(responseData, restMethod.getStatusCode(), restMethod.getStatusText());

            // walk over headers that are returned from the connection
            // and store headers in the response
            for (Header header : restMethod.getResponseHeaders())
            {
                //handle the fact that the key can be null.
                final String key = header.getName();
                if (key != null)
                {
                    restResponse.addResponseHeader(key, header.getValue());
                }
            }
        } catch (ConnectException e) {
            log.error("An error happened during connection to remote peer.", e);
            throw e;
        } catch (HttpException httpe) {
            log.error("A HTTP error happened.", httpe);
            throw httpe;
        } catch (IOException e) {
            log.error("A I/O error happened.", e);
            throw e;
        } finally {
            restMethod.releaseConnection();
        }
        return restResponse;
    }

    private int executeHttpMethod(HttpMethodBase httpMethod)
            throws IOException, HttpException, ConnectException {

        if(log.isDebugEnabled())
            log.debug("Executing Request:" + httpMethod.getName() + " " + httpMethod.getURI());

        HttpClient httpClient = createHttpClient();
        int statusCode = httpClient.executeMethod(httpMethod);

        if(log.isDebugEnabled())
            log.debug("Status code:" + statusCode);

        return statusCode;
    }
}
