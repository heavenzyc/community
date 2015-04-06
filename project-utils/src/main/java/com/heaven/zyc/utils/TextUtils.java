/**
 * @(#)TextUtils.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author jianguo.xu
 * @date 2012-11-11
 */
public class TextUtils {

	public TextUtils() {
	}

	public static final String htmlEncode(String s) {
		return htmlEncode(s, true);
	}

	public static final String htmlEncode(String s, boolean encodeSpecialChars) {
		s = noNull(s);
		StringBuilder str = new StringBuilder();
		for (int j = 0; j < s.length(); j++) {
			char c = s.charAt(j);
			if (c < '\200') {
				switch (c) {
				case 34: // '"'
					str.append("&quot;");
					break;

				case 38: // '&'
					str.append("&amp;");
					break;

				case 60: // '<'
					str.append("&lt;");
					break;

				case 62: // '>'
					str.append("&gt;");
					break;

				default:
					str.append(c);
					break;
				}
				continue;
			}
			if (encodeSpecialChars && c < '\377') {
				String hexChars = "0123456789ABCDEF";
				int a = c % 16;
				int b = (c - a) / 16;
				String hex = (new StringBuilder()).append("")
						.append(hexChars.charAt(b)).append(hexChars.charAt(a))
						.toString();
				str.append((new StringBuilder()).append("&#x").append(hex)
						.append(";").toString());
			} else {
				str.append(c);
			}
		}

		return str.toString();
	}

	public static final String escapeJavaScript(String s) {
		s = noNull(s);
		StringBuffer str = new StringBuffer();
		for (int j = 0; j < s.length(); j++) {
			char c = s.charAt(j);
			switch (c) {
			case 9: // '\t'
				str.append("\\t");
				break;

			case 8: // '\b'
				str.append("\\b");
				break;

			case 10: // '\n'
				str.append("\\n");
				break;

			case 12: // '\f'
				str.append("\\f");
				break;

			case 13: // '\r'
				str.append("\\r");
				break;

			case 92: // '\\'
				str.append("\\\\");
				break;

			case 34: // '"'
				str.append("\\\"");
				break;

			case 39: // '\''
				str.append("\\'");
				break;

			case 47: // '/'
				str.append("\\/");
				break;

			default:
				str.append(c);
				break;
			}
		}

		return str.toString();
	}

	public static final String join(String glue, Iterator<?> pieces) {
		StringBuilder s = new StringBuilder();
		do {
			if (!pieces.hasNext())
				break;
			s.append(pieces.next().toString());
			if (pieces.hasNext())
				s.append(glue);
		} while (true);
		return s.toString();
	}

	public static final String join(String glue, String pieces[]) {
		return join(glue, Arrays.asList(pieces).iterator());
	}

	public static final String join(String glue, Collection<?> pieces) {
		return join(glue, pieces.iterator());
	}

	public static final String noNull(String string, String defaultString) {
		return stringSet(string) ? string : defaultString;
	}

	public static final String noNull(String string) {
		return noNull(string, "");
	}

	public static final boolean stringSet(String string) {
		return string != null && !"".equals(string);
	}

	public static final boolean verifyUrl(String url) {
		if (url == null)
			return false;
		if (url.startsWith("https://"))
			url = (new StringBuilder()).append("http://")
					.append(url.substring(8)).toString();
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}
