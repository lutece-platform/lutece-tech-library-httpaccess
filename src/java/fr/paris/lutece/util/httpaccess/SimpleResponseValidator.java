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

import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * SimpleResponseValidator
 */
public class SimpleResponseValidator implements ResponseStatusValidator
{
    /** The Constant SEPARATOR. */
    private static final String SEPARATOR = ",";

    private final String [ ] _aAuthorizedStatus;

    /**
     * Constructor
     * 
     * @param strList
     *            The list of authorized status
     */
    public SimpleResponseValidator( String strList )
    {
        _aAuthorizedStatus = strList.split( SEPARATOR );
    }

    /**
     * Load a Status validator from properties
     * 
     * @param strProperty
     *            The property key
     * @param strDefault
     *            The default value
     * @return A responseValidator initialized with the authorized status list
     */
    public static ResponseStatusValidator loadFromProperty( String strProperty, String strDefault )
    {
        String strAuthorizedStatusList = AppPropertiesService.getProperty( strProperty, strDefault );
        return new SimpleResponseValidator( strAuthorizedStatusList );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean validate( int nStatus )
    {
        String strStatus = String.valueOf( nStatus );
        for ( String strCode : _aAuthorizedStatus )
        {
            if ( strStatus.equals( strCode.trim( ) ) )
            {
                return true;
            }
        }

        return false;
    }

}
