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

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class CustomHttpAccessServiceTest
{
    private final static class CustomHttpAccessService extends HttpAccessService
    {

        private boolean _getHttpClientBuilderCalled;

        public CustomHttpAccessService( HttpClientConfiguration httpClienConfiguration )
        {
            super( httpClienConfiguration );
        }

        @Override
        protected HttpClientBuilder getHttpClientBuilder( )
        {
            _getHttpClientBuilderCalled = true;
            return super.getHttpClientBuilder( );
        }

    }

    @BeforeClass
    public static void initLutece( )
    {
        // fake initialization
        AppPathService.init( "" );
        AppPropertiesService.init( "" );
    }

    @Test
    public void testCustomHttpClientBuilder( ) throws IOException
    {
        CustomHttpAccessService service = new CustomHttpAccessService( new HttpClientConfiguration( ) );
        service.getHttpClient( ).close( );
        assertTrue( service._getHttpClientBuilderCalled );
    }

}
