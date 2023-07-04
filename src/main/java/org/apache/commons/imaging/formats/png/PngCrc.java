/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.imaging.formats.png;

// should just use ints, not longs
class PngCrc {

    /* Table of CRCs of all 8-bit messages. */
    private final long[] crcTable = new long[256];

    /* Flag: has the table been computed? Initially false. */
    private boolean crcTableComputed;

    public final long continuePartialCrc(final long old_crc, final byte[] buf, final int len) {
        return updateCrc(old_crc, buf);
    }

    /*
     * Update a running CRC with the bytes buf[0..len-1]--the CRC should be
     * initialized to all 1's, and the transmitted value is the 1's complement
     * of the final running CRC (see the crc() routine below)).
     */

    /* Return the CRC of the bytes buf[0..len-1]. */
    public final int crc(final byte[] buf, final int len) {
        return (int) (updateCrc(0xffffffffL, buf) ^ 0xffffffffL);
    }

    public final long finishPartialCrc(final long old_crc) {
        return (old_crc ^ 0xffffffffL);
    }

    /* Make the table for a fast CRC. */
    private void makeCrcTable() {
        long c;
        int n;
        int k;

        for (n = 0; n < 256; n++) {
            c = n;
            for (k = 0; k < 8; k++) {
                if ((c & 1) != 0) {
                    c = 0xedb88320L ^ (c >> 1);
                } else {
                    c = c >> 1;
                }
            }
            crcTable[n] = c;
        }
        crcTableComputed = true;
    }

    public final long startPartialCrc(final byte[] buf, final int len) {
        return updateCrc(0xffffffffL, buf);
    }

    private long updateCrc(final long crc, final byte[] buf) {
        long c = crc;
        int n;

        if (!crcTableComputed) {
            makeCrcTable();
        }
        for (n = 0; n < buf.length; n++) {
            // Debug.debug("crc[" + n + "]", c + " (" + Long.toHexString(c) +
            // ")");

            c = crcTable[(int) ((c ^ buf[n]) & 0xff)] ^ (c >> 8);
        }
        return c;
    }
}
