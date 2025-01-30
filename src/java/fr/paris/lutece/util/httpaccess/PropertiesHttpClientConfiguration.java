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

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * HttpClientConfiguration pulling the configuration from AppPropertiesService
 */
public class PropertiesHttpClientConfiguration extends HttpClientConfiguration
{
    /** The Constant PROPERTY_PROXY_HOST. */
    private static final String PROPERTY_PROXY_HOST = "httpAccess.proxyHost";

    /** The Constant PROPERTY_PROXY_PORT. */
    private static final String PROPERTY_PROXY_PORT = "httpAccess.proxyPort";

    /** The Constant PROPERTY_PROXY_USERNAME. */
    private static final String PROPERTY_PROXY_USERNAME = "httpAccess.proxyUserName";

    /** The Constant PROPERTY_PROXY_PASSWORD. */
    private static final String PROPERTY_PROXY_PASSWORD = "httpAccess.proxyPassword";

    /** The Constant PROPERTY_HOST_NAME. */
    private static final String PROPERTY_HOST_NAME = "httpAccess.hostName";

    /** The Constant PROPERTY_DOMAIN_NAME. */
    private static final String PROPERTY_DOMAIN_NAME = "httpAccess.domainName";

    /** The Constant PROPERTY_REALM. */
    private static final String PROPERTY_REALM = "httpAccess.realm";

    /** The Constant PROPERTY_NO_PROXY_FOR. */
    private static final String PROPERTY_NO_PROXY_FOR = "httpAccess.noProxyFor";

    /** The Constant PROPERTY_CONTENT_CHARSET. */
    private static final String PROPERTY_CONTENT_CHARSET = "httpAccess.contentCharset";

    /** The Constant PROPERTY_ELEMENT_CHARSET. */
    private static final String PROPERTY_ELEMENT_CHARSET = "httpAccess.elementCharset";

    /** The Constant PROPERTY_SOCKET_TIMEOUT. */
    private static final String PROPERTY_SOCKET_TIMEOUT = "httpAccess.socketTimeout";

    /** The Constant PROPERTY_CONNECTION_TIMEOUT. */
    private static final String PROPERTY_CONNECTION_TIMEOUT = "httpAccess.connectionTimeout";

    /** The Constant PROPERTY_CONNECTION_POOL_ENABLED. */
    private static final String PROPERTY_CONNECTION_POOL_ENABLED = "httpAccess.connectionPoolEnabled";

    /** The Constant PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION. */
    private static final String PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION = "httpAccess.connectionPoolMaxTotalConnections";

    /** The Constant PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION_PER_HOST. */
    private static final String PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION_PER_HOST = "httpAccess.connectionPoolMaxConnectionsPerHost";
    
    public PropertiesHttpClientConfiguration( )
    {
        this.setProxyHost( AppPropertiesService.getProperty( PROPERTY_PROXY_HOST ) );
        this.setProxyPort( AppPropertiesService.getProperty( PROPERTY_PROXY_PORT ) );
        this.setProxyUserName( AppPropertiesService.getProperty( PROPERTY_PROXY_USERNAME ) );
        this.setProxyPassword( AppPropertiesService.getProperty( PROPERTY_PROXY_PASSWORD ) );
        this.setHostName( AppPropertiesService.getProperty( PROPERTY_HOST_NAME ) );
        this.setDomainName( AppPropertiesService.getProperty( PROPERTY_DOMAIN_NAME ) );
        this.setRealm( AppPropertiesService.getProperty( PROPERTY_REALM ) );
        this.setNoProxyFor( AppPropertiesService.getProperty( PROPERTY_NO_PROXY_FOR ) );
        this.setContentCharset( AppPropertiesService.getProperty( PROPERTY_CONTENT_CHARSET ) );
        this.setElementCharset( AppPropertiesService.getProperty( PROPERTY_ELEMENT_CHARSET ) );
        this.setConnectionPoolEnabled( AppPropertiesService.getPropertyBoolean( PROPERTY_CONNECTION_POOL_ENABLED, false ) );

        try
        {
            this.setSocketTimeout( StringUtils.isNotEmpty( AppPropertiesService.getProperty( PROPERTY_SOCKET_TIMEOUT ) )
                    ? Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_SOCKET_TIMEOUT ) )
                    : null );
        }
        catch( NumberFormatException e )
        {
            AppLogService.error( "Error during initialisation of socket timeout ", e );
        }

        try
        {
            this.setConnectionTimeout( StringUtils.isNotEmpty( AppPropertiesService.getProperty( PROPERTY_CONNECTION_TIMEOUT ) )
                    ? Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_CONNECTION_TIMEOUT ) )
                    : null );

        }
        catch( NumberFormatException e )
        {
            AppLogService.error( "Error during initialisation of connection timeout ", e );
        }
        try
        {
            this.setConnectionPoolMaxTotalConnection(
                    StringUtils.isNotEmpty( AppPropertiesService.getProperty( PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION ) )
                            ? Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION ) )
                            : null );
        }
        catch( NumberFormatException e )
        {
            AppLogService.error( "Error during initialisation of Connection Pool Maxt Total Connection ", e );
        }
        try
        {
            this.setConnectionPoolMaxConnectionPerHost(
                    StringUtils.isNotEmpty( AppPropertiesService.getProperty( PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION_PER_HOST ) )
                            ? Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION_PER_HOST ) )
                            : null );
        }
        catch( NumberFormatException e )
        {
            AppLogService.error( "Error during initialisation of Connection Pool Maxt Total Connection Per Host ", e );
        }
    }
}
