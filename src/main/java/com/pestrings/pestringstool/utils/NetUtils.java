package com.pestrings.pestringstool.utils;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class NetUtils {

    public static final BitSet MAIN_CHARS;
    public static final BitSet QUERY_FRAGMENT_CHARS;

    static {
        BitSet alphaDigit = bitSet(bitSet('A', 'Z'), bitSet('a', 'z'), bitSet('0', '9'));
        BitSet unreserved = bitSet(alphaDigit, bitSet("-._~"));
        BitSet genDelims = bitSet(":/?#[]@");
        BitSet subDelims = bitSet("!$&'()*+,;=");
        MAIN_CHARS = bitSet(unreserved, genDelims, subDelims);
        BitSet pchar = bitSet(unreserved, subDelims, bitSet(":@"));
        QUERY_FRAGMENT_CHARS = bitSet(pchar, bitSet("/?"));
    }

    private static BitSet bitSet(String s) {
        BitSet result = new BitSet();
        for (int i = 0; i < s.length(); i++) {
            result.set(s.charAt(i));
        }
        return result;
    }

    private static BitSet bitSet(char from, char to) {
        BitSet result = new BitSet();
        result.set(from, to + 1);
        return result;
    }

    private static BitSet bitSet(BitSet... sets) {
        BitSet result = new BitSet();
        for (BitSet set : sets) {
            result.or(set);
        }
        return result;
    }

    static public String encodeURL(String url) {
        BitSet nonEncodingChars = MAIN_CHARS;
        StringBuilder sb = new StringBuilder();
        for (byte c : url.getBytes(StandardCharsets.UTF_8)) {
            if (c == '?' || c == '#') {
                sb.append((char) c);
                nonEncodingChars = QUERY_FRAGMENT_CHARS;
            } else if (nonEncodingChars.get(c & 0xFF)) {
                sb.append((char) c);
            } else {
                sb.append(String.format("%%%02X", c & 0xFF));
            }
        }
        return sb.toString();
    }

}
