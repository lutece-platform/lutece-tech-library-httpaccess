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

/**
 * InvalidResponseStatus
 */
public class InvalidResponseStatus extends HttpAccessException
{
    private static final long serialVersionUID = 275350791204870316L;
    private final int _nResponseStatus;
    private final String _strResponseBody;

    /**
     * Constructor
     * 
     * @param strMessage
     *            The message
     * @param nResponseStatus
     *            The response status
     * @param cause
     *            The cause
     */
    public InvalidResponseStatus( String strMessage, Integer nResponseStatus, String strResponseBody, Exception cause )
    {
        super( strMessage, nResponseStatus, cause );
        _nResponseStatus = nResponseStatus;
        _strResponseBody = strResponseBody;
    }

    @Override
    public String getMessage( )
    {
        return super.getMessage( ) + "\nResponse body :\n>>>\n" + _strResponseBody + "\n<<<";
    }

    /**
     * Return the invalid response status
     * 
     * @return the invalid response status
     */
    public int getResponseStatus( )
    {
        return _nResponseStatus;
    }

    /**
     * Return the response body
     * 
     * @return the reponse body
     * @since 3.0.3
     */
    public String getResponseBody( )
    {
        return _strResponseBody;
    }
}
