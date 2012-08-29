package com.c_platform.parsebody.parsebody.util;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {
	public static String guessURL(String spec) throws MalformedURLException {
		return guessURL(null, spec);
	}

	public static String guessURL(URL baseURL, String spec)
			throws MalformedURLException {
		URL finalURL;
		try {
			if (baseURL != null) {
				int colonIdx = spec.indexOf(':');// /market/product/201103258787.html
				String newProtocol = colonIdx == -1 ? null : spec.substring(0,
						colonIdx);
				// if (newProtocol != null &&
				// !newProtocol.equalsIgnoreCase(baseURL.getProtocol()))
				// {
				// baseURL = null;
				// }
				if (newProtocol != null) {
					return spec;
				}
			}
			finalURL = createURL(baseURL, spec);
		} catch (MalformedURLException mfu) {
			spec = spec.trim();
			int idx = spec.indexOf(':');
			if (idx == -1) {
				int slashIdx = spec.indexOf('/');
				if (slashIdx == 0) {
					// A file, absolute
					finalURL = new URL("file:" + spec);
				} else {
					if (slashIdx == -1) {
						// No slash, no colon, must be host.
						finalURL = new URL(baseURL, "http://" + spec);
					} else {
						String possibleHost = spec.substring(0, slashIdx)
								.toLowerCase();
						if (Domains.isLikelyHostName(possibleHost)) {
							finalURL = new URL(baseURL, "http://" + spec);
						} else {
							finalURL = new URL(baseURL, "file:" + spec);
						}
					}
				}
			} else {
				if (idx == 1) {
					// Likely a drive
					finalURL = new URL(baseURL, "file:" + spec);
				} else {
					throw mfu;
				}
			}
		}
		// if (!"".equals(finalURL.getHost())
		// && finalURL.toExternalForm().indexOf(' ') != -1)
		// {
		// throw new MalformedURLException("There are blanks in the URL: "
		// + finalURL.toExternalForm());
		// }
		return finalURL.toString();
	}

	/**
	 * Creates an absolute URL in a manner equivalent to major browsers.
	 */
	public static URL createURL(URL baseUrl, String relativeUrl)
			throws java.net.MalformedURLException {
		return new URL(baseUrl, relativeUrl);
	}
}
