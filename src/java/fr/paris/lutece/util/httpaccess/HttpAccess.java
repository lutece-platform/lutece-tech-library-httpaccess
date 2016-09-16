/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.util.httpaccess;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// TODO: Auto-generated Javadoc
/**
 * Http net Object Accessor <br/>.
 */
public class HttpAccess
{
    // proxy authentication settings

    /** The Constant PATTERN_FILENAME. */
    private static final String PATTERN_FILENAME = ".*filename=\"([^\"]+)";

    /** The Constant DEFAULT_MIME_TYPE. */
    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    /** The Constant DEFAULT_JSON_MIME_TYPE. */
    private static final String DEFAULT_JSON_MIME_TYPE = "application/json";

    /** The Constant SEPARATOR_CONTENT_TYPE. */
    private static final String SEPARATOR_CONTENT_TYPE = ";";

    /** The Constant PROPERTY_HEADER_CONTENT_DISPOSITION. */
    private static final String PROPERTY_HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    /** The Constant PROPERTY_HEADER_CONTENT_LENGTH. */
    private static final String PROPERTY_HEADER_CONTENT_LENGTH = "Content-Length";

    /** The Constant PROPERTY_HEADER_CONTENT_TYPE. */
    private static final String PROPERTY_HEADER_CONTENT_TYPE = "Content-Type";

    /** The Constant PROPERTY_CONTENT_CHARSET. */
    private static final String PROPERTY_CONTENT_CHARSET = "httpAccess.contentCharset";

    /** The Constant PROPERTY_HTTP_REQUEST_POST. */
    private static final String PROPERTY_HTTP_REQUEST_POST = "POST";

    /** The Constant PROPERTY_HTTP_REQUEST_PUT. */
    private static final String PROPERTY_HTTP_REQUEST_PUT = "PUT";

    /** The Constant JSON_CHARSET. */
    private static final String JSON_CHARSET = "UTF-8";

    /** The Constant DEFAULT_CHARSET. */
    private static final String DEFAULT_CHARSET = "ISO-8859-1";

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * @param strUrl The Url to access
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doGet( String strUrl ) throws HttpAccessException
    {
        return doGet( strUrl, null, null );
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * @param strUrl The Url to access
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doGet( String strUrl, RequestAuthenticator authenticator, List<String> listElements )
        throws HttpAccessException
    {
        return doGet( strUrl, authenticator, listElements, null );
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     *
     * @param strUrl The Url to access
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param headers the headers
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doGet( String strUrl, RequestAuthenticator authenticator, List<String> listElements,
        Map<String, String> headers ) throws HttpAccessException
    {
        return doGet( strUrl, authenticator, listElements, headers, null );
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * @param strUrl The Url to access
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param headersRequest Map of headers request parameters
     * @param headersResponse Map to contain response headers
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doGet( String strUrl, RequestAuthenticator authenticator, List<String> listElements,
        Map<String, String> headersRequest, Map<String, String> headersResponse )
        throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        HttpMethodBase method = new GetMethod( strUrl );
        method.setFollowRedirects( true );

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet(  ) )
            {
                method.setRequestHeader( entry.getKey(  ), entry.getValue(  ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );

            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error getting URL : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders(  ) )
                {
                    headersResponse.put( header.getName(  ), header.getValue(  ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString(  );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return strResponseBody;
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPost( String strUrl, Map<String, String> params )
        throws HttpAccessException
    {
        return doPost( strUrl, params, null, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPost( String strUrl, Map<String, String> params, RequestAuthenticator authenticator,
        List<String> listElements ) throws HttpAccessException
    {
        return doPost( strUrl, params, authenticator, listElements, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param headersRequest Map of headers request parameters
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPost( String strUrl, Map<String, String> params, RequestAuthenticator authenticator,
        List<String> listElements, Map<String, String> headersRequest )
        throws HttpAccessException
    {
        return doPost( strUrl, params, authenticator, listElements, headersRequest, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param headersRequest Map of headers request parameters
     * @param headersResponse Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPost( String strUrl, Map<String, String> params, RequestAuthenticator authenticator,
        List<String> listElements, Map<String, String> headersRequest, Map<String, String> headersResponse )
        throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        PostMethod method = new PostMethod( strUrl );

        if ( params != null )
        {
            for ( Entry<String, String> entry : params.entrySet(  ) )
            {
                method.addParameter( entry.getKey(  ), entry.getValue(  ) );
            }
        }

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet(  ) )
            {
                method.setRequestHeader( entry.getKey(  ), entry.getValue(  ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Posting URL : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders(  ) )
                {
                    headersResponse.put( header.getName(  ), header.getValue(  ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString(  );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return strResponseBody;
    }

    /**
     * Do request enclosing method.
     *
     * @param strUrl the str url
     * @param strMethod the str method
     * @param strContent the str content
     * @param contentType the content type
     * @param charset the charset
     * @param authenticator the authenticator
     * @param listElements the list elements
     * @param headersRequest the headers request
     * @param headersResponse the headers response
     * @return the string
     * @throws HttpAccessException the http access exception
     */
    public String doRequestEnclosingMethod( String strUrl, String strMethod, String strContent, String contentType,
        String charset, RequestAuthenticator authenticator, List<String> listElements,
        Map<String, String> headersRequest, Map<String, String> headersResponse )
        throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        EntityEnclosingMethod method;

        switch ( strMethod )
        {
            case PROPERTY_HTTP_REQUEST_PUT:
                method = new PutMethod( strUrl );

                break;

            case PROPERTY_HTTP_REQUEST_POST:
                method = new PostMethod( strUrl );

                break;

            default:
                method = new PostMethod( strUrl );

                break;
        }

        try
        {
            StringRequestEntity requestEntity = new StringRequestEntity( strContent, contentType, charset );

            if ( requestEntity != null )
            {
                method.setRequestEntity( requestEntity );
            }
        }
        catch ( UnsupportedEncodingException e )
        {
            String strError = "HttpAccess - Error data to '" + strContent + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet(  ) )
            {
                method.setRequestHeader( entry.getKey(  ), entry.getValue(  ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Posting URL : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders(  ) )
                {
                    headersResponse.put( header.getName(  ), header.getValue(  ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString(  );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return strResponseBody;
    }

    /**
     * Do post json.
     *
     * @param strUrl the str url
     * @param strJSON the str json
     * @param authenticator the authenticator
     * @param listElements the list elements
     * @param headersRequest the headers request
     * @param headersResponse the headers response
     * @return the string
     * @throws HttpAccessException the http access exception
     */
    public String doPostJSON( String strUrl, String strJSON, RequestAuthenticator authenticator,
        List<String> listElements, Map<String, String> headersRequest, Map<String, String> headersResponse )
        throws HttpAccessException
    {
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_POST, strJSON, DEFAULT_JSON_MIME_TYPE,
            JSON_CHARSET, authenticator, listElements, headersRequest, headersResponse );
    }

    /**
     * Do post json.
     *
     * @param strUrl the str url
     * @param strJSON the str json
     * @param headersRequest the headers request
     * @param headersResponse the headers response
     * @return the string
     * @throws HttpAccessException the http access exception
     */
    public String doPostJSON( String strUrl, String strJSON, Map<String, String> headersRequest,
        Map<String, String> headersResponse ) throws HttpAccessException
    {
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_POST, strJSON, DEFAULT_JSON_MIME_TYPE,
            JSON_CHARSET, null, null, headersRequest, headersResponse );
    }

    /**
     * Do put json.
     *
     * @param strUrl the str url
     * @param strJSON the str json
     * @param authenticator the authenticator
     * @param listElements the list elements
     * @param headersRequest the headers request
     * @param headersResponse the headers response
     * @return the string
     * @throws HttpAccessException the http access exception
     */
    public String doPutJSON( String strUrl, String strJSON, RequestAuthenticator authenticator,
        List<String> listElements, Map<String, String> headersRequest, Map<String, String> headersResponse )
        throws HttpAccessException
    {
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_PUT, strJSON, DEFAULT_JSON_MIME_TYPE,
            JSON_CHARSET, authenticator, listElements, headersRequest, headersResponse );
    }

    /**
     * Do put json.
     *
     * @param strUrl the str url
     * @param strJSON the str json
     * @param headersRequest the headers request
     * @param headersResponse the headers response
     * @return the string
     * @throws HttpAccessException the http access exception
     */
    public String doPutJSON( String strUrl, String strJSON, Map<String, String> headersRequest,
        Map<String, String> headersResponse ) throws HttpAccessException
    {
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_PUT, strJSON, DEFAULT_JSON_MIME_TYPE,
            JSON_CHARSET, null, null, headersRequest, headersResponse );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPostMultiValues( String strUrl, Map<String, List<String>> params )
        throws HttpAccessException
    {
        return doPostMultiValues( strUrl, params, null, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPostMultiValues( String strUrl, Map<String, List<String>> params,
        RequestAuthenticator authenticator, List<String> listElements )
        throws HttpAccessException
    {
        return doPostMultiValues( strUrl, params, authenticator, listElements, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param headersRequest Map of headers request parameters
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPostMultiValues( String strUrl, Map<String, List<String>> params,
        RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headersRequest )
        throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;
        PostMethod method = new PostMethod( strUrl );

        for ( Entry<String, List<String>> entry : params.entrySet(  ) )
        {
            String strParameter = entry.getKey(  );
            List<String> values = entry.getValue(  );

            for ( String strValue : values )
            {
                method.addParameter( strParameter, strValue );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Posting URL : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            strResponseBody = method.getResponseBodyAsString(  );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return strResponseBody;
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param headersRequest Map of headers request parameters
     * @param headersResponse Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPostMultiValues( String strUrl, Map<String, List<String>> params,
        RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headersRequest,
        Map<String, String> headersResponse ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;
        PostMethod method = new PostMethod( strUrl );

        for ( Entry<String, List<String>> entry : params.entrySet(  ) )
        {
            String strParameter = entry.getKey(  );
            List<String> values = entry.getValue(  );

            for ( String strValue : values )
            {
                method.addParameter( strParameter, strValue );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Posting URL : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            strResponseBody = method.getResponseBodyAsString(  );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return strResponseBody;
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param fileItems The list of file items
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPostMultiPart( String strUrl, Map<String, List<String>> params, Map<String, FileItem> fileItems )
        throws HttpAccessException
    {
        return doPostMultiPart( strUrl, params, fileItems, null, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param fileItems The list of file items
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPostMultiPart( String strUrl, Map<String, List<String>> params, Map<String, FileItem> fileItems,
        RequestAuthenticator authenticator, List<String> listElements )
        throws HttpAccessException
    {
        return doPostMultiPart( strUrl, params, fileItems, authenticator, listElements, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param fileItems The list of file items
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param headersRequest Map of headers request parameters
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPostMultiPart( String strUrl, Map<String, List<String>> params, Map<String, FileItem> fileItems,
        RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headersRequest )
        throws HttpAccessException
    {
        return doPostMultiPart( strUrl, params, fileItems, authenticator, listElements, headersRequest, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param params the list of parameters to post
     * @param fileItems The list of file items
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param headersRequest Map of headers request parameters
     * @param headersResponse Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPostMultiPart( String strUrl, Map<String, List<String>> params, Map<String, FileItem> fileItems,
        RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headersRequest,
        Map<String, String> headersResponse ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;
        PostMethod method = new PostMethod( strUrl );

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet(  ) )
            {
                method.setRequestHeader( entry.getKey(  ), entry.getValue(  ) );
            }
        }
        ArrayList<Part> parts = new ArrayList<Part>();

        if ( ( fileItems != null ) && !fileItems.isEmpty(  ) )
        {
            // Store the Files
            for ( Entry<String, FileItem> paramFileItem : fileItems.entrySet(  ) )
            {
                FileItem fileItem = paramFileItem.getValue(  );

                if ( fileItem != null )
                {
                    File file = new File( fileItem.getName(  ) );

                    try
                    {
                        fileItem.write( file );
                        parts.add( new FilePart( paramFileItem.getKey(  ), file ) );
                    }
                    catch ( Exception e )
                    {
                        String strError = "HttpAccess - Error writing file '" + fileItem.getName(  ) + "' : ";
                        AppLogService.error( strError + e.getMessage(  ), e );
                        throw new HttpAccessException( strError + e.getMessage(  ), e );
                    }
                }
            }
        }

        if( ( params != null ) && !params.isEmpty(  ) )
        {
	        // Additionnal parameters
	        for ( Entry<String, List<String>> param : params.entrySet(  ) )
	        {
	            for ( String strValue : param.getValue(  ) )
	            {
	            	parts.add( new StringPart( param.getKey(  ), strValue ) );
	            }
	        }
        }
        
        if ( ! parts.isEmpty(  ) )
        {        	
	        method.setRequestEntity( new MultipartRequestEntity( parts.toArray(new Part[]{}), method.getParams(  ) ) );
        }
        
        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Posting URL : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders(  ) )
                {
                    headersResponse.put( header.getName(  ), header.getValue(  ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString(  );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return strResponseBody;
    }

    /**
     * Send a PUT HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param params the params
     * @param headersRequest Map of headers request parameters
     * @param headersResponse Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doPut( String strUrl, RequestAuthenticator authenticator, List<String> listElements,
        Map<String, String> params, Map<String, String> headersRequest, Map<String, String> headersResponse )
        throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        PutMethod method = new PutMethod( strUrl );

        if ( ( params != null ) && ( params.size(  ) > 0 ) )
        {
            NameValuePair[] putParameters = new NameValuePair[params.size(  )];
            int nCpt = 0;

            for ( Entry<String, String> entry : params.entrySet(  ) )
            {
                putParameters[nCpt++] = new NameValuePair( entry.getKey(  ), entry.getValue(  ) );
            }

            method.setRequestEntity( new ByteArrayRequestEntity( 
                    EncodingUtil.formUrlEncode( putParameters,
                        AppPropertiesService.getProperty( PROPERTY_CONTENT_CHARSET, DEFAULT_CHARSET ) ).getBytes(  ) ) );
        }

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet(  ) )
            {
                method.setRequestHeader( entry.getKey(  ), entry.getValue(  ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Puting URL : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders(  ) )
                {
                    headersResponse.put( header.getName(  ), header.getValue(  ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString(  );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return strResponseBody;
    }

    /**
     * Send a DELETE HTTP request to an url and return the response content.
     *
     * @param strUrl the url to access
     * @param authenticator The {@link RequestAuthenticator}
     * @param listElements to include in the signature
     * @param headersRequest Map of headers request parameters
     * @param headersResponse Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doDelete( String strUrl, RequestAuthenticator authenticator, List<String> listElements,
        Map<String, String> headersRequest, Map<String, String> headersResponse )
        throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        DeleteMethod method = new DeleteMethod( strUrl );

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet(  ) )
            {
                method.setRequestHeader( entry.getKey(  ), entry.getValue(  ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Deleting URL : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders(  ) )
                {
                    headersResponse.put( header.getName(  ), header.getValue(  ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString(  );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return strResponseBody;
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * @param strUrl The Url to access
     * @param strFilePath the file path
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public void downloadFile( String strUrl, String strFilePath )
        throws HttpAccessException
    {
        HttpMethodBase method = new GetMethod( strUrl );
        method.setFollowRedirects( true );

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Downloading File : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            bis = new BufferedInputStream( method.getResponseBodyAsStream(  ) );

            FileOutputStream fos = new FileOutputStream( strFilePath );
            bos = new BufferedOutputStream( fos );

            int bytes;

            while ( ( bytes = bis.read(  ) ) > -1 )
            {
                bos.write( bytes );
            }
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Unable to connect to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            try
            {
                if ( bis != null )
                {
                    bis.close(  );
                }

                if ( bos != null )
                {
                    bos.close(  );
                }
            }
            catch ( IOException e )
            {
                AppLogService.error( "HttpAccess - Error closing stream : " + e.getMessage(  ), e );
                throw new HttpAccessException( e.getMessage(  ), e );
            }

            // Release the connection.
            method.releaseConnection(  );
        }
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * @param strUrl The Url to access
     * @return the file name
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String getFileName( String strUrl ) throws HttpAccessException
    {
        String strFileName = null;
        HttpMethodBase method = new GetMethod( strUrl );
        method.setFollowRedirects( true );

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Downloading File : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            Header headerContentDisposition = method.getResponseHeader( PROPERTY_HEADER_CONTENT_DISPOSITION );

            if ( headerContentDisposition != null )
            {
                String headerValue = headerContentDisposition.getValue(  );
                Pattern p = Pattern.compile( PATTERN_FILENAME );
                Matcher matcher = p.matcher( headerValue );

                if ( matcher.matches(  ) )
                {
                    strFileName = matcher.group( 1 );
                }
            }
            else
            {
                String[] tab = strUrl.split( "/" );
                strFileName = tab[tab.length - 1];
            }

            method.abort(  );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Unable to connect to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return strFileName;
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * @param strUrl The Url to access
     * @return a {@link FileItem}
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public FileItem downloadFile( String strUrl ) throws HttpAccessException
    {
        HttpMethodBase method = new GetMethod( strUrl );
        method.setFollowRedirects( true );

        MemoryFileItem fileItem = null;

        try
        {
            HttpClient client = HttpAccessService.getInstance(  ).getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( !HttpAccessService.getInstance(  ).matchResponseCodeAuthorized( nResponse ) )
            {
                String strError = "HttpAccess - Error Downloading File : " + strUrl + " - return code : " + nResponse;
                throw new HttpAccessException( strError, nResponse, null );
            }

            // Get the file name
            String strFileName = StringUtils.EMPTY;
            Header headerContentDisposition = method.getResponseHeader( PROPERTY_HEADER_CONTENT_DISPOSITION );

            if ( headerContentDisposition != null )
            {
                String headerValue = headerContentDisposition.getValue(  );
                Pattern p = Pattern.compile( PATTERN_FILENAME );
                Matcher matcher = p.matcher( headerValue );

                if ( matcher.matches(  ) )
                {
                    strFileName = matcher.group( 1 );
                }
            }
            else
            {
                String[] tab = strUrl.split( "/" );
                strFileName = tab[tab.length - 1];
            }

            // Get the file size
            long lSize = 0;
            Header headerContentLength = method.getResponseHeader( PROPERTY_HEADER_CONTENT_LENGTH );

            if ( headerContentLength != null )
            {
                lSize = Long.parseLong( headerContentLength.getValue(  ) );
            }

            // Get the content type of the file
            String strContentType = StringUtils.EMPTY;

            Header headerContentType = method.getResponseHeader( PROPERTY_HEADER_CONTENT_TYPE );

            if ( headerContentType != null )
            {
                strContentType = headerContentType.getValue(  );

                if ( StringUtils.isNotBlank( strContentType ) )
                {
                    int nIndexOfSeparator = strContentType.indexOf( SEPARATOR_CONTENT_TYPE );
                    strContentType = strContentType.substring( 0, nIndexOfSeparator );
                }
            }

            if ( StringUtils.isBlank( strContentType ) )
            {
                strContentType = DEFAULT_MIME_TYPE;
            }

            fileItem = new MemoryFileItem( method.getResponseBody(  ), strFileName, lSize, strContentType );
        }
        catch ( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            String strError = "HttpAccess - Unable to connect to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError + e.getMessage(  ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }

        return fileItem;
    }
}
