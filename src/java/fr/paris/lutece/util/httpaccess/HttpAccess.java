/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;

/**
 * Http net Object Accessor
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

    private ResponseStatusValidator _responseValidator;
    private HttpAccessService _accessService;

    public HttpAccess( )
    {
        _accessService = HttpAccessService.getInstance( );
        _responseValidator = HttpAccessService.getInstance( );
    }

    public HttpAccess( ResponseStatusValidator validator )
    {
        _accessService = HttpAccessService.getInstance( );
        _responseValidator = validator;
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * 
     * @param strUrl
     *            The Url to access
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doGet( String strUrl ) throws HttpAccessException
    {
        return doGet( strUrl, null, null );
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * 
     * @param strUrl
     *            The Url to access
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doGet( String strUrl, RequestAuthenticator authenticator, List<String> listElements ) throws HttpAccessException
    {
        return doGet( strUrl, authenticator, listElements, null );
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     *
     * @param strUrl
     *            The Url to access
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param headers
     *            the headers
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doGet( String strUrl, RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headers ) throws HttpAccessException
    {
        return doGet( strUrl, authenticator, listElements, headers, null );
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * 
     * @param strUrl
     *            The Url to access
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param headersRequest
     *            Map of headers request parameters
     * @param headersResponse
     *            Map to contain response headers
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doGet( String strUrl, RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headersRequest,
            Map<String, String> headersResponse ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        HttpMethodBase method = new GetMethod( strUrl );
        method.setFollowRedirects( true );

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet( ) )
            {
                method.setRequestHeader( entry.getKey( ), entry.getValue( ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );

            int nResponse = client.executeMethod( method );
            validateResponseStatus( nResponse, method, strUrl );

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders( ) )
                {
                    headersResponse.put( header.getName( ), header.getValue( ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString( );
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            // Release the connection.
            _accessService.releaseConnection( client, method );
        }

        return strResponseBody;
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPost( String strUrl, Map<String, String> params ) throws HttpAccessException
    {
        return doPost( strUrl, params, null, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPost( String strUrl, Map<String, String> params, RequestAuthenticator authenticator, List<String> listElements ) throws HttpAccessException
    {
        return doPost( strUrl, params, authenticator, listElements, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param headersRequest
     *            Map of headers request parameters
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPost( String strUrl, Map<String, String> params, RequestAuthenticator authenticator, List<String> listElements,
            Map<String, String> headersRequest ) throws HttpAccessException
    {
        return doPost( strUrl, params, authenticator, listElements, headersRequest, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param headersRequest
     *            Map of headers request parameters
     * @param headersResponse
     *            Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPost( String strUrl, Map<String, String> params, RequestAuthenticator authenticator, List<String> listElements,
            Map<String, String> headersRequest, Map<String, String> headersResponse ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        PostMethod method = new PostMethod( strUrl );

        if ( params != null )
        {
            for ( Entry<String, String> entry : params.entrySet( ) )
            {
                method.addParameter( entry.getKey( ), entry.getValue( ) );
            }
        }

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet( ) )
            {
                method.setRequestHeader( entry.getKey( ), entry.getValue( ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );
            int nResponse = client.executeMethod( method );
            validateResponseStatus( nResponse, method, strUrl );

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders( ) )
                {
                    headersResponse.put( header.getName( ), header.getValue( ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString( );
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            // Release the connection.
            _accessService.releaseConnection( client, method );
        }

        return strResponseBody;
    }

    /**
     * Do request enclosing method.
     *
     * @param strUrl
     *            the str url
     * @param strMethod
     *            the str method
     * @param strContent
     *            the str content
     * @param contentType
     *            the content type
     * @param charset
     *            the charset
     * @param authenticator
     *            the authenticator
     * @param listElements
     *            the list elements
     * @param headersRequest
     *            the headers request
     * @param headersResponse
     *            the headers response
     * @return the string
     * @throws HttpAccessException
     *             the http access exception
     */
    public String doRequestEnclosingMethod( String strUrl, String strMethod, String strContent, String contentType, String charset,
            RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headersRequest, Map<String, String> headersResponse )
            throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        EntityEnclosingMethod method;

        switch( strMethod )
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
            method.setRequestEntity( requestEntity );
        }
        catch( UnsupportedEncodingException e )
        {
            throwHttpAccessException( strUrl, e );
        }

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet( ) )
            {
                method.setRequestHeader( entry.getKey( ), entry.getValue( ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );
            int nResponse = client.executeMethod( method );
            validateResponseStatus( nResponse, method, strUrl );

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders( ) )
                {
                    headersResponse.put( header.getName( ), header.getValue( ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString( );
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            // Release the connection.
            _accessService.releaseConnection( client, method );
        }

        return strResponseBody;
    }

    /**
     * Do post json.
     *
     * @param strUrl
     *            the str url
     * @param strJSON
     *            the str json
     * @param authenticator
     *            the authenticator
     * @param listElements
     *            the list elements
     * @param headersRequest
     *            the headers request
     * @param headersResponse
     *            the headers response
     * @return the string
     * @throws HttpAccessException
     *             the http access exception
     */
    public String doPostJSON( String strUrl, String strJSON, RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headersRequest,
            Map<String, String> headersResponse ) throws HttpAccessException
    {
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_POST, strJSON, DEFAULT_JSON_MIME_TYPE, JSON_CHARSET, authenticator, listElements,
                headersRequest, headersResponse );
    }

    /**
     * Do post json.
     *
     * @param strUrl
     *            the str url
     * @param strJSON
     *            the str json
     * @param headersRequest
     *            the headers request
     * @param headersResponse
     *            the headers response
     * @return the string
     * @throws HttpAccessException
     *             the http access exception
     */
    public String doPostJSON( String strUrl, String strJSON, Map<String, String> headersRequest, Map<String, String> headersResponse )
            throws HttpAccessException
    {
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_POST, strJSON, DEFAULT_JSON_MIME_TYPE, JSON_CHARSET, null, null, headersRequest,
                headersResponse );
    }

    /**
     * Do put json.
     *
     * @param strUrl
     *            the str url
     * @param strJSON
     *            the str json
     * @param authenticator
     *            the authenticator
     * @param listElements
     *            the list elements
     * @param headersRequest
     *            the headers request
     * @param headersResponse
     *            the headers response
     * @return the string
     * @throws HttpAccessException
     *             the http access exception
     */
    public String doPutJSON( String strUrl, String strJSON, RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headersRequest,
            Map<String, String> headersResponse ) throws HttpAccessException
    {
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_PUT, strJSON, DEFAULT_JSON_MIME_TYPE, JSON_CHARSET, authenticator, listElements,
                headersRequest, headersResponse );
    }

    /**
     * Do put json.
     *
     * @param strUrl
     *            the str url
     * @param strJSON
     *            the str json
     * @param headersRequest
     *            the headers request
     * @param headersResponse
     *            the headers response
     * @return the string
     * @throws HttpAccessException
     *             the http access exception
     */
    public String doPutJSON( String strUrl, String strJSON, Map<String, String> headersRequest, Map<String, String> headersResponse ) throws HttpAccessException
    {
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_PUT, strJSON, DEFAULT_JSON_MIME_TYPE, JSON_CHARSET, null, null, headersRequest,
                headersResponse );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPostMultiValues( String strUrl, Map<String, List<String>> params ) throws HttpAccessException
    {
        return doPostMultiValues( strUrl, params, null, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPostMultiValues( String strUrl, Map<String, List<String>> params, RequestAuthenticator authenticator, List<String> listElements )
            throws HttpAccessException
    {
        return doPostMultiValues( strUrl, params, authenticator, listElements, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param headersRequest
     *            Map of headers request parameters
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPostMultiValues( String strUrl, Map<String, List<String>> params, RequestAuthenticator authenticator, List<String> listElements,
            Map<String, String> headersRequest ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;
        PostMethod method = new PostMethod( strUrl );

        for ( Entry<String, List<String>> entry : params.entrySet( ) )
        {
            String strParameter = entry.getKey( );
            List<String> values = entry.getValue( );

            for ( String strValue : values )
            {
                method.addParameter( strParameter, strValue );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );
            int nResponse = client.executeMethod( method );

            validateResponseStatus( nResponse, method, strUrl );
            strResponseBody = method.getResponseBodyAsString( );
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            // Release the connection.
            _accessService.releaseConnection( client, method );
        }

        return strResponseBody;
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param headersRequest
     *            Map of headers request parameters
     * @param headersResponse
     *            Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPostMultiValues( String strUrl, Map<String, List<String>> params, RequestAuthenticator authenticator, List<String> listElements,
            Map<String, String> headersRequest, Map<String, String> headersResponse ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;
        PostMethod method = new PostMethod( strUrl );

        for ( Entry<String, List<String>> entry : params.entrySet( ) )
        {
            String strParameter = entry.getKey( );
            List<String> values = entry.getValue( );

            for ( String strValue : values )
            {
                method.addParameter( strParameter, strValue );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );
            int nResponse = client.executeMethod( method );
            validateResponseStatus( nResponse, method, strUrl );
            strResponseBody = method.getResponseBodyAsString( );
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            // Release the connection.
            _accessService.releaseConnection( client, method );
        }

        return strResponseBody;
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param fileItems
     *            The list of file items
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPostMultiPart( String strUrl, Map<String, List<String>> params, Map<String, FileItem> fileItems ) throws HttpAccessException
    {
        return doPostMultiPart( strUrl, params, fileItems, null, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param fileItems
     *            The list of file items
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPostMultiPart( String strUrl, Map<String, List<String>> params, Map<String, FileItem> fileItems, RequestAuthenticator authenticator,
            List<String> listElements ) throws HttpAccessException
    {
        return doPostMultiPart( strUrl, params, fileItems, authenticator, listElements, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param fileItems
     *            The list of file items
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param headersRequest
     *            Map of headers request parameters
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPostMultiPart( String strUrl, Map<String, List<String>> params, Map<String, FileItem> fileItems, RequestAuthenticator authenticator,
            List<String> listElements, Map<String, String> headersRequest ) throws HttpAccessException
    {
        return doPostMultiPart( strUrl, params, fileItems, authenticator, listElements, headersRequest, null );
    }

    /**
     * Send a POST HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param params
     *            the list of parameters to post
     * @param fileItems
     *            The list of file items
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param headersRequest
     *            Map of headers request parameters
     * @param headersResponse
     *            Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPostMultiPart( String strUrl, Map<String, List<String>> params, Map<String, FileItem> fileItems, RequestAuthenticator authenticator,
            List<String> listElements, Map<String, String> headersRequest, Map<String, String> headersResponse ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;
        PostMethod method = new PostMethod( strUrl );

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet( ) )
            {
                method.setRequestHeader( entry.getKey( ), entry.getValue( ) );
            }
        }

        ArrayList<Part> parts = new ArrayList<Part>( );
        ArrayList<File> listFiles = new ArrayList<File>( );

        try
        {
            if ( ( fileItems != null ) && !fileItems.isEmpty( ) )
            {
                // Store the Files
                for ( Entry<String, FileItem> paramFileItem : fileItems.entrySet( ) )
                {
                    FileItem fileItem = paramFileItem.getValue( );

                    if ( fileItem != null )
                    {
                        try
                        {
                            String strContentType = null;
                            String strCharset = null;
                            if ( StringUtils.isNotBlank( fileItem.getContentType( ) ) )
                            {
                                String [ ] splitContentType = StringUtils.split( fileItem.getContentType( ), SEPARATOR_CONTENT_TYPE );
                                if ( splitContentType.length > 0 && StringUtils.isNotBlank( splitContentType [0] ) )
                                {
                                    strContentType = splitContentType [0];
                                }
                                if ( splitContentType.length > 1 && StringUtils.isNotBlank( splitContentType [1] ) )
                                {
                                    strCharset = splitContentType [1];
                                }
                            }

                            PartSource partSource;
                            if ( fileItem.isInMemory( ) )
                            {
                                partSource = new ByteArrayPartSource( fileItem.getName( ), fileItem.get( ) );
                            }
                            else
                            {
                                File file = File.createTempFile( "httpaccess-multipart-", null );
                                // Store files for deletion after the request completed
                                listFiles.add( file );
                                fileItem.write( file );
                                partSource = new FilePartSource( fileItem.getName( ), file );
                            }

                            FilePart part = new FilePart( paramFileItem.getKey( ), partSource, strContentType, strCharset );
                            if ( strContentType != null && strCharset == null )
                            {
                                // Commons httpclient in the constructor of FilePart replaces null by ISO-8859-1
                                // Undo this explicitly when strCharset is null because we don't want to send
                                // things like "Content-Type: image/jpeg ; charset=ISO-8859-1"
                                part.setCharSet( null );
                            }
                            parts.add( part );
                        }
                        catch( Exception e )
                        {
                            String strError = "HttpAccess - Error writing file '" + fileItem.getName( ) + "' : ";
                            AppLogService.error( strError + e.getMessage( ), e );
                            throw new HttpAccessException( strError + e.getMessage( ), e );
                        }
                    }
                }
            }

            if ( ( params != null ) && !params.isEmpty( ) )
            {
                String charset = AppPropertiesService.getProperty( PROPERTY_CONTENT_CHARSET, DEFAULT_CHARSET );
                // Additionnal parameters
                for ( Entry<String, List<String>> param : params.entrySet( ) )
                {
                    for ( String strValue : param.getValue( ) )
                    {
                        parts.add( new StringPart( param.getKey( ), strValue, charset ) );
                    }
                }
            }

            if ( !parts.isEmpty( ) )
            {
                method.setRequestEntity( new MultipartRequestEntity( parts.toArray( new Part [ ] { } ), method.getParams( ) ) );
            }

            if ( authenticator != null )
            {
                authenticator.authenticateRequest( method, listElements );
            }

            HttpClient client = null;
            try
            {
                client = _accessService.getHttpClient( method );
                int nResponse = client.executeMethod( method );
                validateResponseStatus( nResponse, method, strUrl );
                if ( headersResponse != null )
                {
                    for ( Header header : method.getResponseHeaders( ) )
                    {
                        headersResponse.put( header.getName( ), header.getValue( ) );
                    }
                }

                strResponseBody = method.getResponseBodyAsString( );
            }
            catch( IOException e )
            {
                throwHttpAccessException( strUrl, e );
            }
            finally
            {
                // Release the connection.
                _accessService.releaseConnection( client, method );
            }
        }
        finally
        {
            // Delete temporary files
            for ( File file : listFiles )
            {
                boolean deleted = file.delete( );
                if ( !deleted )
                {
                    AppLogService.error( "HttpAccess - Non fatal error: could not delete httpaccess temporary file: " + file.getAbsolutePath( ) );
                }
            }
        }

        return strResponseBody;
    }

    /**
     * Send a PUT HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param params
     *            the params
     * @param headersRequest
     *            Map of headers request parameters
     * @param headersResponse
     *            Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doPut( String strUrl, RequestAuthenticator authenticator, List<String> listElements, Map<String, String> params,
            Map<String, String> headersRequest, Map<String, String> headersResponse ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;
        PutMethod method = new PutMethod( strUrl );

        if ( ( params != null ) && ( params.size( ) > 0 ) )
        {
            NameValuePair [ ] putParameters = new NameValuePair [ params.size( )];
            int nCpt = 0;

            for ( Entry<String, String> entry : params.entrySet( ) )
            {
                putParameters [nCpt++] = new NameValuePair( entry.getKey( ), entry.getValue( ) );
            }

            method.setRequestEntity( new ByteArrayRequestEntity(
                    EncodingUtil.formUrlEncode( putParameters, AppPropertiesService.getProperty( PROPERTY_CONTENT_CHARSET, DEFAULT_CHARSET ) ).getBytes( ) ) );
        }

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet( ) )
            {
                method.setRequestHeader( entry.getKey( ), entry.getValue( ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );
            int nResponse = client.executeMethod( method );
            validateResponseStatus( nResponse, method, strUrl );

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders( ) )
                {
                    headersResponse.put( header.getName( ), header.getValue( ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString( );
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            // Release the connection.
            _accessService.releaseConnection( client, method );
        }

        return strResponseBody;
    }

    /**
     * Send a DELETE HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param authenticator
     *            The {@link RequestAuthenticator}
     * @param listElements
     *            to include in the signature
     * @param headersRequest
     *            Map of headers request parameters
     * @param headersResponse
     *            Map to contain response headers
     * @return The response content of the Post request to the given Url
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String doDelete( String strUrl, RequestAuthenticator authenticator, List<String> listElements, Map<String, String> headersRequest,
            Map<String, String> headersResponse ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        DeleteMethod method = new DeleteMethod( strUrl );

        if ( headersRequest != null )
        {
            for ( Entry<String, String> entry : headersRequest.entrySet( ) )
            {
                method.setRequestHeader( entry.getKey( ), entry.getValue( ) );
            }
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );
            int nResponse = client.executeMethod( method );

            validateResponseStatus( nResponse, method, strUrl );

            if ( headersResponse != null )
            {
                for ( Header header : method.getResponseHeaders( ) )
                {
                    headersResponse.put( header.getName( ), header.getValue( ) );
                }
            }

            strResponseBody = method.getResponseBodyAsString( );
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            // Release the connection.
            _accessService.releaseConnection( client, method );
        }

        return strResponseBody;
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * 
     * @param strUrl
     *            The Url to access
     * @param strFilePath
     *            the file path
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public void downloadFile( String strUrl, String strFilePath ) throws HttpAccessException
    {
        BufferedOutputStream bos = null;

        try
        {
            FileOutputStream fos = new FileOutputStream( strFilePath );
            bos = new BufferedOutputStream( fos );
            downloadFile( strUrl, bos );

        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            try
            {
                if ( bos != null )
                {
                    bos.close( );
                }
            }
            catch( IOException e )
            {
                AppLogService.error( "HttpAccess - Error closing stream : " + e.getMessage( ), e );
                throw new HttpAccessException( e.getMessage( ), e );
            }

        }
    }

    /**
     * Send a GET HTTP request to an Url and return the response content in the ouput stream.
     * 
     * @param strUrl The Url to access
     * @param outputStream write in the outpustrean the contents of the file 
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public void downloadFile( String strUrl, OutputStream outputStream ) throws HttpAccessException
    {
        HttpMethodBase method = new GetMethod( strUrl );
        method.setFollowRedirects( true );
        BufferedInputStream bis = null;

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );
            int nResponse = client.executeMethod( method );
            validateResponseStatus( nResponse, method, strUrl );
            bis = new BufferedInputStream( method.getResponseBodyAsStream( ) );

            byte [ ] buffer = new byte [ 8 * 1024];
            int bytesRead;

            while ( ( bytesRead = bis.read( buffer ) ) != -1 )
            {
                outputStream.write( buffer, 0, bytesRead );
            }
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            try
            {
                if ( bis != null )
                {
                    bis.close( );
                }

            }
            catch( IOException e )
            {
                AppLogService.error( "HttpAccess - Error closing stream : " + e.getMessage( ), e );
                throw new HttpAccessException( e.getMessage( ), e );
            }

            // Release the connection.
            _accessService.releaseConnection( client, method );
        }
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * 
     * @param strUrl
     *            The Url to access
     * @return the file name
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public String getFileName( String strUrl ) throws HttpAccessException
    {
        String strFileName = null;
        HttpMethodBase method = new GetMethod( strUrl );
        method.setFollowRedirects( true );

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );
            int nResponse = client.executeMethod( method );
            validateResponseStatus( nResponse, method, strUrl );
            Header headerContentDisposition = method.getResponseHeader( PROPERTY_HEADER_CONTENT_DISPOSITION );

            if ( headerContentDisposition != null )
            {
                String headerValue = headerContentDisposition.getValue( );
                Pattern p = Pattern.compile( PATTERN_FILENAME );
                Matcher matcher = p.matcher( headerValue );

                if ( matcher.matches( ) )
                {
                    strFileName = matcher.group( 1 );
                }
            }
            else
            {
                String [ ] tab = strUrl.split( "/" );
                strFileName = tab [tab.length - 1];
            }

            method.abort( );
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            // Release the connection.
            _accessService.releaseConnection( client, method );
        }

        return strFileName;
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * 
     * @param strUrl
     *            The Url to access
     * @return a {@link FileItem}
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public FileItem downloadFile( String strUrl ) throws HttpAccessException
    {
        HttpMethodBase method = new GetMethod( strUrl );
        method.setFollowRedirects( true );

        MemoryFileItem fileItem = null;

        HttpClient client = null;
        try
        {
            client = _accessService.getHttpClient( method );
            int nResponse = client.executeMethod( method );
            validateResponseStatus( nResponse, method, strUrl );

            // Get the file name
            String strFileName = StringUtils.EMPTY;
            Header headerContentDisposition = method.getResponseHeader( PROPERTY_HEADER_CONTENT_DISPOSITION );

            if ( headerContentDisposition != null )
            {
                String headerValue = headerContentDisposition.getValue( );
                Pattern p = Pattern.compile( PATTERN_FILENAME );
                Matcher matcher = p.matcher( headerValue );

                if ( matcher.matches( ) )
                {
                    strFileName = matcher.group( 1 );
                }
            }
            else
            {
                String [ ] tab = strUrl.split( "/" );
                strFileName = tab [tab.length - 1];
            }

            // Get the file size
            long lSize = 0;
            Header headerContentLength = method.getResponseHeader( PROPERTY_HEADER_CONTENT_LENGTH );

            if ( headerContentLength != null )
            {
                lSize = Long.parseLong( headerContentLength.getValue( ) );
            }

            // Get the content type of the file
            String strContentType = StringUtils.EMPTY;

            Header headerContentType = method.getResponseHeader( PROPERTY_HEADER_CONTENT_TYPE );

            if ( headerContentType != null )
            {
                strContentType = headerContentType.getValue( );

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

            fileItem = new MemoryFileItem( method.getResponseBody( ), strFileName, lSize, strContentType );
        }
        catch( IOException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            // Release the connection.
            _accessService.releaseConnection( client, method );
        }

        return fileItem;
    }

    /**
     * Validate an HTTP response status code
     * 
     * @param nResponseStatus
     *            The response status
     * @param method
     *            The HTTP method
     * @param strCurrentAction
     * @throws HttpAccessException
     */
    private void validateResponseStatus( int nResponseStatus, HttpMethod method, String strUrl ) throws HttpAccessException
    {
        if ( !_responseValidator.validate( nResponseStatus ) )
        {
            String strError = "HttpAccess - Error executing method " + method.getName( ) + " at URL : " + strUrl + " - return code : " + nResponseStatus;
            String strResponseBody;
            try
            {
                strResponseBody = " Response Body : \n" + method.getResponseBodyAsString( );

            }
            catch( IOException ex )
            {
                strResponseBody = " unable to get Response Body.";
            }
            strError += strResponseBody;

            throw new InvalidResponseStatus( strError, nResponseStatus, null );
        }

    }

    /**
     * Throws a new HttpAccess exception
     * 
     * @param strUrl
     *            The URL concerned by the original exception
     * @param exception
     *            the original exception
     * @throws HttpAccessException
     *             The exception throwned
     */
    private void throwHttpAccessException( String strUrl, Exception exception ) throws HttpAccessException
    {
        String strError = "HttpAccess - Error URL : " + strUrl + "' : ";
        AppLogService.error( strError + exception.getMessage( ), exception );
        throw new HttpAccessException( strError + exception.getMessage( ), exception );
    }
}
