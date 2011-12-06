package com.ppc.commons.remote.httpclient.response;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: Fabien Sanglier
 * Date: 7/23/11
 * Time: 12:25 AM
 * To change this template use File | Settings | File Templates.
 */
public interface RestResponseProcessor {
    public Object processRestResponseStream(Reader responseStreamReader) throws IOException;
}
