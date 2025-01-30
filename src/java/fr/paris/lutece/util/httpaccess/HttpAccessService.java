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
import org.apache.hc.core5.http.HttpHost;
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
    private static HttpAccessService _singleton;

    private HttpClientConfiguration _httpClientConfiguration;
    
    
    private  PoolingHttpClientConnectionManager _connectionManager;
    
    private  CloseableHttpClient _httpClient;
   
    
    public HttpClientConfiguration getHttpClientConfiguration() {
		return _httpClientConfiguration;
	}



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
            _singleton = new HttpAccessService( new PropertiesHttpClientConfiguration( ) );
        }

        return _singleton;
    }
    /**
     * create new specific Instance of HttpAccessService 
     * @param httpClienConfiguration the httpClienConfiguration
     */
    public HttpAccessService(HttpClientConfiguration httpClienConfiguration)
    {
    	super();
    	_httpClientConfiguration=httpClienConfiguration;
    	_responseValidator = SimpleResponseValidator.loadFromProperty( PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED, DEFAULT_RESPONSE_CODE_AUTHORIZED );
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

    /**
     * get an HTTP client object using current configuration.
     *
     * @param method
     *            The method
     * @return An HTTP client authenticated
     */
    public synchronized CloseableHttpClient  getHttpClient(  String strTargetHost)
    {
    	
    	
			HttpClientBuilder clientBuilder = getHttpClientBuilder( );
			
		
		    // bNoProxy will be true when we would normally be using a proxy but matched on the NoProxyFor list
		  
		    if ( StringUtils.isNotBlank( _httpClientConfiguration.getProxyHost() ) )
		    {
		    	    boolean bNoProxy = ( StringUtils.isNotBlank( _httpClientConfiguration.getNoProxyFor() ) && matchesList( _httpClientConfiguration.getNoProxyFor().split( SEPARATOR ), strTargetHost) );
		            if(!bNoProxy && StringUtils.isNotBlank( _httpClientConfiguration.getProxyPort() ) && StringUtils.isNumeric( _httpClientConfiguration.getProxyPort() ))
		            {
		            	final HttpHost proxy = new HttpHost("http", _httpClientConfiguration.getProxyHost(),Integer.parseInt( _httpClientConfiguration.getProxyPort()));
		            	clientBuilder.setProxy(proxy);
		            }
		        
		    }
		
		    if ( _httpClientConfiguration.isConnectionPoolEnabled() )
		    {
		    	
		    	  if(_connectionManager==null)
		    	  {
		    		  _connectionManager= new PoolingHttpClientConnectionManager();
		        	
		                if (_httpClientConfiguration.getConnectionPoolMaxConnectionPerHost() !=null )
		                {
		                	_connectionManager.setDefaultMaxPerRoute(_httpClientConfiguration.getConnectionPoolMaxConnectionPerHost());
		                	
		                
		                }
		
		                if (  _httpClientConfiguration.getConnectionPoolMaxTotalConnection() !=null )
		                {
		                	_connectionManager.setMaxTotal(   _httpClientConfiguration.getConnectionPoolMaxTotalConnection()  );
		                }
		               
		    	  }
		            clientBuilder.setConnectionManager(_connectionManager);
		            clientBuilder.setConnectionManagerShared(true);   
		        
		     }

		    if (  _httpClientConfiguration.getSocketTimeout() != null  ||    _httpClientConfiguration.getConnectionTimeout() != null  )
		    {
		    	RequestConfig.Builder requestConfiguilder = RequestConfig.custom();
		    	 if(  _httpClientConfiguration.getConnectionTimeout()!=null)
		    	 {
		    		 requestConfiguilder.setConnectTimeout(Timeout.ofMilliseconds(  _httpClientConfiguration.getConnectionTimeout() ));
		    		 
		    	 }
		    	 
		    	 if( _httpClientConfiguration.getSocketTimeout() != null )
		    	 {
		    		
		    		 requestConfiguilder.setResponseTimeout(Timeout.ofMilliseconds( _httpClientConfiguration.getSocketTimeout() ));
		    		
		    		 
		    	 }
		    	 clientBuilder.setDefaultRequestConfig(requestConfiguilder.build());
		    	 //follow redirect
		         clientBuilder.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE);
        }

       

        _httpClient =clientBuilder.build();
    
    	
        return _httpClient;
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
