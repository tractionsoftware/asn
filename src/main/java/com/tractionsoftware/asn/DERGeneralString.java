/*
 * Copyright (C) 2006-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tractionsoftware.asn;

import java.io.IOException;

/**
 * DER General String Class
 *
 * @author gkspencer
 */
public class DERGeneralString extends DERObject {

    // String value

    private String m_string;

    /**
     * Default constructor
     */
    public DERGeneralString() {
    }

    /**
     * Class constructor
     *
     * @param str
     *     String
     */
    public DERGeneralString(String str) {
        m_string = str;
    }

    /**
     * Return the string value
     *
     * @return String
     */
    public final String getValue() {
        return m_string;
    }

    /**
     * Decode the object
     *
     * @param buf
     *     DERBuffer
     */
    public void derDecode(DERBuffer buf) throws IOException {

        // Decode the type

        if (buf.unpackType() == DER.GeneralString) {

            // Unpack the length and bytes

            int len = buf.unpackLength();
            if (len > 0) {
                // Get the string bytes
                byte[] bytes = buf.unpackBytes(len);
                m_string = new String(bytes);
            }
            else {
                m_string = null;
            }
        }
        else {
            throw new IOException("Wrong DER type, expected GeneralString");
        }
    }

    /**
     * Encode the object
     *
     * @param buf
     *     DERBuffer
     */
    public void derEncode(DERBuffer buf) throws IOException {

        // Pack the type, length and bytes

        buf.packByte(DER.GeneralString);

        if (m_string != null) {
            byte[] bytes = m_string.getBytes();
            buf.packLength(bytes.length);
            buf.packBytes(bytes, 0, bytes.length);
        }
        else {
            buf.packLength(0);
        }
    }

    /**
     * Return as a string
     *
     * @return String
     */
    public String toString() {
        return "[GeneralString:" + m_string + "]";
    }

}
