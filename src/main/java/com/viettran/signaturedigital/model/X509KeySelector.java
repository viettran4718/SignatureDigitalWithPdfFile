package com.viettran.signaturedigital.model;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;

public class X509KeySelector extends KeySelector {
	@Override
	@SuppressWarnings("rawtypes")
	public KeySelectorResult select(final KeyInfo keyInfo, final KeySelector.Purpose purpose,
			final AlgorithmMethod method, final XMLCryptoContext context) throws KeySelectorException {
		final Iterator ki = keyInfo.getContent().iterator();
		while (ki.hasNext()) {
			final XMLStructure info = (XMLStructure) ki.next();
			if (!(info instanceof X509Data)) {
				continue;
			}
			final X509Data x509Data = (X509Data) info;
			final Iterator xi = x509Data.getContent().iterator();
			while (xi.hasNext()) {
				final Object o = xi.next();
				if (!(o instanceof X509Certificate)) {
					continue;
				}
				final PublicKey key = ((X509Certificate) o).getPublicKey();
				// Make sure the algorithm is compatible
				// with the method.
				if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
					return new KeySelectorResult() {
						@Override
						public Key getKey() {
							return key;
						}
					};
				}
			}
		}
		throw new KeySelectorException("No key found!");
	}

	static boolean algEquals(final String algURI, final String algName) {
		if ((algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1))
				|| (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1))) {
			return true;
		} else {
			return false;
		}
	}
}