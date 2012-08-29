package com.c_platform.parsebody.parsebody.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author J. H. S.
 */
public class Domains {
	private static final Collection gTLDs;

	static {
		gTLDs = new HashSet();
		gTLDs.add(".com");
		gTLDs.add(".edu");
		gTLDs.add(".gov");
		gTLDs.add(".int");
		gTLDs.add(".mil");
		gTLDs.add(".net");
		gTLDs.add(".org");
		gTLDs.add(".biz");
		gTLDs.add(".info");
		gTLDs.add(".name");
		gTLDs.add(".pro");
		gTLDs.add(".aero");
		gTLDs.add(".coop");
		gTLDs.add(".museum");
		// TODO: New gTLDs?
	}

	/**
     * 
     */
	private Domains() {
		super();
	}

	public static boolean isValidCookieDomain(String domain, String hostName) {
		String plainDomain;
		if (!domain.startsWith(".")) {
			// Valid domains must start with a dot
			// according to RFC 2109, but
			// RFC 2965 specifies a dot is prepended
			// in the Set-Cookie2 header.
			plainDomain = domain;
			domain = "." + domain;
		} else {
			plainDomain = domain.substring(1);
		}
		String plainDomainTL = plainDomain.toLowerCase();
		String hostNameTL = hostName.toLowerCase();
		if (!hostNameTL.endsWith(plainDomainTL)) {
			return false;
		}
		int lastDotIdx = domain.lastIndexOf('.');
		if (lastDotIdx == -1) {
			return false;
		}
		String suffix = domain.substring(lastDotIdx).toLowerCase();
		if (gTLDs.contains(suffix)) {
			return Strings.countChars(domain, '.') >= 2;
		} else {
			return Strings.countChars(domain, '.') >= 3;
		}
	}

	/**
	 * 
	 * @param host
	 *            A host name in lower case.
	 * @return
	 */
	public static boolean endsWithGTLD(String host) {
		Iterator i = gTLDs.iterator();
		while (i.hasNext()) {
			String ending = (String) i.next();
			if (host.endsWith(ending)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLikelyHostName(String name) {
		String nameTL = name.toLowerCase();
		if (nameTL.startsWith("www.")) {
			return true;
		}
		if (endsWithGTLD(name)) {
			return true;
		}
		int lastDotIdx = nameTL.lastIndexOf('.');
		if (lastDotIdx == -1) {
			return false;
		}
		// Check for country code.
		return lastDotIdx == nameTL.length() - 3;
	}

	public static Collection getPossibleDomains(String hostName) {
		Collection domains = new LinkedList();
		domains.add(hostName);
		int dotIdx = hostName.indexOf('.', 1);
		if (dotIdx == -1) {
			return domains;
		}
		String testDomain = hostName.substring(dotIdx);
		if (!Domains.isValidCookieDomain(testDomain, hostName)) {
			return domains;
		}
		domains.addAll(Domains.getPossibleDomains(testDomain.substring(1)));
		return domains;
	}
}