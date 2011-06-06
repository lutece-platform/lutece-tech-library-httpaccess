/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * Http net Object Accessor <br/>
 */
public class HttpAccess
{
    // proxy authentication settings
    private static final String PROPERTY_PROXY_HOST = "httpAccess.proxyHost";
    private static final String PROPERTY_PROXY_PORT = "httpAccess.proxyPort";
    private static final String PROPERTY_PROXY_USERNAME = "httpAccess.proxyUserName";
    private static final String PROPERTY_PROXY_PASSWORD = "httpAccess.proxyPassword";
    private static final String PROPERTY_HOST_NAME = "httpAccess.hostName";
    private static final String PROPERTY_DOMAIN_NAME = "httpAccess.domainName";
    private static final String PROPERTY_REALM = "httpAccess.realm";
    private static final String PROPERTY_NO_PROXY_FOR = "httpAccess.noProxyFor";
    private static final String SEPARATOR = ",";

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * @param strUrl The Url to access
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public String doGet( String strUrl ) throws HttpAccessException
    {
        String strResponseBody = null;

        try
        {
            HttpMethodBase method = new GetMethod( strUrl );
            method.setFollowRedirects( true );

            try
            {
                HttpClient client = getHttpClient( method );
                int nResponse = client.executeMethod( method );

                if ( nResponse != HttpURLConnection.HTTP_OK )
                {
                    String strError = "HttpAccess - Error getting URL : " + strUrl + " - return code : " + nResponse;
                    throw new HttpAccessException( strError, null );
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
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
            throw new HttpAccessException( e.getMessage(  ), e );
        }

        return strResponseBody;
    }

    /**
     * Send a GET HTTP request to an Url and return the response content.
     * @param strUrl The Url to access
     * @return The response content of the Get request to the given Url
     * @throws HttpAccessException if there is a problem to access to the given Url
     */
    public void downloadFile( String strUrl, String strFilePath )
        throws HttpAccessException
    {
        try
        {
            HttpMethodBase method = new GetMethod( strUrl );
            method.setFollowRedirects( true );

            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;

            try
            {
                HttpClient client = getHttpClient( method );
                int nResponse = client.executeMethod( method );

                if ( nResponse != HttpURLConnection.HTTP_OK )
                {
                    String strError = "HttpAccess - Error downloading file - return code : " + nResponse;
                    throw new HttpAccessException( strError, null );
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
                }

                // Release the connection.
                method.releaseConnection(  );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
            throw new HttpAccessException( e.getMessage(  ), e );
        }
    }

    /**
     * Create an HTTP client object using current configuration
     * @param method The method
     * @return An HTTP client authenticated
     */
    private HttpClient getHttpClient( HttpMethodBase method )
    {
        String strProxyHost = AppPropertiesService.getProperty( PROPERTY_PROXY_HOST );
        String strProxyPort = AppPropertiesService.getProperty( PROPERTY_PROXY_PORT );
        String strProxyUserName = AppPropertiesService.getProperty( PROPERTY_PROXY_USERNAME );
        String strProxyPassword = AppPropertiesService.getProperty( PROPERTY_PROXY_PASSWORD );
        String strHostName = AppPropertiesService.getProperty( PROPERTY_HOST_NAME );
        String strDomainName = AppPropertiesService.getProperty( PROPERTY_DOMAIN_NAME );
        String strRealm = AppPropertiesService.getProperty( PROPERTY_REALM );
        String strNoProxyFor = AppPropertiesService.getProperty( PROPERTY_NO_PROXY_FOR );
        boolean bNoProxy = false;

        // Create an instance of HttpClient.
        HttpClient client = new HttpClient(  );

        // if proxy host and port found, set the correponding elements
        if ( ( strProxyHost != null ) && ( !strProxyHost.equals( "" ) ) && ( strProxyPort != null ) &&
                ( !strProxyPort.equals( "" ) ) )
        {
            try
            {
                bNoProxy = ( ( strNoProxyFor != null ) && !strNoProxyFor.equals( "" ) ) &&
                    matchesList( strNoProxyFor.split( SEPARATOR ), method.getURI(  ).getHost(  ) );
            }
            catch ( URIException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }

            if ( !bNoProxy )
            {
                client.getHostConfiguration(  ).setProxy( strProxyHost, Integer.parseInt( strProxyPort ) );
            }
        }

        Credentials cred = null;

        //  if hostname and domain name found, consider we are in NTLM authentication scheme
        // else if only username and password found, use simple UsernamePasswordCredentials
        if ( ( strHostName != null ) && ( strDomainName != null ) )
        {
            cred = new NTCredentials( strProxyUserName, strProxyPassword, strHostName, strDomainName );
        }
        else if ( ( strProxyUserName != null ) && ( strProxyPassword != null ) )
        {
            cred = new UsernamePasswordCredentials( strProxyUserName, strProxyPassword );
        }

        if ( ( cred != null ) && !bNoProxy )
        {
            client.getState(  ).setProxyCredentials( strRealm, strProxyHost, cred );
            client.getState(  ).setAuthenticationPreemptive( true );
            method.setDoAuthentication( true );
        }

        return client;
    }

    //application du pattern matcher sur la base d un seul texte et d'une liste de patterns
    private boolean matchesList( String[] patterns, String text )
    {
        if ( patterns == null )
        {
            return false;
        }

        for ( String pattern : patterns )
        {
            if ( matches( pattern, text ) )
            {
                return true;
            }
        }

        return false;
    }

    //algorithme de verification base sur les caracteres * et ?
    private static boolean matches( String pattern, String text )
    {
        text += '\0';
        pattern += '\0';

        int N = pattern.length(  );

        boolean[] states = new boolean[N + 1];
        boolean[] old = new boolean[N + 1];
        old[0] = true;

        for ( int i = 0; i < text.length(  ); i++ )
        {
            char c = text.charAt( i );
            states = new boolean[N + 1];

            for ( int j = 0; j < N; j++ )
            {
                char p = pattern.charAt( j );

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

        return states[N];
    }
}
