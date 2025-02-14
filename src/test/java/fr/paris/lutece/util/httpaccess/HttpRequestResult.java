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

public class HttpRequestResult
{

    public HttpRequestResult( )
    {

    }

    public HttpRequestResult( String _strMethodName, String [ ] headers, String requestBody, String requestPath )
    {
        super( );
        this._strMethodName = _strMethodName;
        this.headers = headers;
        this.requestBody = requestBody;
        this.requestPath = requestPath;
    }

    private String _strMethodName;
    private String [ ] headers;
    private String requestBody;
    private String requestPath;

    public String [ ] getHeaders( )
    {
        return headers;
    }

    public void setHeaders( String [ ] headers )
    {
        this.headers = headers;
    }

    public String getMethodName( )
    {
        return _strMethodName;
    }

    public void setMethodName( String strMethodName )
    {
        this._strMethodName = strMethodName;
    }

    public String getRequestBody( )
    {
        return requestBody;
    }

    public void setRequestBody( String requestBody )
    {
        this.requestBody = requestBody;
    }

    public String getRequestPath( )
    {
        return requestPath;
    }

    public void setRequestPath( String requestPath )
    {
        this.requestPath = requestPath;
    }

}
