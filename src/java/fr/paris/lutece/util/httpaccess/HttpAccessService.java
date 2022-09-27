/*
 * Copyright (c) 2002-2021, City of Paris
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
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;
import org.apache.http.impl.client.LaxRedirectStrategy;

import fr.paris.lutece.portal.service.util.AppPropertiesService;

// TODO: Auto-generated Javadoc
/**
 * HttpAccessService.
 */
public class HttpAccessService implements ResponseStatusValidator
{
    /** The Constant DEFAULT_RESPONSE_CODE_AUTHORIZED. */
    private static final String DEFAULT_RESPONSE_CODE_AUTHORIZED = "200,201,202";

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

    /** The Constant PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED. */
    private static final String PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED = "httpAccess.responsesCodeAuthorized";

    /** The Constant PROPERTY_HTTP_PROTOCOLE_CONTENT_CHARSET. */
    private static final String PROPERTY_HTTP_PROTOCOLE_CONTENT_CHARSET = "http.protocol.content-charset";

    /** The Constant PROPERTY_HTTP_PROTOCOLE_ELEMENT_CHARSET. */
    private static final String PROPERTY_HTTP_PROTOCOLE_ELEMENT_CHARSET = "http.protocol.element-charset";

    /** The _singleton. */
    private static HttpAccessService _singleton;


    /** The _str proxy host. */
    private String _strProxyHost;

    /** The _str proxy port. */
    private String _strProxyPort;

    /** The _str proxy user name. */
    private String _strProxyUserName;

    /** The _str proxy password. */
    private String _strProxyPassword;

    /** The _str host name. */
    private String _strHostName;

    /** The _str domain name. */
    private String _strDomainName;

    /** The _str realm. */
    private String _strRealm;

    /** The _str no proxy for. */
    private String _strNoProxyFor;

    /** The _str content charset. */
    private String _strContentCharset;

    /** The _str element charset. */
    private String _strElementCharset;

    /** The _str socket timeout. */
    private String _strSocketTimeout;

    /** The _str connection timeout. */
    private String _strConnectionTimeout;

    /** The _b connection pool enabled. */
    private boolean _bConnectionPoolEnabled;

    /** The _str connection pool max total connection. */
    private String _strConnectionPoolMaxTotalConnection;

    /** The _str connection pool max connection per host. */
    private String _strConnectionPoolMaxConnectionPerHost;

    /** The Constant SEPARATOR. */
    private static final String SEPARATOR = ",";

    private ResponseStatusValidator _responseValidator;

    /**
     * Gets the single instance of HttpAccessService.
     *
     * @return single instance of HttpAccessService
     */
    public static HttpAccessService getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new HttpAccessService( );
            // WARNING if .init( ) throw exception, the singleton is already defined
            _singleton.init( );
        }

        return _singleton;
    }

    /**
     * get an HTTP client object using current configuration.
     *
     * @param method
     *            The method
     * @return An HTTP client authenticated
     */
    public synchronized CloseableHttpClient  getHttpClient(  String strTargetHost )
    {
    	
    	HttpClientBuilder clientBuilder = HttpClients.custom();
    	

        // bNoProxy will be true when we would normally be using a proxy but matched on the NoProxyFor list
      
        if ( StringUtils.isNotBlank( _strProxyHost ) )
        {
        	    boolean bNoProxy = ( StringUtils.isNotBlank( _strNoProxyFor ) && matchesList( _strNoProxyFor.split( SEPARATOR ), strTargetHost) );
                if(!bNoProxy && StringUtils.isNotBlank( _strProxyPort ) && StringUtils.isNumeric( _strProxyPort ))
                {
                	final HttpHost proxy = new HttpHost("http", _strProxyHost,Integer.parseInt( _strProxyPort));
                	clientBuilder.setProxy(proxy);
                }
            
        }

        if ( _bConnectionPoolEnabled )
        {
        	    PoolingHttpClientConnectionManager connManager= new PoolingHttpClientConnectionManager();
        	
                if ( StringUtils.isEmpty( _strConnectionPoolMaxConnectionPerHost ) )
                {
                	  connManager.setDefaultMaxPerRoute(Integer.parseInt(_strConnectionPoolMaxConnectionPerHost));
                }

                if ( StringUtils.isEmpty( _strConnectionPoolMaxTotalConnection ) )
                {
                	  connManager.setMaxTotal( Integer.parseInt( _strConnectionPoolMaxTotalConnection ) );
                }
                clientBuilder.setConnectionManager(connManager);
            
         }
//     
//        Credentials cred = null;
//
//        // If hostname and domain name found, consider we are in NTLM authentication scheme
//        // else if only username and password found, use simple UsernamePasswordCredentials
//        if ( StringUtils.isNotBlank( _strHostName ) && StringUtils.isNotBlank( _strDomainName ) )
//        {
//            cred = new NTCredentials( _strProxyUserName, _strProxyPassword, _strHostName, _strDomainName );
//        }
//        else
//            if ( StringUtils.isNotBlank( _strProxyUserName ) && StringUtils.isNotBlank( _strProxyPassword ) )
//            {
//                cred = new UsernamePasswordCredentials( _strProxyUserName, _strProxyPassword );
//            }
//
//        if ( ( cred != null ) && !bNoProxy )
//        {
//            AuthScope authScope = new AuthScope( _strProxyHost, Integer.parseInt( _strProxyPort ), _strRealm );
//            client.getState( ).setProxyCredentials( authScope, cred );
//            client.getParams( ).setAuthenticationPreemptive( true );
//            method.setDoAuthentication( true );
//        }

//        if ( StringUtils.isNotBlank( _strContentCharset ) )
//        {
//            client.getParams( ).setParameter( PROPERTY_HTTP_PROTOCOLE_CONTENT_CHARSET, _strContentCharset );
//        }
//
//        if ( StringUtils.isNotBlank( _strElementCharset ) )
//        {
//            client.getParams( ).setParameter( PROPERTY_HTTP_PROTOCOLE_ELEMENT_CHARSET, _strElementCharset );
//        }

        if ( StringUtils.isNotBlank( _strSocketTimeout ) ||  StringUtils.isNotBlank( _strConnectionTimeout ))
        {
        	RequestConfig.Builder requestConfiguilder = RequestConfig.custom();
        	 if(StringUtils.isNotBlank(  _strConnectionTimeout))
        	 {
        		 requestConfiguilder.setConnectTimeout(Timeout.ofMilliseconds(( Integer.parseInt( _strConnectionTimeout ))));
        		 
        	 }
        	 
        	 if(StringUtils.isNotBlank( _strSocketTimeout ))
        	 {
        		 requestConfiguilder.setConnectionRequestTimeout(Timeout.ofMilliseconds(( Integer.parseInt( _strSocketTimeout ))));
        		 
        	 }
        	 clientBuilder.setDefaultRequestConfig(requestConfiguilder.build());
        	
        	
        }

        
        //follow redirect
        clientBuilder.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE);
        

        return clientBuilder.build();
    }

    /**
     * heck if the text matches one of the pattern of the list.
     *
     * @param listPatterns
     *            the list of patterns
     * @param strText
     *            the text
     * @return true if the text matches one of the pattern, false otherwise
     */
    private boolean matchesList( String [ ] listPatterns, String strText )
    {
        if ( listPatterns == null )
        {
            return false;
        }

        for ( String pattern : listPatterns )
        {
            if ( matches( pattern, strText ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the pattern match the text. It also deals with special characters like * or ?
     * 
     * @param strPattern
     *            the pattern
     * @param strText
     *            the text
     * @return true if the text matches the pattern, false otherwise
     */
    private static boolean matches( String strPattern, String strText )
    {
        String strTextTmp = strText + '\0';
        String strPatternTmp = strPattern + '\0';

        int nLength = strPatternTmp.length( );

        boolean [ ] states = new boolean [ nLength + 1];
        boolean [ ] old = new boolean [ nLength + 1];
        old [0] = true;

        for ( int i = 0; i < strTextTmp.length( ); i++ )
        {
            char c = strTextTmp.charAt( i );
            states = new boolean [ nLength + 1];

            for ( int j = 0; j < nLength; j++ )
            {
                char p = strPatternTmp.charAt( j );

                if ( old [j] && ( p == '*' ) )
                {
                    old [j + 1] = true;
                }

                if ( old [j] && ( p == c ) )
                {
                    states [j + 1] = true;
                }

                if ( old [j] && ( p == '?' ) )
                {
                    states [j + 1] = true;
                }

                if ( old [j] && ( p == '*' ) )
                {
                    states [j] = true;
                }

                if ( old [j] && ( p == '*' ) )
                {
                    states [j + 1] = true;
                }
            }

            old = states;
        }

        return states [nLength];
    }

    /**
     * init properties.
     */
    private void init( )
    {
        _strProxyHost = AppPropertiesService.getProperty( PROPERTY_PROXY_HOST );
        _strProxyPort = AppPropertiesService.getProperty( PROPERTY_PROXY_PORT );
        _strProxyUserName = AppPropertiesService.getProperty( PROPERTY_PROXY_USERNAME );
        _strProxyPassword = AppPropertiesService.getProperty( PROPERTY_PROXY_PASSWORD );
        _strHostName = AppPropertiesService.getProperty( PROPERTY_HOST_NAME );
        _strDomainName = AppPropertiesService.getProperty( PROPERTY_DOMAIN_NAME );
        _strRealm = AppPropertiesService.getProperty( PROPERTY_REALM );
        _strNoProxyFor = AppPropertiesService.getProperty( PROPERTY_NO_PROXY_FOR );
        _strContentCharset = AppPropertiesService.getProperty( PROPERTY_CONTENT_CHARSET );
        _strElementCharset = AppPropertiesService.getProperty( PROPERTY_ELEMENT_CHARSET );
        _strSocketTimeout = AppPropertiesService.getProperty( PROPERTY_SOCKET_TIMEOUT );
        _strConnectionTimeout = AppPropertiesService.getProperty( PROPERTY_CONNECTION_TIMEOUT );
        _responseValidator = SimpleResponseValidator.loadFromProperty( PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED, DEFAULT_RESPONSE_CODE_AUTHORIZED );
        _bConnectionPoolEnabled = AppPropertiesService.getPropertyBoolean( PROPERTY_CONNECTION_POOL_ENABLED, false );
        _strConnectionPoolMaxTotalConnection = AppPropertiesService.getProperty( PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION );
        _strConnectionPoolMaxConnectionPerHost = AppPropertiesService.getProperty( PROPERTY_CONNECTION_POOL_MAX_TOTAL_CONNECTION_PER_HOST );
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
