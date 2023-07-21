
public static class DefaultTrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        // Keine Aktion erforderlich - Client wird nicht überprüft.
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        try {
            // Lade das Wurzelzertifikat von Let's Encrypt oder einer vertrauenswürdigen Zertifizierungsstelle
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream is = new BufferedInputStream(new FileInputStream("path_to_letsencrypt_root_cert.pem"));
            X509Certificate caCert = (X509Certificate) certificateFactory.generateCertificate(is);
            is.close();

            // Erstellen Sie eine Zertifikatskette mit dem Serverzertifikat und dem Wurzelzertifikat von Let's Encrypt
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("caCert", caCert);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Überprüfen Sie die Zertifikatskette
            X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            defaultTrustManager.checkServerTrusted(certs, authType);
        } catch (IOException | GeneralSecurityException e) {
            throw new CertificateException("Failed to verify server certificate: " + e.getMessage());
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}