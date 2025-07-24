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

import java.nio.ByteOrder;

/**
 * The data packing class is a static class that is used to pack and unpack basic data types to/from network byte order
 * and Intel byte order.
 *
 * @author gkspencer
 */
public final class DataPacker {

    // Flag to indicate the byte order of the platform that we are currently running on.
    private static final boolean bigEndian = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);

    /**
     * Return the current endian setting.
     *
     * @return true if the system is big endian, else false.
     */
    public static boolean isBigEndian() {
        return bigEndian;
    }

    /**
     * Unpack a null terminated data string from the data buffer.
     *
     * @param typ
     *     Data type, as specified by SMBDataType.
     * @param bytes
     *     Byte array to unpack the string value from.
     * @param pos
     *     Offset to start unpacking the string value.
     * @param maxlen
     *     Maximum length of data to be searched for a null character.
     * @param uni
     *     String is Unicode if true, else ASCII
     * @return String, else null if the terminating null character was not found.
     */
    public static String getDataString(char typ, byte[] bytes, int pos, int maxlen, boolean uni) {

        //  Check if the data string has the required data type

        if (bytes[pos++] == (byte) typ) {
            //  Extract the null terminated string
            if (uni) {
                return getUnicodeString(bytes, wordAlign(pos), maxlen / 2);
            }
            return getString(bytes, pos, maxlen - 1);
        }

        // Invalid data type

        return null;
    }

    /**
     * Unpack a 32-bit integer.
     *
     * @param buf
     *     Byte buffer containing the integer to be unpacked.
     * @param pos
     *     Position within the buffer that the integer is stored.
     * @return The unpacked 32-bit integer value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static int getInt(byte[] buf, int pos) throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough

        if (buf.length < pos + 3) {
            throw new IndexOutOfBoundsException();
        }

        //  Unpack the 32-bit value
        int i1 = (int) buf[pos] & 0xFF;
        int i2 = (int) buf[pos + 1] & 0xFF;
        int i3 = (int) buf[pos + 2] & 0xFF;
        int i4 = (int) buf[pos + 3] & 0xFF;

        //  Return the unpacked value
        return (i1 << 24) + (i2 << 16) + (i3 << 8) + i4;

    }

    /**
     * Unpack a 32-bit integer that is stored in Intel format.
     *
     * @param bytes
     *     Byte array containing the Intel integer to be unpacked.
     * @param pos
     *     Offset that the Intel integer is stored within the byte array.
     * @return Unpacked integer value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static int getIntelInt(byte[] bytes, int pos) throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to restore the int

        if (bytes.length < pos + 3) {
            throw new IndexOutOfBoundsException();
        }

        //  Determine the byte ordering for this platform, and restore the int

        int iVal;

        //  Restore the int value from the byte array

        int i1 = (int) bytes[pos + 3] & 0xFF;
        int i2 = (int) bytes[pos + 2] & 0xFF;
        int i3 = (int) bytes[pos + 1] & 0xFF;
        int i4 = (int) bytes[pos] & 0xFF;

        iVal = (i1 << 24) + (i2 << 16) + (i3 << 8) + i4;

        //  Return the int value

        return iVal;
    }

    /**
     * Unpack a 64-bit long.
     *
     * @param buf
     *     Byte buffer containing the integer to be unpacked.
     * @param pos
     *     Position within the buffer that the integer is stored.
     * @return The unpacked 64-bit long value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static long getLong(byte[] buf, int pos)
        throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to restore the long

        if (buf.length < pos + 7) {
            throw new IndexOutOfBoundsException();
        }

        //  Restore the long value from the byte array

        long lVal = 0L;

        for (int i = 0; i < 8; i++) {

            //	Get the current byte, shift the value and add to the return value

            long curVal = (long) buf[pos + i] & 0xFF;
            curVal = curVal << ((7 - i) * 8);
            lVal += curVal;
        }

        //  Return the long value

        return lVal;
    }

    /**
     * Unpack a 64-bit integer that is stored in Intel format.
     *
     * @param bytes
     *     Byte array containing the Intel long to be unpacked.
     * @param pos
     *     Offset that the Intel integer is stored within the byte array.
     * @return Unpacked long value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static long getIntelLong(byte[] bytes, int pos) throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to restore the long

        if (bytes.length < pos + 7) {
            throw new IndexOutOfBoundsException();
        }

        //  Restore the long value from the byte array

        long lVal = 0L;

        for (int i = 0; i < 8; i++) {

            //	Get the current byte, shift the value and add to the return value

            long curVal = (long) bytes[pos + i] & 0xFF;
            curVal = curVal << (i * 8);
            lVal += curVal;
        }

        //  Return the long value

        return lVal;
    }

    /**
     * Unpack a 16-bit value that is stored in Intel format.
     *
     * @param bytes
     *     Byte array containing the short value to be unpacked.
     * @param pos
     *     Offset to start unpacking the short value.
     * @return Unpacked short value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static int getIntelShort(byte[] bytes, int pos)
        throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to restore the int

        if (bytes.length < pos) {
            throw new IndexOutOfBoundsException();
        }

        //  Restore the short value from the byte array

        int sVal = (((int) bytes[pos + 1] << 8) + ((int) bytes[pos] & 0xFF));

        //  Return the short value

        return sVal & 0xFFFF;
    }

    /**
     * Unpack a 16-bit value.
     *
     * @param bytes
     *     Byte array containing the short to be unpacked.
     * @param pos
     *     Offset within the byte array that the short is stored.
     * @return Unpacked short value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static int getShort(byte[] bytes, int pos) throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to restore the int

        if (bytes.length < pos) {
            throw new IndexOutOfBoundsException();
        }

        //  Determine the byte ordering for this platform, and restore the short

        int sVal;

        if (bigEndian) {
            //  Big endian
            sVal = ((((int) bytes[pos + 1]) << 8) + ((int) bytes[pos] & 0xFF));
        }
        else {
            //  Little endian
            sVal = ((((int) bytes[pos]) << 8) + ((int) bytes[pos + 1] & 0xFF));
        }

        //  Return the short value

        return sVal & 0xFFFF;
    }

    /**
     * Unpack a null terminated string from the data buffer.
     *
     * @param bytes
     *     Byte array to unpack the string value from.
     * @param pos
     *     Offset to start unpacking the string value.
     * @param maxlen
     *     Maximum length of data to be searched for a null character.
     * @return String, else null if the terminating null character was not found.
     */
    public static String getString(byte[] bytes, int pos, int maxlen) {

        //  Search for the trailing null

        int maxpos = pos + maxlen;
        int endpos = pos;

        while (bytes[endpos] != 0x00 && endpos < maxpos) {
            endpos++;
        }

        //  Check if we reached the end of the buffer

        if (endpos <= maxpos) {
            return new String(bytes, pos, endpos - pos);
        }
        return null;
    }

    /**
     * Unpack a null terminated string from the data buffer. The string may be ASCII or Unicode.
     *
     * @param bytes
     *     Byte array to unpack the string value from.
     * @param pos
     *     Offset to start unpacking the string value.
     * @param maxlen
     *     Maximum length of data to be searched for a null character.
     * @param isUni
     *     Unicode string if true, else ASCII string
     * @return String, else null if the terminating null character was not found.
     */
    public static String getString(byte[] bytes, int pos, int maxlen, boolean isUni) {

        //	Get a string from the buffer

        String str;

        if (isUni) {
            str = getUnicodeString(bytes, pos, maxlen);
        }
        else {
            str = getString(bytes, pos, maxlen);
        }

        //	return the string

        return str;
    }

    /**
     * Unpack a null terminated Unicode string from the data buffer.
     *
     * @param byt
     *     Byte array to unpack the string value from.
     * @param pos
     *     Offset to start unpacking the string value.
     * @param maxlen
     *     Maximum length of data to be searched for a null character.
     * @return String, else null if the terminating null character was not found.
     */
    public static String getUnicodeString(byte[] byt, int pos, int maxlen) {

        //	Check for an empty string

        if (maxlen == 0) {
            return "";
        }

        //  Search for the trailing null

        int maxpos = pos + (maxlen * 2);
        int endpos = pos;
        char[] chars = new char[maxlen];
        int cpos = 0;
        char curChar;

        do {

            //  Get a Unicode character from the buffer

            curChar = (char) (((byt[endpos + 1] & 0xFF) << 8) + (byt[endpos] & 0xFF));

            //  Add the character to the array

            chars[cpos++] = curChar;

            //  Update the buffer pointer

            endpos += 2;

        } while (curChar != 0 && endpos < maxpos);

        //  Check if we reached the end of the buffer

        if (endpos <= maxpos) {
            if (curChar == 0) {
                cpos--;
            }
            return new String(chars, 0, cpos);
        }
        return null;
    }

    /**
     * Pack a 32-bit integer into the supplied byte buffer.
     *
     * @param val
     *     Integer value to be packed.
     * @param bytes
     *     Byte buffer to pack the integer value into.
     * @param pos
     *     Offset to start packing the integer value.
     * @throws IndexOutOfBoundsException
     *     If the buffer does not have enough space.
     */
    public static void putInt(int val, byte[] bytes, int pos) throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to store the int

        if (bytes.length < pos + 3) {
            throw new IndexOutOfBoundsException();
        }

        //  Pack the integer value

        bytes[pos] = (byte) ((val >> 24) & 0xFF);
        bytes[pos + 1] = (byte) ((val >> 16) & 0xFF);
        bytes[pos + 2] = (byte) ((val >> 8) & 0xFF);
        bytes[pos + 3] = (byte) (val & 0xFF);
    }

    /**
     * Pack an 32-bit integer value in Intel format.
     *
     * @param val
     *     Integer value to be packed.
     * @param bytes
     *     Byte array to pack the value into.
     * @param pos
     *     Offset to start packing the integer value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static void putIntelInt(int val, byte[] bytes, int pos) throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to store the int

        if (bytes.length < pos + 3) {
            throw new IndexOutOfBoundsException();
        }

        //  Store the int value in the byte array

        bytes[pos + 3] = (byte) ((val >> 24) & 0xFF);
        bytes[pos + 2] = (byte) ((val >> 16) & 0xFF);
        bytes[pos + 1] = (byte) ((val >> 8) & 0xFF);
        bytes[pos] = (byte) (val & 0xFF);
    }

    /**
     * Pack a 64-bit integer value into the buffer
     *
     * @param val
     *     Integer value to be packed.
     * @param bytes
     *     Byte array to pack the value into.
     * @param pos
     *     Offset to start packing the integer value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static void putLong(long val, byte[] bytes, int pos)
        throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to store the int

        if (bytes.length < pos + 7) {
            throw new IndexOutOfBoundsException();
        }

        //  Store the long value in the byte array

        bytes[pos] = (byte) ((val >> 56) & 0xFF);
        bytes[pos + 1] = (byte) ((val >> 48) & 0xFF);
        bytes[pos + 2] = (byte) ((val >> 40) & 0xFF);
        bytes[pos + 3] = (byte) ((val >> 32) & 0xFF);
        bytes[pos + 4] = (byte) ((val >> 24) & 0xFF);
        bytes[pos + 5] = (byte) ((val >> 16) & 0xFF);
        bytes[pos + 6] = (byte) ((val >> 8) & 0xFF);
        bytes[pos + 7] = (byte) (val & 0xFF);
    }


    /**
     * Pack a 64-bit integer value in Intel format.
     *
     * @param val
     *     Integer value to be packed.
     * @param bytes
     *     Byte array to pack the value into.
     * @param pos
     *     Offset to start packing the integer value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static void putIntelLong(long val, byte[] bytes, int pos)
        throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to store the int

        if (bytes.length < pos + 7) {
            throw new IndexOutOfBoundsException();
        }

        //  Store the long value in the byte array

        bytes[pos + 7] = (byte) ((val >> 56) & 0xFF);
        bytes[pos + 6] = (byte) ((val >> 48) & 0xFF);
        bytes[pos + 5] = (byte) ((val >> 40) & 0xFF);
        bytes[pos + 4] = (byte) ((val >> 32) & 0xFF);
        bytes[pos + 3] = (byte) ((val >> 24) & 0xFF);
        bytes[pos + 2] = (byte) ((val >> 16) & 0xFF);
        bytes[pos + 1] = (byte) ((val >> 8) & 0xFF);
        bytes[pos] = (byte) (val & 0xFF);
    }

    /**
     * Pack a 64-bit integer value in Intel format.
     *
     * @param val
     *     Integer value to be packed.
     * @param bytes
     *     Byte array to pack the value into.
     * @param pos
     *     Offset to start packing the integer value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static void putIntelLong(int val, byte[] bytes, int pos)
        throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to store the int

        if (bytes.length < pos + 7) {
            throw new IndexOutOfBoundsException();
        }

        //  Store the int value in the byte array

        bytes[pos + 7] = (byte) 0;
        bytes[pos + 6] = (byte) 0;
        bytes[pos + 5] = (byte) 0;
        bytes[pos + 4] = (byte) 0;
        bytes[pos + 3] = (byte) ((val >> 24) & 0xFF);
        bytes[pos + 2] = (byte) ((val >> 16) & 0xFF);
        bytes[pos + 1] = (byte) ((val >> 8) & 0xFF);
        bytes[pos] = (byte) (val & 0xFF);
    }

    /**
     * Pack a 16 bit value in Intel byte order.
     *
     * @param val
     *     Short value to be packed.
     * @param bytes
     *     Byte array to pack the short value into.
     * @param pos
     *     Offset to start packing the short value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static void putIntelShort(int val, byte[] bytes, int pos)
        throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to store the short

        if (bytes.length < pos) {
            throw new IndexOutOfBoundsException();
        }

        // 	Pack the short value

        bytes[pos + 1] = (byte) ((val >> 8) & 0xFF);
        bytes[pos] = (byte) (val & 0xFF);
    }

    /**
     * Pack a 16-bit value into the supplied byte buffer.
     *
     * @param val
     *     Short value to be packed.
     * @param bytes
     *     Byte array to pack the short value into.
     * @param pos
     *     Offset to start packing the short value.
     * @throws IndexOutOfBoundsException
     *     If there is not enough data in the buffer.
     */
    public static void putShort(int val, byte[] bytes, int pos) throws IndexOutOfBoundsException {

        //  Check if the byte array is long enough to store the short

        if (bytes.length < pos) {
            throw new IndexOutOfBoundsException();
        }

        //  Pack the short value

        bytes[pos] = (byte) ((val >> 8) & 0xFF);
        bytes[pos + 1] = (byte) (val & 0xFF);
    }

    /**
     * Pack a string into a data buffer
     *
     * @param str
     *     String to be packed into the buffer
     * @param bytes
     *     Byte array to pack the string into
     * @param pos
     *     Position to start packing the string
     * @param nullTerminate
     *     true if the string should be null terminated, else false
     * @return The ending buffer position
     */
    public static int putString(String str, byte[] bytes, int pos, boolean nullTerminate) {

        // Pack the data bytes
        int bufpos = pos;

        // Get the string as a byte array
        for (byte b : str.getBytes()) {
            bytes[bufpos++] = b;
        }

        // Null terminate the string, if required

        if (nullTerminate) {
            bytes[bufpos++] = 0;
        }

        // Return the next free buffer position

        return bufpos;
    }

    /**
     * Pack a string into a data buffer
     *
     * @param str
     *     String to be packed into the buffer
     * @param fldLen
     *     Field length, will be space padded if short
     * @param bytes
     *     Byte array to pack the string into
     * @param pos
     *     Position to start packing the string
     * @return The ending buffer position
     */
    public final static int putString(String str, int fldLen, byte[] bytes, int pos) {

        //  Get the string as a byte array

        byte[] byts = str.getBytes();

        //  Pack the data bytes

        int bufpos = pos;
        int idx = 0;

        while (idx < fldLen) {
            if (idx < byts.length) {
                bytes[bufpos++] = byts[idx];
            }
            else {
                bytes[bufpos++] = (byte) 0;
            }
            idx++;
        }

        //  Return the next free buffer position

        return bufpos;
    }

    /**
     * Pack a string into a data buffer. The string may be ASCII or Unicode.
     *
     * @param str
     *     String to be packed into the buffer
     * @param bytes
     *     Byte array to pack the string into
     * @param pos
     *     Position to start packing the string
     * @param nullterm
     *     true if the string should be null terminated, else false
     * @param isUni
     *     true if the string should be packed as Unicode, false to pack as ASCII
     * @return The ending buffer position
     */
    public final static int putString(String str, byte[] bytes, int pos, boolean nullterm, boolean isUni) {

        //	Pack the string

        int newpos;

        if (isUni) {
            newpos = putUnicodeString(str, bytes, pos, nullterm);
        }
        else {
            newpos = putString(str, bytes, pos, nullterm);
        }

        //	Return the end of string buffer position

        return newpos;
    }

    /**
     * Pack a Unicode string into a data buffer
     *
     * @param str
     *     String to be packed into the buffer
     * @param bytes
     *     Byte array to pack the string into
     * @param pos
     *     Position to start packing the string
     * @param nullterm
     *     true if the string should be null terminated, else false
     * @return The ending buffer position
     */
    public final static int putUnicodeString(String str, byte[] bytes, int pos, boolean nullterm) {

        //  Pack the data bytes

        int bufpos = pos;

        for (int i = 0; i < str.length(); i++) {

            //	Get the current character from the string

            char ch = str.charAt(i);

            //	Pack the unicode character

            bytes[bufpos++] = (byte) (ch & 0xFF);
            bytes[bufpos++] = (byte) ((ch & 0xFF00) >> 8);
        }

        //  Null terminate the string, if required

        if (nullterm) {
            bytes[bufpos++] = 0;
            bytes[bufpos++] = 0;
        }

        //  Return the next free buffer position

        return bufpos;
    }

    /**
     * Pack nulls into the buffer.
     *
     * @param buf
     *     Buffer to pack data into.
     * @param pos
     *     Position to start packing.
     * @param cnt
     *     Number of nulls to pack.
     * @throws ArrayIndexOutOfBoundsException
     *     If the buffer does not have enough space.
     */
    public final static void putZeros(byte[] buf, int pos, int cnt) throws ArrayIndexOutOfBoundsException {

        //  Check if the buffer is big enough

        if (buf.length < (pos + cnt)) {
            throw new ArrayIndexOutOfBoundsException();
        }

        //  Pack the nulls

        for (int i = 0; i < cnt; i++) {
            buf[pos + i] = 0;
        }
    }

    /**
     * Align a buffer offset on a word boundary
     *
     * @param pos
     *     int
     * @return int
     */
    public final static int wordAlign(int pos) {
        return (pos + 1) & 0xFFFFFFFE;
    }

    /**
     * Align a buffer offset on a longword boundary
     *
     * @param pos
     *     int
     * @return int
     */
    public final static int longwordAlign(int pos) {
        return (pos + 3) & 0xFFFFFFFC;
    }

    /**
     * Calculate the string length in bytes
     *
     * @param str
     *     String
     * @param uni
     *     boolean
     * @param nul
     *     boolean
     * @return int
     */
    public final static int getStringLength(String str, boolean uni, boolean nul) {

        //	Calculate the string length in bytes

        int len = str.length();
        if (nul) {
            len += 1;
        }
        if (uni) {
            len *= 2;
        }

        return len;
    }

    /**
     * Calculate the new buffer position after the specified string and encoding (ASCII or Unicode)
     *
     * @param pos
     *     int
     * @param str
     *     String
     * @param uni
     *     boolean
     * @param nul
     *     boolean
     * @return int
     */
    public final static int getBufferPosition(int pos, String str, boolean uni, boolean nul) {

        //	Calculate the new buffer position

        int len = str.length();
        if (nul) {
            len += 1;
        }
        if (uni) {
            len *= 2;
        }

        return pos + len;
    }
}
