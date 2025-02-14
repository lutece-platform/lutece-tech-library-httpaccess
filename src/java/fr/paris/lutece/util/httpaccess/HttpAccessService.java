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

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;

/**
 * HttpAccessService.
 */
public class HttpAccessService implements ResponseStatusValidator
{
    /** The Constant DEFAULT_RESPONSE_CODE_AUTHORIZED. */
    private static final String DEFAULT_RESPONSE_CODE_AUTHORIZED = "200,201,202";

    /** The Constant PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED. */
    private static final String PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED = "httpAccess.responsesCodeAuthorized";

    /** The _singleton. */
    private static final HttpAccessService _singleton = new HttpAccessService( new PropertiesHttpClientConfiguration( ) );

    private final HttpClientConfiguration _httpClientConfiguration;

    private final CloseableHttpClient _httpClient;

    public HttpClientConfiguration getHttpClientConfiguration( )
    {
        return _httpClientConfiguration;
    }

    private final ResponseStatusValidator _responseValidator;

    /**
     * Gets the single instance of HttpAccessService.
     *
     * @return single instance of HttpAccessService
     */
    public static HttpAccessService getInstance( )
    {
        return _singleton;
    }

    /**
     * create new specific Instance of HttpAccessService
     * 
     * @param httpClientConfiguration
     *            the httpClienConfiguration
     */
    public HttpAccessService( HttpClientConfiguration httpClientConfiguration )
    {
        super( );
        _httpClientConfiguration = httpClientConfiguration;
        _responseValidator = SimpleResponseValidator.loadFromProperty( PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED, DEFAULT_RESPONSE_CODE_AUTHORIZED );
        _httpClient = buildHttpClient( );
    }

    /**
     * Procure a HttpClientBuilder
     * 
     * @return a HttpClientBuilder
     */
    protected HttpClientBuilder getHttpClientBuilder( )
    {
        return HttpClients.custom( );
    }

    public CloseableHttpClient getHttpClient( )
    {
        return _httpClient;
    }

    /**
     * get an HTTP client object using current configuration.
     *
     * @param method
     *            The method
     * @return An HTTP client authenticated
     */
    private CloseableHttpClient buildHttpClient( )
    {

        HttpClientBuilder clientBuilder = getHttpClientBuilder( );

        if ( StringUtils.isNotBlank( _httpClientConfiguration.getProxyHost( ) ) )
        {
            clientBuilder.setRoutePlanner( new ProxyRoutePlanner( _httpClientConfiguration.getProxyHost( ),
                    Integer.parseInt( _httpClientConfiguration.getProxyPort( ) ), _httpClientConfiguration.getNoProxyFor( ) ) );
        }

        if ( _httpClientConfiguration.getConnectionPoolMaxConnectionPerHost( ) != null
                || _httpClientConfiguration.getConnectionPoolMaxTotalConnection( ) != null )
        {
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager( );

            if ( _httpClientConfiguration.getConnectionPoolMaxConnectionPerHost( ) != null )
            {
                connectionManager.setDefaultMaxPerRoute( _httpClientConfiguration.getConnectionPoolMaxConnectionPerHost( ) );

            }

            if ( _httpClientConfiguration.getConnectionPoolMaxTotalConnection( ) != null )
            {
                connectionManager.setMaxTotal( _httpClientConfiguration.getConnectionPoolMaxTotalConnection( ) );
            }

            clientBuilder.setConnectionManager( connectionManager );
        }

        if ( _httpClientConfiguration.getSocketTimeout( ) != null || _httpClientConfiguration.getConnectionTimeout( ) != null )
        {
            RequestConfig.Builder requestConfiguilder = RequestConfig.custom( );
            if ( _httpClientConfiguration.getConnectionTimeout( ) != null )
            {
                requestConfiguilder.setConnectTimeout( Timeout.ofMilliseconds( _httpClientConfiguration.getConnectionTimeout( ) ) );

            }

            if ( _httpClientConfiguration.getSocketTimeout( ) != null )
            {

                requestConfiguilder.setResponseTimeout( Timeout.ofMilliseconds( _httpClientConfiguration.getSocketTimeout( ) ) );

            }
            clientBuilder.setDefaultRequestConfig( requestConfiguilder.build( ) );
            // follow redirect
            clientBuilder.setRedirectStrategy( DefaultRedirectStrategy.INSTANCE );
        }

        return clientBuilder.build( );
    }

    /**
     * Default Response status Validation
     * 
     * @param nStatus
     *            The status
     * @return true if Response code is authorized
     */
    @Override
    public boolean validate( int nStatus )
    {
        return _responseValidator.validate( nStatus );
    }

}
