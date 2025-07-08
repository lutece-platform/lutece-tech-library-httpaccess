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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Http net Object Accessor
 */
public class HttpAccessTest
{

    private MockWebServer mockWebServer;
    private ObjectMapper _objectMapper = new ObjectMapper( );
    private Logger _logger = Logger.getLogger( this.getClass( ) );

    @BeforeClass
    public static void initLutece( )
    {
        // fake initialization
        try
        {
            AppPathService.init( "" );
            AppPropertiesService.init( "" );
        }
        catch ( Exception e )
        {
            // ignore
        }
    }

    @Test
    public void testDoGetProxy( ) throws HttpAccessException, JsonMappingException, JsonProcessingException
    {
        String strUrlTestHttp = mockWebServer.url( "/test?param1=1&parma2=2" ).toString( );
        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setProxyHost( "localhost_butdoesnot.exists" );
        configuration.setProxyPort( "3128" );
        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );
        try
        {
            httpAccess.doGet( strUrlTestHttp, null, null, null, null );
            fail( "Should have failed to connect to proxy" );
        }
        catch( HttpAccessException e )
        {
            assertEquals( e.getCause( ).getClass( ), UnknownHostException.class );
        }
    }

    @Test
    public void testDoGetProxyNoProxy( ) throws HttpAccessException, IOException
    {
        String strUrlTestHttp = mockWebServer.url( "/test?param1=1&parma2=2" ).toString( );
        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setProxyHost( "localhost_butdoesnot.exists" );
        configuration.setProxyPort( "3128" );
        configuration.setNoProxyFor( mockWebServer.getHostName( ) );
        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );
        httpAccess.doGet( strUrlTestHttp, null, null, null, null );
        String strTest = httpAccess.doGet( strUrlTestHttp, null, null, null, null );

        HttpRequestResult jsonRespone = _objectMapper.readValue( strTest, HttpRequestResult.class );
        assertEquals( "GET", jsonRespone.getMethodName( ) );
        _logger.debug( strTest );
    }

    @Test
    public void testDoGet( ) throws IOException, HttpAccessException
    {

        String strUrlTestHttp = mockWebServer.url( "/test?param1=1&parma2=2" ).toString( );
        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 10000 );
        configuration.setSocketTimeout( 10000 );
        // configuration.setProxyHost("");
        // configuration.setNoProxyFor("*.paris.mdp");
        // configuration.setProxyPort("8080");

        Map<String, String> mapHeaders = new HashMap<String, String>( );
        Map<String, String> mapHeadersResponse = new HashMap<String, String>( );

        mapHeaders.put( "Authorization", " Basic token" );

        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );

        String strTest = httpAccess.doGet( strUrlTestHttp, null, null, mapHeaders, mapHeadersResponse );

        HttpRequestResult jsonRespone = _objectMapper.readValue( strTest, HttpRequestResult.class );
        assertEquals( "GET", jsonRespone.getMethodName( ) );
        _logger.debug( strTest );

    }

    @Test
    public void testGetInvalidResponseBody( ) throws IOException, HttpAccessException
    {
        String strUrlTestHttp = mockWebServer.url( "/500" ).toString( );
        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 10000 );
        configuration.setSocketTimeout( 10000 );

        Map<String, String> mapHeaders = new HashMap<String, String>( );
        Map<String, String> mapHeadersResponse = new HashMap<String, String>( );

        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );

        try
        {
            httpAccess.doGet( strUrlTestHttp, null, null, mapHeaders, mapHeadersResponse );
        }
        catch( InvalidResponseStatus e )
        {
            assertEquals( 500, e.getResponseStatus( ) );
            HttpRequestResult jsonRespone = _objectMapper.readValue( e.getResponseBody( ), HttpRequestResult.class );
            assertEquals( "GET", jsonRespone.getMethodName( ) );
        }
    }

    @Test
    public void testDoDelete( ) throws IOException, HttpAccessException
    {

        String strUrlTestHttp = mockWebServer.url( "test?id_delete=125" ).toString( );

        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 1000 );
        configuration.setSocketTimeout( 10000 );
        // configuration.setProxyHost("");
        configuration.setNoProxyFor( "*.paris.mdp" );
        configuration.setProxyPort( "8080" );

        Map<String, String> mapHeaders = new HashMap<String, String>( );
        Map<String, String> mapHeadersResponse = new HashMap<String, String>( );

        mapHeaders.put( "Authorization", " Basic token" );

        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );
        String strTest = httpAccess.doDelete( strUrlTestHttp, null, null, mapHeaders, mapHeadersResponse );
        HttpRequestResult jsonRespone = _objectMapper.readValue( strTest, HttpRequestResult.class );
        assertEquals( "DELETE", jsonRespone.getMethodName( ) );
        _logger.debug( strTest );
    }

    @Test
    public void testDoPost( ) throws IOException, HttpAccessException
    {

        String strUrlTestHttp = mockWebServer.url( "/test" ).toString( );

        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 10000 );
        configuration.setSocketTimeout( 10000 );
        // configuration.setProxyHost("");
        configuration.setNoProxyFor( "*.paris.mdp" );
        configuration.setProxyPort( "8080" );

        Map<String, String> mapHeaders = new HashMap<String, String>( );
        Map<String, String> mapParameters = new HashMap<String, String>( );

        Map<String, String> mapHeadersResponse = new HashMap<String, String>( );

        mapHeaders.put( "Authorization", " Basic " );
        mapParameters.put( "grant_type", "client_credentials" );
        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );
        String strTest = httpAccess.doPost( strUrlTestHttp, mapParameters, null, null, mapHeaders, mapHeadersResponse );
        HttpRequestResult jsonRespone = _objectMapper.readValue( strTest, HttpRequestResult.class );
        assertEquals( "POST", jsonRespone.getMethodName( ) );
        _logger.debug( strTest );

    }

    @Test
    public void testDoPut( ) throws HttpAccessException, IOException
    {

        String strUrlTestHttp = mockWebServer.url( "/test/225a" ).toString( );

        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 10000 );
        configuration.setSocketTimeout( 10000 );
        // configuration.setProxyHost("");
        configuration.setNoProxyFor( "*.paris.mdp" );
        configuration.setProxyPort( "8080" );

        Map<String, String> mapHeaders = new HashMap<String, String>( );
        Map<String, String> mapParameters = new HashMap<String, String>( );

        Map<String, String> mapHeadersResponse = new HashMap<String, String>( );

        mapHeaders.put( "Authorization", " Basic " );
        mapParameters.put( "grant_type", "client_credentials" );
        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );

        String strTest = httpAccess.doPut( strUrlTestHttp, null, null, mapParameters, mapHeaders, mapHeadersResponse );
        HttpRequestResult jsonRespone = _objectMapper.readValue( strTest, HttpRequestResult.class );
        assertEquals( "PUT", jsonRespone.getMethodName( ) );
        _logger.debug( strTest );

    }

    @Test
    public void testDoPostMultipart( ) throws HttpAccessException, IOException
    {

        String strUrlTestHttp = mockWebServer.url( "/test" ).toString( );

        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 1000 );
        configuration.setSocketTimeout( 10000 );
        // configuration.setProxyHost("");
        configuration.setNoProxyFor( "*.paris.mdp" );
        configuration.setProxyPort( "8080" );

        Map<String, String> mapHeaders = new HashMap<String, String>( );

        Map<String, List<String>> mapParameters = new HashMap<String, List<String>>( );
        List<String> params = new ArrayList<String>( );
        params.add( "test1 é" );
        params.add( "test2" );

        mapParameters.put( "identityChange", params );
        Map<String, String> mapHeadersResponse = new HashMap<String, String>( );

        // mapHeaders.put("Content-Type", "application/json; charset=utf-8");
        mapHeaders.put( "client_code", "RhssoFranceConnect" );

        // mapParameters.put("grant_type", "client_credentials");
        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );

        String strXml = "<xml> éttezez</xml>";

        MemoryFileItem meFileItem = new MemoryFileItem( strXml.getBytes( "UTF-8" ), "test_file.xml", strXml.getBytes( "UTF-8" ).length,
                "text/xml; charset=UTF-8" );

        Map<String, FileItem> mapFileItem = new HashMap( );

        mapFileItem.put( "file_download_param_1", meFileItem );

        String strTest = httpAccess.doPostMultiPart( strUrlTestHttp, mapParameters, mapFileItem, null, null, mapHeadersResponse, mapHeadersResponse );
        HttpRequestResult jsonRespone = _objectMapper.readValue( strTest, HttpRequestResult.class );
        assertEquals( "POST", jsonRespone.getMethodName( ) );
        _logger.debug( strTest );

    }

    @Test
    public void testDoPostJson( ) throws HttpAccessException, IOException
    {
        String strJson = "{\n" + "  \"identity_change\" : {\n" + "    \"identity\" : {\n" + "    \"customer_id\" : \"b59f9424-6c5f-4bc7-a12545\",\n"
                + "    \"attributes\" : {\n" + "      \"birthcountry\" : {\n" + "        \"key\" : \"birthcountry\",\n" + "        \"type\" : \"string\",\n"
                + "        \"value\" : \"FRANCE\",\n" + "        \"application_last_update\" : \"RhssoFranceConnect\",\n"
                + "        \"date_last_update\" : 1622468105000,\n" + "        \"certified\" : true,\n" + "        \"writable\" : true,\n"
                + "        \"certificate\" : {\n" + "          \"certifier_code\" : \"fccertifier\",\n"
                + "          \"certifier_name\" : \"France Connect Certifier\",\n" + "          \"certificate_level\" : 3,\n"
                + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n" + "      \"birthdate\" : {\n"
                + "        \"key\" : \"birthdate\",\n" + "        \"type\" : \"string\",\n" + "        \"value\" : \"24/08/1982\",\n"
                + "        \"application_last_update\" : \"RhssoFranceConnect\",\n" + "        \"date_last_update\" : 1622468105000,\n"
                + "        \"certified\" : true,\n" + "        \"writable\" : true,\n" + "        \"certificate\" : {\n"
                + "          \"certifier_code\" : \"fccertifier\",\n" + "          \"certifier_name\" : \"France Connect Certifier\",\n"
                + "          \"certificate_level\" : 3,\n" + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n"
                + "      \"gender\" : {\n" + "        \"key\" : \"gender\",\n" + "        \"type\" : \"string\",\n" + "        \"value\" : \"1\",\n"
                + "        \"application_last_update\" : \"RhssoFranceConnect\",\n" + "        \"date_last_update\" : 1622468105000,\n"
                + "        \"certified\" : true,\n" + "        \"writable\" : true,\n" + "        \"certificate\" : {\n"
                + "          \"certifier_code\" : \"fccertifier\",\n" + "          \"certifier_name\" : \"France Connect Certifier\",\n"
                + "          \"certificate_level\" : 3,\n" + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n"
                + "      \"birthplace\" : {\n" + "        \"key\" : \"birthplace\",\n" + "        \"type\" : \"string\",\n"
                + "        \"value\" : \"PARIS 07\",\n" + "        \"application_last_update\" : \"RhssoFranceConnect\",\n"
                + "        \"date_last_update\" : 1622468105000,\n" + "        \"certified\" : true,\n" + "        \"writable\" : true,\n"
                + "        \"certificate\" : {\n" + "          \"certifier_code\" : \"fccertifier\",\n"
                + "          \"certifier_name\" : \"France Connect Certifier\",\n" + "          \"certificate_level\" : 3,\n"
                + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n" + "      \"login\" : {\n" + "        \"key\" : \"login\",\n"
                + "        \"type\" : \"string\",\n" + "        \"value\" : \"testbp2022@yopmail.com\",\n"
                + "        \"application_last_update\" : \"RhssoFranceConnect\",\n" + "        \"date_last_update\" : 1622468105000,\n"
                + "        \"certified\" : true,\n" + "        \"writable\" : true,\n" + "        \"certificate\" : {\n"
                + "          \"certifier_code\" : \"fccertifier\",\n" + "          \"certifier_name\" : \"France Connect Certifier\",\n"
                + "          \"certificate_level\" : 3,\n" + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n"
                + "      \"family_name\" : {\n" + "        \"key\" : \"family_name\",\n" + "        \"type\" : \"string\",\n"
                + "        \"value\" : \"DUBOIS\",\n" + "        \"application_last_update\" : \"RhssoFranceConnect\",\n"
                + "        \"date_last_update\" : 1622468105000,\n" + "        \"certified\" : true,\n" + "        \"writable\" : true,\n"
                + "        \"certificate\" : {\n" + "          \"certifier_code\" : \"fccertifier\",\n"
                + "          \"certifier_name\" : \"France Connect Certifier\",\n" + "          \"certificate_level\" : 3,\n"
                + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n" + "      \"first_name\" : {\n"
                + "        \"key\" : \"first_name\",\n" + "        \"type\" : \"string\",\n" + "        \"value\" : \"Angela Claire Louise Bernard\",\n"
                + "        \"application_last_update\" : \"RhssoFranceConnect\",\n" + "        \"date_last_update\" : 1622468105000,\n"
                + "        \"certified\" : true,\n" + "        \"writable\" : true,\n" + "        \"certificate\" : {\n"
                + "          \"certifier_code\" : \"fccertifier\",\n" + "          \"certifier_name\" : \"France Connect Certifier\",\n"
                + "          \"certificate_level\" : 3,\n" + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n"
                + "      \"email\" : {\n" + "        \"key\" : \"email\",\n" + "        \"type\" : \"string\",\n"
                + "        \"value\" : \"testbp2022@yopmail.com\",\n" + "        \"application_last_update\" : \"RhssoFranceConnect\",\n"
                + "        \"date_last_update\" : 1622468105000,\n" + "        \"certified\" : false,\n" + "        \"writable\" : true,\n"
                + "        \"certificate\" : null\n" + "      },\n" + "    \"fc_birthdate\" : {\n" + "        \"key\" : \"fc_birthdate\",\n"
                + "        \"type\" : \"string\",\n" + "        \"value\" : \"1962-08-85\",\n"
                + "        \"application_last_update\" : \"RhssoFranceConnect\",\n" + "        \"date_last_update\" : 1622468105000,\n"
                + "        \"certified\" : true,\n" + "        \"writable\" : true,\n" + "        \"certificate\" : {\n"
                + "          \"certifier_code\" : \"fccertifier\",\n" + "          \"certifier_name\" : \"France Connect Certifier\",\n"
                + "          \"certificate_level\" : 3,\n" + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n" + "   \n"
                + "      \"fc_given_name\" : {\n" + "        \"key\" : \"fc_given_name\",\n" + "        \"type\" : \"string\",\n"
                + "        \"value\" : \"Angela Claire Louise Bernard 3\",\n" + "        \"application_last_update\" : \"RhssoFranceConnect\",\n"
                + "        \"date_last_update\" : 1622468105000,\n" + "        \"certified\" : true,\n" + "        \"writable\" : true,\n"
                + "        \"certificate\" : {\n" + "          \"certifier_code\" : \"fccertifier\",\n"
                + "          \"certifier_name\" : \"France Connect Certifier\",\n" + "          \"certificate_level\" : 3,\n"
                + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n" + "      \"fc_birthplace\" : {\n"
                + "        \"key\" : \"fc_birthplace\",\n" + "        \"type\" : \"string\",\n" + "        \"value\" : \"75107\",\n"
                + "        \"application_last_update\" : \"RhssoFranceConnect\",\n" + "        \"date_last_update\" : 1622468105000,\n"
                + "        \"certified\" : true,\n" + "        \"writable\" : true,\n" + "        \"certificate\" : {\n"
                + "          \"certifier_code\" : \"fccertifier\",\n" + "          \"certifier_name\" : \"France Connect Certifier\",\n"
                + "          \"certificate_level\" : 3,\n" + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n" + "  \n"
                + "      \"fc_gender\" : {\n" + "        \"key\" : \"fc_gender\",\n" + "        \"type\" : \"string\",\n" + "        \"value\" : \"female\",\n"
                + "        \"application_last_update\" : \"RhssoFranceConnect\",\n" + "        \"date_last_update\" : 1622468105000,\n"
                + "        \"certified\" : true,\n" + "        \"writable\" : true,\n" + "        \"certificate\" : {\n"
                + "          \"certifier_code\" : \"fccertifier\",\n" + "          \"certifier_name\" : \"France Connect Certifier\",\n"
                + "          \"certificate_level\" : 3,\n" + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n" + "     \n"
                + "      \"fc_family_name\" : {\n" + "        \"key\" : \"fc_family_name\",\n" + "        \"type\" : \"string\",\n"
                + "        \"value\" : \"DUBOIS\",\n" + "        \"application_last_update\" : \"RhssoFranceConnect\",\n"
                + "        \"date_last_update\" : 1622468105000,\n" + "        \"certified\" : true,\n" + "        \"writable\" : true,\n"
                + "        \"certificate\" : {\n" + "          \"certifier_code\" : \"fccertifier\",\n"
                + "          \"certifier_name\" : \"France Connect Certifier\",\n" + "          \"certificate_level\" : 3,\n"
                + "          \"certificate_exp_date\" : null\n" + "        }\n" + "      },\n" + "      \"fc_birthcountry\" : {\n"
                + "        \"key\" : \"fc_birthcountry\",\n" + "        \"type\" : \"string\",\n" + "        \"value\" : \"99100\",\n"
                + "        \"application_last_update\" : \"RhssoFranceConnect\",\n" + "        \"date_last_update\" : 1622468105000,\n"
                + "        \"certified\" : true,\n" + "        \"writable\" : true,\n" + "        \"certificate\" : {\n"
                + "          \"certifier_code\" : \"fccertifier\",\n" + "          \"certifier_name\" : \"France Connect Certifier\",\n"
                + "          \"certificate_level\" : 3,\n" + "          \"certificate_exp_date\" : null\n" + "        }\n" + "    }\n" + "  }\n" + "}\n" + ",\n"
                + "    \"author\" : {\n" + "      \"id\" : \"usager\",\n" + "      \"type\" : 1,\n" + "      \"application_code\" : \"RhssoFranceConnect\"\n"
                + "    }\n" + "  }\n" + "}";

        String strUrlTestHttp = mockWebServer.url( "/test" ).toString( );

        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 1000 );
        configuration.setSocketTimeout( 10000 );
        // configuration.setProxyHost("");
        configuration.setNoProxyFor( "*.paris.mdp" );
        configuration.setProxyPort( "8080" );

        Map<String, String> mapHeaders = new HashMap<String, String>( );

        Map<String, List<String>> mapParameters = new HashMap<String, List<String>>( );
        List<String> params = new ArrayList<String>( );
        params.add( strJson );
        mapParameters.put( "identityChange", params );
        Map<String, String> mapHeadersResponse = new HashMap<String, String>( );

        // mapHeaders.put("Content-Type", "application/json; charset=utf-8");
        mapHeaders.put( "client_code", "RhssoFranceConnect" );

        // mapParameters.put("grant_type", "client_credentials");
        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );

        String strTest = httpAccess.doPostJSON( strUrlTestHttp, strJson, mapHeaders, mapHeadersResponse );

        HttpRequestResult jsonRespone = _objectMapper.readValue( strTest, HttpRequestResult.class );
        assertEquals( "POST", jsonRespone.getMethodName( ) );
        _logger.debug( strTest );

    }

    @Test
    public void testDoDownloadFile( ) throws HttpAccessException
    {

        String strUrlTestHttp = mockWebServer.url( "/test" ).toString( );

        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 1000 );
        configuration.setSocketTimeout( 10000 );
        configuration.setProxyHost( "" );
        configuration.setNoProxyFor( "*.paris.mdp" );
        configuration.setProxyPort( "8080" );

        // mapParameters.put("grant_type", "client_credentials");
        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );

        httpAccess.downloadFile( strUrlTestHttp, "/tmp/favicon-16x16.png" );

    }

    @Test
    public void testGetFileName( ) throws HttpAccessException
    {

        String strUrlTestHttp = mockWebServer.url( "/test" ).toString( );

        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 1000 );
        configuration.setSocketTimeout( 10000 );
        configuration.setProxyHost( "" );
        configuration.setNoProxyFor( "*.paris.mdp" );
        configuration.setProxyPort( "8080" );

        // mapParameters.put("grant_type", "client_credentials");
        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );

        httpAccess.getFileName( strUrlTestHttp );

    }

    @Test
    public void testConnectionPool( )
    {

        List<Integer> listOfNumbersGet = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 );
        String strUrlTestHttp = mockWebServer.url( "/test" ).toString( );

        HttpClientConfiguration configuration = new HttpClientConfiguration( );
        configuration.setConnectionTimeout( 10000 );
        configuration.setSocketTimeout( 100000 );
        // onfiguration.setProxyHost("");
        configuration.setNoProxyFor( "*.paris.mdp" );
        configuration.setProxyPort( "8080" );
        configuration.setConnectionPoolEnabled( true );
        configuration.setConnectionPoolMaxTotalConnection( 3 );
        configuration.setConnectionPoolMaxConnectionPerHost( 3 );

        Map<String, String> mapHeaders = new HashMap<String, String>( );
        Map<String, String> mapHeadersResponse = new HashMap<String, String>( );

        mapHeaders.put( "Authorization", " Basic token with connection pool" );
        HttpAccessService httpAccessService = new HttpAccessService( configuration );
        listOfNumbersGet.parallelStream( ).forEach( x -> {

            HttpAccess httpAccess = new HttpAccess( httpAccessService, new MockResponseStatusValidator( ) );

            String strTest;
            try
            {
                strTest = httpAccess.doGet( strUrlTestHttp, null, null, mapHeaders, mapHeadersResponse );
                HttpRequestResult jsonRespone = _objectMapper.readValue( strTest, HttpRequestResult.class );
                assertEquals( "GET", jsonRespone.getMethodName( ) );
                _logger.debug( strTest );
            }
            catch( HttpAccessException e )
            {
                assertFalse( true );
            }

            catch( JsonProcessingException e )
            {
                // TODO Auto-generated catch block
                assertFalse( true );
            }
            catch( IOException e )
            {
                // TODO Auto-generated catch block
                assertFalse( true );
            }

        } );
    }

    @Before
    public void init( ) throws IOException
    {
        this.mockWebServer = new MockWebServer( );
        this.mockWebServer.setDispatcher( new Dispatcher( )
        {

            @Override
            public MockResponse dispatch( RecordedRequest request )
            {
                int responseCode = 200;
                List<String> pathSegments = request.getRequestUrl( ).pathSegments( );
                if ( StringUtils.isNumeric( pathSegments.get( pathSegments.size( ) - 1 ) ) )
                {
                    responseCode = Integer.parseInt( pathSegments.get( pathSegments.size( ) - 1 ) );
                }
                return new MockResponse( ).addHeader( "Content-Type", "application/json; charset=utf-8" ).setResponseCode( responseCode )
                        .setBody( printRequest( request ) );

            }
        } );

        this.mockWebServer.start( 18080 );

        try
        {
            AppPathService.init( "" );
            AppPropertiesService.init( "" );
        }
        catch ( Exception e )
        {
            // ignore
        }
    }

    @After
    public void stopServer( ) throws IOException
    {

        this.mockWebServer.shutdown( );
    }

    private String printRequest( RecordedRequest request )
    {

        try
        {
            return _objectMapper.writeValueAsString( new HttpRequestResult( request.getMethod( ), request.getHeaders( ).getNamesAndValues$okhttp( ),
                    request.getBody( ).readUtf8( ), request.getPath( ) ) );
        }
        catch( JsonProcessingException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace( );
        }

        return "";

    }

}
