/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.signrequest.AuthenticateRequestInformations;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;

/**
 * Http net Object Accessor.
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

    /** The Constant PROPERTY_HTTP_REQUEST_POST. */
    private static final String PROPERTY_HTTP_REQUEST_POST = "POST";

    /** The Constant PROPERTY_HTTP_REQUEST_PUT. */
    private static final String PROPERTY_HTTP_REQUEST_PUT = "PUT";

    /** The Constant PROPERTY_HTTP_REQUEST_DELETE. */
    private static final String PROPERTY_HTTP_REQUEST_DELETE = "DELETE";

    /** The Constant DEFAULT_CHARSET. */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /** The response validator. */
    private ResponseStatusValidator _responseValidator;

    /** The access service. */
    private HttpAccessService _accessService;

    /**
     * Instantiates a new http access.
     */
    public HttpAccess( )
    {
        _accessService = HttpAccessService.getInstance( );
        _responseValidator = _accessService;
    }

    /**
     * Instantiates a new http access.
     *
     * @param validator
     *            the validator
     */
    public HttpAccess( ResponseStatusValidator validator )
    {
        _accessService = HttpAccessService.getInstance( );
        _responseValidator = validator;
    }

    /**
     * Instantiates a new http access.
     *
     * @param accessService
     *            the access service
     * @param validator
     *            the validator
     */
    public HttpAccess( HttpAccessService accessService, ResponseStatusValidator validator )
    {
        _accessService = accessService;
        _responseValidator = validator;
    }

    /**
     * Instantiates a new http access.
     *
     * @param accessService
     *            the access service
     */
    public HttpAccess( HttpAccessService accessService )
    {
        _accessService = accessService;
        _responseValidator = HttpAccessService.getInstance( );
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

        HttpUriRequestBase httpGet = new HttpGet( strUrl );
        addSecurityInformations( httpGet, strUrl, authenticator, listElements );

        if ( headersRequest != null )
        {
            headersRequest.forEach( ( k, v ) -> httpGet.addHeader( k, v ) );
        }

        strResponseBody = getResponseBody( httpGet, strUrl, headersResponse );

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

        HttpUriRequestBase httpPost = new HttpPost( strUrl );

        return doSendFormEntity( httpPost, strUrl, params, authenticator, listElements, headersRequest, headersResponse );

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
        HttpUriRequestBase httpRequest;

        switch( strMethod )
        {
            case PROPERTY_HTTP_REQUEST_PUT:

                httpRequest = new HttpPut( strUrl );
                break;

            case PROPERTY_HTTP_REQUEST_POST:
                httpRequest = new HttpPost( strUrl );
                break;

            case PROPERTY_HTTP_REQUEST_DELETE:
                httpRequest = new HttpDelete( strUrl );
                break;
            default:
                httpRequest = new HttpPost( strUrl );
                break;
        }

        if ( headersRequest != null )
        {
            headersRequest.forEach( ( k, v ) -> httpRequest.addHeader( k, v ) );
        }
        addSecurityInformations( httpRequest, strUrl, authenticator, listElements );
        httpRequest.setEntity( new StringEntity( strContent, ContentType.APPLICATION_JSON, charset, false ) );

        strResponseBody = getResponseBody( httpRequest, strUrl, headersResponse );

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
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_POST, strJSON, DEFAULT_JSON_MIME_TYPE,
                !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                        ? _accessService.getHttpClientConfiguration( ).getContentCharset( )
                        : DEFAULT_CHARSET,
                authenticator, listElements, headersRequest, headersResponse );
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
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_POST, strJSON, DEFAULT_JSON_MIME_TYPE,
                !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                        ? _accessService.getHttpClientConfiguration( ).getContentCharset( )
                        : DEFAULT_CHARSET,
                null, null, headersRequest, headersResponse );
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
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_PUT, strJSON, DEFAULT_JSON_MIME_TYPE,
                !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                        ? _accessService.getHttpClientConfiguration( ).getContentCharset( )
                        : DEFAULT_CHARSET,
                authenticator, listElements, headersRequest, headersResponse );
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
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_PUT, strJSON, DEFAULT_JSON_MIME_TYPE,
                !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                        ? _accessService.getHttpClientConfiguration( ).getContentCharset( )
                        : DEFAULT_CHARSET,
                null, null, headersRequest, headersResponse );
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

        return doPostMultiValues( strUrl, params, authenticator, listElements, headersRequest, null );
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
        HttpUriRequestBase httpPost = new HttpPost( strUrl );

        List<NameValuePair> nvps = new ArrayList<>( );

        if ( headersRequest != null )
        {
            headersRequest.forEach( ( k, v ) -> httpPost.addHeader( k, v ) );
        }

        if ( params != null )
        {
            params.forEach( ( k, v ) -> v.stream( ).forEach( y -> nvps.add( new BasicNameValuePair( k, y ) ) ) );
        }

        addSecurityInformations( httpPost, strUrl, authenticator, listElements );
        httpPost.setEntity( new UrlEncodedFormEntity( nvps,
                !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                        ? Charset.forName( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                        : Charset.forName( DEFAULT_CHARSET ) ) );

        strResponseBody = getResponseBody( httpPost, strUrl, headersResponse );

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
        HttpUriRequestBase httpPost = new HttpPost( strUrl );

        if ( headersRequest != null )
        {
            headersRequest.forEach( ( k, v ) -> httpPost.addHeader( k, v ) );
        }

        ArrayList<File> listFiles = new ArrayList<File>( );

        MultipartEntityBuilder builder = MultipartEntityBuilder.create( );

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
                            if ( splitContentType.length > 1 && StringUtils.isNotBlank( splitContentType [1] )
                                    && splitContentType [1].toUpperCase( ).contains( "CHARSET" ) )
                            {

                                String [ ] splitCharset = splitContentType [1].split( "=" );
                                if ( splitCharset.length > 1 )
                                {
                                    strCharset = splitCharset [1];
                                }

                            }
                        }

                        if ( fileItem.isInMemory( ) )
                        {

                            ContentType contentType = !StringUtils.isEmpty( strContentType )
                                    ? ContentType.create( strContentType,
                                            !StringUtils.isEmpty( strCharset ) ? Charset.forName( strCharset ) : Charset.forName( DEFAULT_CHARSET ) )
                                    : ContentType.DEFAULT_BINARY;

                            builder.addBinaryBody( paramFileItem.getKey( ), fileItem.get( ), contentType, fileItem.getName( ) );

                        }
                        else
                        {
                            File file = File.createTempFile( "httpaccess-multipart-", null );
                            // Store files for deletion after the request completed
                            listFiles.add( file );
                            fileItem.write( file );
                            ContentType contentType = !StringUtils.isEmpty( strContentType )
                                    ? ContentType.create( strContentType,
                                            !StringUtils.isEmpty( strCharset ) ? Charset.forName( strCharset ) : Charset.forName( DEFAULT_CHARSET ) )
                                    : ContentType.DEFAULT_BINARY;

                            builder.addBinaryBody( paramFileItem.getKey( ), file, contentType, fileItem.getName( ) );

                        }

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

            ContentType contentType = ContentType.create( "text/plain",
                    !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                            ? Charset.forName( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                            : Charset.forName( DEFAULT_CHARSET ) );
            // Additionnal parameters
            params.forEach( ( k, v ) -> {
                v.stream( ).forEach( y -> {
                    builder.addTextBody( k, y, contentType );
                } );
            } );

        }

        addSecurityInformations( httpPost, strUrl, authenticator, listElements );
        builder.setCharset( !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                ? Charset.forName( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                : Charset.forName( DEFAULT_CHARSET ) );
        HttpEntity entityForm = builder.build( );
        httpPost.setEntity( entityForm );

        strResponseBody = getResponseBody( httpPost, strUrl, headersResponse );

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

        HttpPut httpPut = new HttpPut( strUrl );
        return doSendFormEntity( httpPut, strUrl, params, authenticator, listElements, headersRequest, headersResponse );
    }

    /**
     * Send a POST or PUT HTTP request to an url and return the response content.
     *
     * @param httprequestBase
     *            the httprequest base
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
    private String doSendFormEntity( HttpUriRequestBase httprequestBase, String strUrl, Map<String, String> params, RequestAuthenticator authenticator,
            List<String> listElements, Map<String, String> headersRequest, Map<String, String> headersResponse ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;

        List<NameValuePair> nvps = new ArrayList<>( );

        if ( headersRequest != null )
        {
            headersRequest.forEach( ( k, v ) -> httprequestBase.addHeader( k, v ) );
        }
        if ( params != null )
        {
            params.forEach( ( k, v ) -> nvps.add( new BasicNameValuePair( k, v ) ) );
        }

        addSecurityInformations( httprequestBase, strUrl, authenticator, listElements );
        httprequestBase.setEntity( new UrlEncodedFormEntity( nvps,
                !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                        ? Charset.forName( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                        : Charset.forName( DEFAULT_CHARSET ) ) );

        strResponseBody = getResponseBody( httprequestBase, strUrl, headersResponse );

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

        HttpUriRequestBase httpDelete = new HttpDelete( strUrl );
        addSecurityInformations( httpDelete, strUrl, authenticator, listElements );

        if ( headersRequest != null )
        {
            headersRequest.forEach( ( k, v ) -> httpDelete.addHeader( k, v ) );
        }

        strResponseBody = getResponseBody( httpDelete, strUrl, headersResponse );

        return strResponseBody;
    }

    /**
     * Send a DELETE HTTP request to an url and return the response content.
     *
     * @param strUrl
     *            the url to access
     * @param strJson
     *            the json to send in body
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
    public String doDeleteJSON( String strUrl, String strJSON, RequestAuthenticator authenticator, List<String> listElements,
            Map<String, String> headersRequest, Map<String, String> headersResponse ) throws HttpAccessException
    {
        return doRequestEnclosingMethod( strUrl, PROPERTY_HTTP_REQUEST_DELETE, strJSON, DEFAULT_JSON_MIME_TYPE,
                !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                        ? _accessService.getHttpClientConfiguration( ).getContentCharset( )
                        : DEFAULT_CHARSET,
                authenticator, listElements, headersRequest, headersResponse );
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
     * @param strUrl
     *            The Url to access
     * @param outputStream
     *            write in the outpustrean the contents of the file
     * @throws HttpAccessException
     *             if there is a problem to access to the given Url
     */
    public void downloadFile( String strUrl, OutputStream outputStream ) throws HttpAccessException
    {
        HttpGet httpGet = new HttpGet( strUrl );

        try
        {

            CloseableHttpClient httpClient = _accessService.getHttpClient( );
            try ( CloseableHttpResponse response = httpClient.execute( httpGet ) )
            {

                int nResponse = response.getCode( );
                validateResponseStatus( nResponse, httpGet.getMethod( ), response, strUrl );

                HttpEntity entity = response.getEntity( );

                if ( entity != null )
                {
                    entity.writeTo( outputStream );

                }

            }
        }
        catch( IOException | ParseException e )
        {
            throwHttpAccessException( strUrl, e );
        }
        finally
        {
            try
            {
                if ( outputStream != null )
                {
                    outputStream.close( );
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

        HttpGet httpGet = new HttpGet( strUrl );

        try
        {
            CloseableHttpClient httpClient = _accessService.getHttpClient( );
            try ( CloseableHttpResponse response = httpClient.execute( httpGet ) )
            {

                int nResponse = response.getCode( );
                validateResponseStatus( nResponse, httpGet.getMethod( ), response, strUrl );

                Header headerContentDisposition = response.getHeader( PROPERTY_HEADER_CONTENT_DISPOSITION );
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

            }
        }
        catch( IOException | ProtocolException e )
        {
            throwHttpAccessException( strUrl, e );
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
        MemoryFileItem fileItem = null;
        HttpGet httpGet = new HttpGet( strUrl );

        try
        {

            CloseableHttpClient httpClient = _accessService.getHttpClient( );
            try ( CloseableHttpResponse response = httpClient.execute( httpGet ) )
            {

                int nResponse = response.getCode( );
                validateResponseStatus( nResponse, httpGet.getMethod( ), response, strUrl );
                // Get the file name
                String strFileName = StringUtils.EMPTY;

                Header headerContentDisposition = response.getHeader( PROPERTY_HEADER_CONTENT_DISPOSITION );
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
                Header headerContentLength = response.getHeader( PROPERTY_HEADER_CONTENT_LENGTH );
                if ( headerContentLength != null )
                {
                    lSize = Long.parseLong( headerContentLength.getValue( ) );
                }

                // Get the content type of the file
                String strContentType = StringUtils.EMPTY;

                Header headerContentType = response.getHeader( PROPERTY_HEADER_CONTENT_TYPE );

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

                HttpEntity entity = response.getEntity( );

                if ( entity != null )
                {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                    entity.writeTo( outputStream );
                    fileItem = new MemoryFileItem( outputStream.toByteArray( ), strFileName, lSize, strContentType );
                }

            }
        }

        catch( IOException | ProtocolException e )
        {
            throwHttpAccessException( strUrl, e );
        }

        return fileItem;
    }

    /**
     * Validate an HTTP response status code.
     *
     * @param nResponseStatus
     *            The response status
     * @param strMethodName
     *            the str method name
     * @param response
     *            the response
     * @param strUrl
     *            the str url
     * @throws HttpAccessException
     *             the http access exception
     * @throws ParseException
     *             the parse exception
     */
    private void validateResponseStatus( int nResponseStatus, String strMethodName, CloseableHttpResponse response, String strUrl )
            throws HttpAccessException, ParseException
    {
        if ( _responseValidator.validate( nResponseStatus ) )
        {
            return; // status OK
        }
        String strError = "HttpAccess - Error executing method " + strMethodName + " at URL : " + stripPassword( strUrl ) + " - return code : "
                + nResponseStatus;
        String strResponseBody;
        try
        {
            HttpEntity entity = response.getEntity( );
            // Get response information
            strResponseBody = entity != null ? EntityUtils.toString( entity ) : "<Response body unavailable>";
        }
        catch( IOException ex )
        {
            strResponseBody = "<unable to get Response Body : " + ex.getMessage( ) + ">";
        }
        throw new InvalidResponseStatus( strError, nResponseStatus, strResponseBody, null );
    }

    /**
     * Throws a new HttpAccess exception.
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
        String strError = "HttpAccess - Error URL : " + stripPassword( strUrl ) + "' : ";
        AppLogService.error( strError + exception.getMessage( ), exception );
        throw new HttpAccessException( strError + exception.getMessage( ), exception );
    }

    /**
     * Hide end of url if the keyword "password" appears in parameters.
     *
     * @param strUrl
     *            the str url
     * @return the url stripped
     */
    private String stripPassword( String strUrl )
    {
        if ( strUrl != null && strUrl.indexOf( "?" ) > 0 && strUrl.toLowerCase( ).indexOf( "password", strUrl.indexOf( "?" ) ) > 0 )
        {
            return strUrl.substring( 0, strUrl.toLowerCase( ).indexOf( "password", strUrl.indexOf( "?" ) ) ) + "***";
        }
        else
        {
            return strUrl;
        }
    }

    /**
     * Add the security information in the request
     *
     * @param httpRequest
     *            the http request
     * @param strTargetUrl
     *            the target url of the request
     * @param authenticator
     *            the authenticator
     * @param listElements
     *            the list of elements used by sthe authenticator
     */
    private void addSecurityInformations( HttpUriRequestBase httpRequest, String strTargetUrl, RequestAuthenticator authenticator, List<String> listElements )
    {

        if ( authenticator != null )
        {
            AuthenticateRequestInformations securityInformations = authenticator.getSecurityInformations( listElements );
            // Add Security Parameters in the request
            if ( !securityInformations.getSecurityParameteres( ).isEmpty( ) )
            {

                List<NameValuePair> nvps = new ArrayList<>( );

                securityInformations.getSecurityParameteres( ).forEach( ( k, v ) -> nvps.add( new BasicNameValuePair( k, v ) ) );
                // Add to the request URL
                try
                {
                    URI uri = new URIBuilder( new URI( strTargetUrl ) ).addParameters( nvps ).build( );
                    httpRequest.setUri( uri );
                }
                catch( URISyntaxException e )
                {
                    throw new RuntimeException( e );
                }

            }
            // Add security Headers in the request
            if ( !securityInformations.getSecurityHeaders( ).isEmpty( ) )
            {

                securityInformations.getSecurityHeaders( ).forEach( ( k, v ) -> httpRequest.addHeader( k, v ) );
            }

        }

    }

    /**
     * Gets the response body.
     *
     * @param httpRequest
     *            the http request
     * @param strUrl
     *            the str url
     * @param mapResponseHeader
     *            the map response header
     * @return the response body
     * @throws HttpAccessException
     *             the http access exception
     */
    private String getResponseBody( HttpUriRequestBase httpRequest, String strUrl, Map<String, String> mapResponseHeader ) throws HttpAccessException
    {
        String strResponseBody = StringUtils.EMPTY;
        try
        {
            CloseableHttpClient httpClient = _accessService.getHttpClient( );
            try ( CloseableHttpResponse response = httpClient.execute( httpRequest ) )
            {

                int nResponse = response.getCode( );
                validateResponseStatus( nResponse, httpRequest.getMethod( ), response, strUrl );

                if ( mapResponseHeader != null && response.getHeaders( ) != null )
                {

                    Arrays.asList( response.getHeaders( ) ).stream( ).forEach( x -> mapResponseHeader.put( x.getName( ), x.getValue( ) ) );

                }
                HttpEntity entity = response.getEntity( );
                if ( entity != null )
                {
                    strResponseBody = EntityUtils.toString( entity,
                            !StringUtils.isEmpty( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                                    ? Charset.forName( _accessService.getHttpClientConfiguration( ).getContentCharset( ) )
                                    : Charset.forName( DEFAULT_CHARSET ) );
                }

            }

        }

        catch( IOException | ParseException e )
        {
            throwHttpAccessException( strUrl, e );
        }

        return strResponseBody;
    }

}
