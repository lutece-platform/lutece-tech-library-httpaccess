package fr.paris.lutece.util.httpaccess;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * 
 * HttpAccessService
 *
 */
public class HttpAccessService {
 	
	 
	private static final String DEFAULT_RESPONSE_CODE_AUTHORIZED="200";
	private static final String PROPERTY_PROXY_HOST = "httpAccess.proxyHost";
    private static final String PROPERTY_PROXY_PORT = "httpAccess.proxyPort";
    private static final String PROPERTY_PROXY_USERNAME = "httpAccess.proxyUserName";
    private static final String PROPERTY_PROXY_PASSWORD = "httpAccess.proxyPassword";
    private static final String PROPERTY_HOST_NAME = "httpAccess.hostName";
    private static final String PROPERTY_DOMAIN_NAME = "httpAccess.domainName";
    private static final String PROPERTY_REALM = "httpAccess.realm";
    private static final String PROPERTY_NO_PROXY_FOR = "httpAccess.noProxyFor";
    private static final String PROPERTY_CONTENT_CHARSET = "httpAccess.contentCharset";
    private static final String PROPERTY_SOCKET_TIMEOUT = "httpAccess.socketTimeout";
    private static final String PROPERTY_CONNEXION_TIMEOUT = "httpAccess.connexionTimeout";
    private static final String PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED = "httpAccess.responsesCodeAuthorized";
    private static final String PROPERTY_HTTP_PROTOCOLE_CONTENT_CHARSET = "http.protocol.content-charset";
	
    private static final String SEPARATOR = ",";
    
	private static HttpAccessService _singleton;
	private  String _strProxyHost;
	private  String _strProxyPort;
	private  String _strProxyUserName;
	private  String _strProxyPassword;
	private  String _strHostName;
	private  String _strDomainName;
	private  String _strRealm;
	private  String _strNoProxyFor;
	private  String _strContentCharset;
	private  String _strSocketTimeout;
	private  String _strConnexionTimeout;
	private  String[] _tabResponsesCodeErrors={DEFAULT_RESPONSE_CODE_AUTHORIZED};
  
	
	public static HttpAccessService getInstance()
	{
		if(_singleton == null)
		{
			_singleton=new HttpAccessService();
			_singleton.init();
		}
		return _singleton;
		
	}
	
 
	 	/**
	     * get an HTTP client object using current configuration
	     * @param method The method
	     * @return An HTTP client authenticated
	     */
	    public  HttpClient getHttpClient( HttpMethodBase method )
	    {
	        
	        boolean bNoProxy = false;

	        // Create an instance of HttpClient.
	        HttpClient client = new HttpClient(  );

	        // If proxy host and port found, set the correponding elements
	        if ( StringUtils.isNotBlank( _strProxyHost ) && StringUtils.isNotBlank( _strProxyPort ) &&
	                StringUtils.isNumeric( _strProxyPort ) )
	        {
	            try
	            {
	                bNoProxy = ( StringUtils.isNotBlank( _strNoProxyFor ) &&
	                    matchesList( _strNoProxyFor.split( SEPARATOR ), method.getURI(  ).getHost(  ) ) );
	            }
	            catch ( URIException e )
	            {
	                AppLogService.error( e.getMessage(  ), e );
	            }

	            if ( !bNoProxy )
	            {
	                client.getHostConfiguration(  ).setProxy( _strProxyHost, Integer.parseInt( _strProxyPort ) );
	            }
	        }

	        Credentials cred = null;

	        // If hostname and domain name found, consider we are in NTLM authentication scheme
	        // else if only username and password found, use simple UsernamePasswordCredentials
	        if ( StringUtils.isNotBlank( _strHostName ) && StringUtils.isNotBlank(_strDomainName ) )
	        {
	            cred = new NTCredentials( _strProxyUserName, _strProxyPassword, _strHostName, _strDomainName );
	        }
	        else if ( StringUtils.isNotBlank( _strProxyUserName ) && StringUtils.isNotBlank( _strProxyPassword ) )
	        {
	            cred = new UsernamePasswordCredentials( _strProxyUserName, _strProxyPassword );
	        }

	        if ( ( cred != null ) && !bNoProxy )
	        {
	            AuthScope authScope = new AuthScope( _strProxyHost, Integer.parseInt( _strProxyPort ), _strRealm );
	            client.getState(  ).setProxyCredentials( authScope, cred );
	            client.getParams(  ).setAuthenticationPreemptive( true );
	            method.setDoAuthentication( true );
	        }

	        if ( StringUtils.isNotBlank( _strContentCharset ) )
	        {
	            client.getParams(  ).setParameter( PROPERTY_HTTP_PROTOCOLE_CONTENT_CHARSET, _strContentCharset );
	        }
	        if ( StringUtils.isNotBlank( _strSocketTimeout ))
	        {
	        	client.getParams().setSoTimeout(Integer.parseInt(_strSocketTimeout));
	        }
	        if(StringUtils.isNotBlank(_strConnexionTimeout))
	        {
	        	client.getHttpConnectionManager().getParams().setConnectionTimeout(Integer.parseInt(_strConnexionTimeout));
	        }
	        return client;
	    }
	    
	    
	    
	    
	    /**
	     * test if the http code return is authorized
	     * @param nHttpResponseCode the http response code
	     * @return true if the http response code match the authorized  response codes 
	     */
	    public	boolean matchResponseCodeAuthorized(Integer nHttpResponseCode)
	    {
	    	for(String strCode:_tabResponsesCodeErrors)
	    	{
	    		if( nHttpResponseCode.equals(new Integer(strCode)))
	    		{
	    			return true;
	    		}
	    	}
	    	return false;
	    }
	 
	    /**
	     * heck if the text matches one of the pattern of the list
	     * @param listPatterns the list of patterns
	     * @param strText the text
	     * @return true if the text matches one of the pattern, false otherwise
	     */
	    private boolean matchesList( String[] listPatterns, String strText )
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
	     * Check if the pattern match the text. It also deals with special characters
	     * like * or ?
	     * @param strPattern the pattern
	     * @param strText the text
	     * @return true if the text matches the pattern, false otherwise
	     */
	    private static boolean matches( String strPattern, String strText )
	    {
	        String strTextTmp = strText + '\0';
	        String strPatternTmp = strPattern + '\0';

	        int nLength = strPatternTmp.length(  );

	        boolean[] states = new boolean[nLength + 1];
	        boolean[] old = new boolean[nLength + 1];
	        old[0] = true;

	        for ( int i = 0; i < strTextTmp.length(  ); i++ )
	        {
	            char c = strTextTmp.charAt( i );
	            states = new boolean[nLength + 1];

	            for ( int j = 0; j < nLength; j++ )
	            {
	                char p = strPatternTmp.charAt( j );

	                if ( old[j] && ( p == '*' ) )
	                {
	                    old[j + 1] = true;
	                }

	                if ( old[j] && ( p == c ) )
	                {
	                    states[j + 1] = true;
	                }

	                if ( old[j] && ( p == '?' ) )
	                {
	                    states[j + 1] = true;
	                }

	                if ( old[j] && ( p == '*' ) )
	                {
	                    states[j] = true;
	                }

	                if ( old[j] && ( p == '*' ) )
	                {
	                    states[j + 1] = true;
	                }
	            }

	            old = states;
	        }

	        return states[nLength];
	    }
	    /**
	     * init properties
	     */
	    private void init()
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
		        _strSocketTimeout = AppPropertiesService.getProperty( PROPERTY_SOCKET_TIMEOUT );
		        _strConnexionTimeout = AppPropertiesService.getProperty( PROPERTY_CONNEXION_TIMEOUT );
		        String strTabResponsesCodeAuthorized= AppPropertiesService.getProperty(PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED);
		        if(!StringUtils.isEmpty(strTabResponsesCodeAuthorized))
		        {	
		        	_tabResponsesCodeErrors=strTabResponsesCodeAuthorized.split(SEPARATOR);
		        }
		        
		        
		}
	
}
