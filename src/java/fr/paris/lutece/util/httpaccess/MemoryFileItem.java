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


import fr.paris.lutece.portal.service.upload.MultipartItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * MemoryFileItem
 *
 */
public class MemoryFileItem implements MultipartItem
{
    private static final long serialVersionUID = 922231338093963479L;
    private byte [ ] _data;
    private String _strName;
    private long _lSize;
    private String _strContentType;

    /**
     * Constructor
     * 
     * @param data
     *            the data
     * @param strName
     *            the file name
     * @param lSize
     *            the size of the file
     * @param strContentType
     *            the content type of the file
     */
    public MemoryFileItem( byte [ ] data, String strName, long lSize, String strContentType )
    {
        _data = data;
        _strName = strName;
        _lSize = lSize;
        _strContentType = strContentType;
    }

    /**
     * {@inheritDoc}
     */
    public void delete( )
    {
        // Nothing
    }

    /**
     * {@inheritDoc}
     */
    public byte [ ] get( )
    {
        return _data;
    }

    /**
     * {@inheritDoc}
     */
    public String getContentType( )
    {
        return _strContentType;
    }

    /**
     * Not supported
     * 
     * @return null
     */
    public String getFieldName( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream( ) throws IOException
    {
    	return new ByteArrayInputStream( _data );
    }

    /**
     * {@inheritDoc}
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * {@inheritDoc}
     */
    public long getSize( )
    {
        return _lSize;
    }

    /**
     * {@inheritDoc}
     */
    public String getString( )
    {
        return new String( get( ) );
    }

    /**
     * {@inheritDoc}
     */
    public String getString( String strEncoding ) throws UnsupportedEncodingException
    {
        return new String( get( ), strEncoding );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFormField( )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInMemory( )
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void setFieldName( String strFieldName )
    {
        // Nothing
    }

    /**
     * {@inheritDoc}
     */
    public void setFormField( boolean state )
    {
        // Nothing
    }
}