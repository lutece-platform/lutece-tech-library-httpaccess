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

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.HttpURLConnection;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    private static final String PROPERTY_CONTENT_CHARSET = "httpAccess.contentCharset";
    private static final String PROPERTY_HTTP_PROTOCOLE_CONTENT_CHARSET = "http.protocol.content-charset";
    private static final String PROPERTY_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String SEPARATOR = ",";
    private static final String PATTERN_FILENAME = ".*filename=\"([^\"]+)";

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
        String strResponseBody = StringUtils.EMPTY;

        try
        {
            HttpMethodBase method = new GetMethod( strUrl );
            method.setFollowRedirects( true );

            if ( authenticator != null )
            {
                authenticator.authenticateRequest( method, listElements );
            }

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
     * Send a POST HTTP request to an url and return the response content
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
     * Send a POST HTTP request to an url and return the response content
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
        String strResponseBody = StringUtils.EMPTY;

        PostMethod method = new PostMethod( strUrl );

        for ( Entry<String, String> entry : params.entrySet(  ) )
        {
            method.addParameter( entry.getKey(  ), entry.getValue(  ) );
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

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

        return strResponseBody;
    }

    /**
     * Send a POST HTTP request to an url and return the response content
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
     * Send a POST HTTP request to an url and return the response content
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

        return strResponseBody;
    }

    /**
     * Send a POST HTTP request to an url and return the response content
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
     * Send a POST HTTP request to an url and return the response content
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
        String strResponseBody = StringUtils.EMPTY;
        PostMethod method = new PostMethod( strUrl );

        if ( ( fileItems != null ) && !fileItems.isEmpty(  ) )
        {
            // Calculate the size
            int nSizeParam = 0;

            for ( Entry<String, List<String>> param : params.entrySet(  ) )
            {
                nSizeParam += param.getValue(  ).size(  );
            }

            int nSize = fileItems.size(  ) + nSizeParam;

            // Part in which we store the parameters + files
            Part[] parts = new Part[nSize];

            int nIndex = 0;

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
                        parts[nIndex] = new FilePart( paramFileItem.getKey(  ), file );
                        nIndex++;
                    }
                    catch ( Exception e )
                    {
                        String strError = "HttpAccess - Error writing file '" + fileItem.getName(  ) + "' : ";
                        AppLogService.error( strError + e.getMessage(  ), e );
                        throw new HttpAccessException( strError + e.getMessage(  ), e );
                    }
                }
            }

            // Additionnal parameters
            for ( Entry<String, List<String>> param : params.entrySet(  ) )
            {
                for ( String strValue : param.getValue(  ) )
                {
                    parts[nIndex] = new StringPart( param.getKey(  ), strValue );
                    nIndex++;
                }
            }

            method.setRequestEntity( new MultipartRequestEntity( parts, method.getParams(  ) ) );
        }

        if ( authenticator != null )
        {
            authenticator.authenticateRequest( method, listElements );
        }

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
            HttpClient client = getHttpClient( method );
            int nResponse = client.executeMethod( method );

            if ( nResponse != HttpURLConnection.HTTP_OK )
            {
                String strError = "HttpAccess - Error downloading file - return code : " + nResponse;
                throw new HttpAccessException( strError, null );
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
        String strContentCharset = AppPropertiesService.getProperty( PROPERTY_CONTENT_CHARSET );
        boolean bNoProxy = false;

        // Create an instance of HttpClient.
        HttpClient client = new HttpClient(  );

        // If proxy host and port found, set the correponding elements
        if ( StringUtils.isNotBlank( strProxyHost ) && StringUtils.isNotBlank( strProxyPort ) &&
                StringUtils.isNumeric( strProxyPort ) )
        {
            try
            {
                bNoProxy = ( StringUtils.isNotBlank( strNoProxyFor ) &&
                    matchesList( strNoProxyFor.split( SEPARATOR ), method.getURI(  ).getHost(  ) ) );
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

        // If hostname and domain name found, consider we are in NTLM authentication scheme
        // else if only username and password found, use simple UsernamePasswordCredentials
        if ( StringUtils.isNotBlank( strHostName ) && StringUtils.isNotBlank( strDomainName ) )
        {
            cred = new NTCredentials( strProxyUserName, strProxyPassword, strHostName, strDomainName );
        }
        else if ( StringUtils.isNotBlank( strProxyUserName ) && StringUtils.isNotBlank( strProxyPassword ) )
        {
            cred = new UsernamePasswordCredentials( strProxyUserName, strProxyPassword );
        }

        if ( ( cred != null ) && !bNoProxy )
        {
            AuthScope authScope = new AuthScope( strProxyHost, Integer.parseInt( strProxyPort ), strRealm );
            client.getState(  ).setProxyCredentials( authScope, cred );
            client.getParams(  ).setAuthenticationPreemptive( true );
            method.setDoAuthentication( true );
        }

        if ( StringUtils.isNotBlank( strContentCharset ) )
        {
            client.getParams(  ).setParameter( PROPERTY_HTTP_PROTOCOLE_CONTENT_CHARSET, strContentCharset );
        }

        return client;
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
}
