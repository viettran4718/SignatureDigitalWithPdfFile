package com.viettran.signaturedigital.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;

public class SignDocument {

	private KeyStore keyStore;
	private X509Certificate certificate;

	private List<String> listAlias;

	PrivateKey privateKey;

	public KeyStore getKeyStore() {
		return this.keyStore;
	}

	public X509Certificate getCertificate() {
		return this.certificate;
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public List<String> getListAlias() {
		return this.listAlias;
	}

	// Contructor tÃ¬m thiáº¿t bá»‹ cÃ³ chá»©a chá»¯ kÃ½ sá»‘
	public SignDocument() {
		super();

		this.listAlias = new LinkedList<String>();
		try {
			this.keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
			this.keyStore.load(null);
			final Enumeration<String> enumeration = this.keyStore.aliases();
			while (enumeration.hasMoreElements()) {
				final String string = enumeration.nextElement();
				this.listAlias.add(string);
				// final Certificate ksCertificate =
				// this.keyStore.getCertificate(string);
				// this.certificate = (X509Certificate) ksCertificate;
				// this.privateKey = (PrivateKey)
				// this.keyStore.getKey(string, null);
			}
		} catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException
				| IOException e) {
			JOptionPane.showMessageDialog(null, "Không tìm thấy công cụ chứa chữ ký số", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}

	}

	public void setupCertificate(final String alias) {

		try {
			final Certificate ksCertificate = this.keyStore.getCertificate(alias);
			this.certificate = (X509Certificate) ksCertificate;
			this.privateKey = (PrivateKey) this.keyStore.getKey(alias, null);
		} catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
			JOptionPane.showMessageDialog(null, "Không tìm thấy công cụ chứa chữ ký số", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void signXml(final String xmlFileInput, final String xmlFileOutput)
			throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

		if (this.certificate == null) {
			JOptionPane.showMessageDialog(null, "Không tìm thấy usb chứa chữ ký số", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Create a DOM XMLSignatureFactory that will be used to
		// generate the enveloped signature.
		final XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		// Create a Reference to the enveloped document (in this case,
		// you are signing the whole document, so a URI of "" signifies
		// that, and also specify the SHA1 digest algorithm and
		// the ENVELOPED Transform.
		final Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
				Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null,
				null);

		// Create the SignedInfo.
		final SignedInfo si = fac.newSignedInfo(
				fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
				fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

		final KeyInfoFactory kif = fac.getKeyInfoFactory();
		final List x509Content = new ArrayList();
		x509Content.add(this.certificate.getSubjectX500Principal().getName());
		x509Content.add(this.certificate);
		final X509Data xd = kif.newX509Data(x509Content);
		final KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

		// Instantiate the document to be signed.
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = null;
		try {
			doc = dbf.newDocumentBuilder().parse(new FileInputStream(xmlFileInput));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			JOptionPane.showMessageDialog(null, "Không tìm thấy hoặc file xml không hợp lệ !", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Create a DOMSignContext and specify the RSA PrivateKey and
		// location of the resulting XMLSignature's parent element.
		final DOMSignContext dsc = new DOMSignContext(this.privateKey, doc.getDocumentElement());

		// Create the XMLSignature, but don't sign it yet.
		final XMLSignature signature = fac.newXMLSignature(si, ki);

		// Marshal, generate, and sign the enveloped signature.
		try {
			signature.sign(dsc);
		} catch (MarshalException | XMLSignatureException e) {
			JOptionPane.showMessageDialog(null, "Có lỗi trong quá trình ký", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Output the resulting document.
		OutputStream os;

		final TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans;
		try {
			os = new FileOutputStream(xmlFileOutput);
			trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));
		} catch (FileNotFoundException | TransformerException e) {
			JOptionPane.showMessageDialog(null, "Có lỗi trong quá trình ghi file", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public void signPdf(final String alias, final String original, final String destination, final String reason,
			final String location) {
		PdfReader reader;
		try {
			reader = new PdfReader(original);
		} catch (final IOException e) {
			JOptionPane.showMessageDialog(null, "File pdf muốn ký không hợp lệ", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		File file = new File(destination);

		// If have exist file, ask user replace
		if (file.exists()) {
			int i = JOptionPane.showConfirmDialog(null,
					"File có tên " + file.getName() + " đã tồn tại, bạn muốn thay thế?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION) {
				FileOutputStream os;
				try {
					os = new FileOutputStream(destination);
				} catch (final FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "File pdf đã tồn tại hoặc không hợp lệ", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				PdfStamper stamper;
				try {
					stamper = PdfStamper.createSignature(reader, os, '\0');
					final PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
					appearance.setCrypto(this.getPrivateKey(), this.keyStore.getCertificateChain(alias), null,
							PdfSignatureAppearance.WINCER_SIGNED);

					appearance.setReason(reason);
					appearance.setLocation(location);
					// appearance.setVisibleSignature(new Rectangle(72, 732, 144, 780), 1, "first");
					stamper.close();
				} catch (DocumentException | KeyStoreException | IOException e) {
					JOptionPane.showMessageDialog(null, "Có lỗi trong quá trình ký", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				JOptionPane.showMessageDialog(null, "Ký file pfd thành công", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

		// If dont have exist file, sign

		FileOutputStream os;
		try {
			os = new FileOutputStream(destination);
		} catch (final FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File pdf đã tồn tại hoặc không hợp lệ", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		PdfStamper stamper;
		try {
			stamper = PdfStamper.createSignature(reader, os, '\0');
			final PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
			appearance.setCrypto(this.getPrivateKey(), this.keyStore.getCertificateChain(alias), null,
					PdfSignatureAppearance.WINCER_SIGNED);

			appearance.setReason(reason);
			appearance.setLocation(location);
			// appearance.setVisibleSignature(new Rectangle(72, 732, 144, 780), 1, "first");
			stamper.close();
		} catch (DocumentException | KeyStoreException | IOException e) {
			JOptionPane.showMessageDialog(null, "Có lỗi trong quá trình ký", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(null, "Ký file pfd thành công", "Success", JOptionPane.INFORMATION_MESSAGE);

	}

	@SuppressWarnings("rawtypes")
	public boolean verifyXml(final String fileInput) {
		final XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		Document doc = null;
		try {
			doc = dbf.newDocumentBuilder().parse(new FileInputStream(fileInput));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			JOptionPane.showMessageDialog(null, "File không hợp lệ", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		final NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) {
			JOptionPane.showMessageDialog(null, "Không tìm thấy chữ ký", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		// Create a DOMValidateContext and specify a KeySelector
		// and document context.
		final DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), nl.item(0));

		// Unmarshal the XMLSignature.
		XMLSignature signature = null;
		try {
			signature = fac.unmarshalXMLSignature(valContext);
		} catch (final MarshalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Validate the XMLSignature.
		boolean coreValidity = false;
		try {
			coreValidity = signature.validate(valContext);
		} catch (final XMLSignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Check core validation status.
		if (coreValidity == false) {
			JOptionPane.showMessageDialog(null, "Văn bản không được xác thực");
			try {
				final boolean sv = signature.getSignatureValue().validate(valContext);
				System.out.println("signature validation status: " + sv);
				if (sv == false) {
					// Check the validation status of each Reference.
					final Iterator i = signature.getSignedInfo().getReferences().iterator();
					for (int j = 0; i.hasNext(); j++) {

						try {
							final boolean refValid = ((Reference) i.next()).validate(valContext);
							System.out.println("ref[" + j + "] validity status: " + refValid);
						} catch (final XMLSignatureException e) {
						}
					}
				}
			} catch (final XMLSignatureException e) {
			}
			return false;
		} else {
			JOptionPane.showMessageDialog(null, "Văn bản được xác thực", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
	}

}
