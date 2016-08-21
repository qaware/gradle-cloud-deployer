package de.qaware.cloud.deployer.kubernetes.resource.base;

import de.qaware.cloud.deployer.kubernetes.config.cloud.ClusterConfig;
import de.qaware.cloud.deployer.kubernetes.config.cloud.SSLConfig;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.ByteString;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class ClientFactory {

    private final Retrofit retrofit;

    public ClientFactory(ClusterConfig clusterConfig) {
        this.retrofit = createRetrofit(clusterConfig);
    }

    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    private Retrofit createRetrofit(ClusterConfig clusterConfig) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(chain -> {
            String credentials = Credentials.basic(clusterConfig.getUsername(), clusterConfig.getPassword());
            Request original = chain.request();
            Request request = original.newBuilder()
                    .addHeader("Authorization", credentials)
                    .build();
            return chain.proceed(request);
        });

        try {
            SSLConfig sslConfig = clusterConfig.getSslConfig();
            if (sslConfig.isTrustAll()) {
                builder = addTrustAllTrustManager(builder);
            } else if (sslConfig.hasCertificate()) {
                builder = addTrustCertTrustManager(builder, sslConfig.getCertificate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClient client = builder.build();

        return new Retrofit.Builder()
                .baseUrl(clusterConfig.getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
    }

    private OkHttpClient.Builder addTrustAllTrustManager(OkHttpClient.Builder builder) throws NoSuchAlgorithmException, KeyManagementException {
        final TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, new java.security.SecureRandom());

        X509TrustManager reqLogger = (X509TrustManager) trustManagers[0];

        builder.sslSocketFactory(sslContext.getSocketFactory(), reqLogger);
        builder.hostnameVerifier((hostname, sslSession) -> true);

        return builder;
    }

    private OkHttpClient.Builder addTrustCertTrustManager(OkHttpClient.Builder builder, String certData) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, KeyManagementException {

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        CertificateFactory certFactory = CertificateFactory.getInstance("X509");

        InputStream certInputStream = createCertInputStream(certData);
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certInputStream);
        String alias = cert.getSubjectX500Principal().getName();

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null);
        trustStore.setCertificateEntry(alias, cert);
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, new SecureRandom());

        X509TrustManager reqLogger = (X509TrustManager) trustManagers[0];

        builder.sslSocketFactory(sslContext.getSocketFactory(), reqLogger);

        return builder;
    }

    private InputStream createCertInputStream(String certData) {
        ByteString decoded = ByteString.decodeBase64(certData);
        byte[] bytes1;
        if (decoded != null) {
            bytes1 = decoded.toByteArray();
        } else {
            bytes1 = certData.getBytes();
        }
        return new ByteArrayInputStream(bytes1);
    }
}
