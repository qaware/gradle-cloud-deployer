/*
 * Copyright 2016 QAware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.qaware.cloud.deployer.commons.resource;

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
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

/**
 * A factory which creates retrofit clients. Those clients are initialized using the specified cloud config.
 */
public class ClientFactory {

    /**
     * Specifies the algorithm which is used for trust manager creation.
     */
    private static final String SSL_ALGORITHM = "SSL";

    /**
     * The factory which is used for certificate creation.
     */
    private static final String CERTIFICATE_FACTORY_TYPE = "X509";

    /**
     * The type of the keystore used for custom certificate storage.
     */
    private static final String KEYSTORE_TYPE = "JKS";

    /**
     * The retrofit instance which is used to build the clients.
     */
    private final Retrofit retrofit;

    /**
     * Creates a new ClientFactory which creates clients. Those are initialized with the specified cloud config.
     *
     * @param cloudConfig The config which is used for the clients.
     * @throws ResourceException If an error occurs.
     */
    public ClientFactory(CloudConfig cloudConfig) throws ResourceException {
        this.retrofit = createRetrofit(cloudConfig);
    }

    /**
     * Creates a new client using the factory's config.
     *
     * @param serviceClass The client's class.
     * @param <T>          The client's class
     * @return A new client instance of the specified class initialized with the factory's config.
     */
    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    /**
     * Creates the retrofit instance using the specified cloud config. It adds headers and ssl.
     *
     * @param cloudConfig The config which specifies the headers and ssl options.
     * @return The retrofit instance.
     * @throws ResourceException If an error occurs during ssl configuration.
     */
    private Retrofit createRetrofit(CloudConfig cloudConfig) throws ResourceException {

        // Check if url is specified.
        if (cloudConfig.getBaseUrl() == null || cloudConfig.getBaseUrl().isEmpty()) {
            throw new ResourceException("Can't create a client without a url");
        }

        // Create a client builder.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // Add credentials header if credentials are specified.
        builder = addCredentials(cloudConfig, builder);

        // Add token header if token is specified.
        builder = addToken(cloudConfig, builder);

        // Add ssl config if specified.
        builder = addSSLConfig(cloudConfig, builder);

        // Build the client.
        OkHttpClient client = builder.build();

        return new Retrofit.Builder()
                .baseUrl(cloudConfig.getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
    }

    /**
     * Adds ssl configuration as specified in the config to the builder, if existing.
     *
     * @param cloudConfig The config which specifies how to configure ssl.
     * @param builder     The builder which will be configured.
     * @return The configured builder or the original builder if no ssl config is specified in the config.
     * @throws ResourceException If an error during trust manager creation occurs.
     */
    private OkHttpClient.Builder addSSLConfig(CloudConfig cloudConfig, OkHttpClient.Builder builder) throws ResourceException {
        SSLConfig sslConfig = cloudConfig.getSslConfig();
        if (sslConfig != null) {
            try {
                if (sslConfig.isTrustAll()) {
                    builder = addTrustAllTrustManager(builder);
                } else if (sslConfig.hasCertificate()) {
                    builder = addTrustCertTrustManager(builder, sslConfig.getCertificate());
                }
            } catch (Exception e) {
                throw new ResourceException(e);
            }
        }
        return builder;
    }

    /**
     * Adds a credentials header using the credentials in the config to the builder, if existing.
     *
     * @param cloudConfig The config which specifies the credentials.
     * @param builder     The builder which will be configured.
     * @return The configured builder or the original builder if no credentials are specified in the config.
     */
    private OkHttpClient.Builder addCredentials(CloudConfig cloudConfig, OkHttpClient.Builder builder) {
        String username = cloudConfig.getUsername();
        String password = cloudConfig.getPassword();
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            builder.addInterceptor(chain -> {
                String credentials = Credentials.basic(username, password);
                Request original = chain.request();
                Request request = original.newBuilder()
                        .addHeader("Authorization", credentials)
                        .build();
                return chain.proceed(request);
            });
        }
        return builder;
    }

    /**
     * Adds a token header using the token specified in the config to the builder, if existing.
     *
     * @param cloudConfig The config which specifies the token.
     * @param builder     The builder which will be configured.
     * @return The configured builder or the original builder if no token is specified in the config.
     */
    private OkHttpClient.Builder addToken(CloudConfig cloudConfig, OkHttpClient.Builder builder) {
        String token = cloudConfig.getToken();
        if (token != null && !token.isEmpty()) {
            builder.addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .addHeader("Authorization", "token=" + token)
                        .build();
                return chain.proceed(request);
            });
        }
        return builder;
    }

    /**
     * Adds a trust manager which trusts all certificates to the specified builder.
     *
     * @param builder The builder which will use the all-trusting trust manager.
     * @return The builder with the all-trusting trust manager.
     * @throws NoSuchAlgorithmException If the ssl algorithm doesn't exist - shouldn't occur.
     * @throws KeyManagementException   If a problem during trust manager creation occurs - shouldn't occur.
     */
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

        SSLContext sslContext = SSLContext.getInstance(SSL_ALGORITHM);
        sslContext.init(null, trustManagers, new java.security.SecureRandom());

        X509TrustManager reqLogger = (X509TrustManager) trustManagers[0];

        builder.sslSocketFactory(sslContext.getSocketFactory(), reqLogger);
        builder.hostnameVerifier((hostname, sslSession) -> true);

        return builder;
    }

    /**
     * Adds a trust manager which trusts the specified certificate to the specified builder.
     *
     * @param builder  The builder which will use the trust manager.
     * @param certData The certificate to use.
     * @return The builder with the custom trust manager.
     * @throws NoSuchAlgorithmException If the ssl algorithm doesn't exist - shouldn't occur.
     * @throws KeyStoreException        If a problem with the key store occurs.
     * @throws CertificateException     If a problem with the certificate exists.
     * @throws IOException              If a problem during trust store loading occurs - shouldn't occur.
     * @throws KeyManagementException   If a problem during trust manager creation occurs - shouldn't occur.
     */
    private OkHttpClient.Builder addTrustCertTrustManager(OkHttpClient.Builder builder, String certData) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, KeyManagementException {

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_FACTORY_TYPE);

        InputStream certInputStream = createCertInputStream(certData);
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certInputStream);
        String alias = cert.getSubjectX500Principal().getName();

        KeyStore trustStore = KeyStore.getInstance(KEYSTORE_TYPE);
        trustStore.load(null);
        trustStore.setCertificateEntry(alias, cert);
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance(SSL_ALGORITHM);
        sslContext.init(null, trustManagers, new SecureRandom());

        X509TrustManager reqLogger = (X509TrustManager) trustManagers[0];

        builder.sslSocketFactory(sslContext.getSocketFactory(), reqLogger);

        return builder;
    }

    /**
     * Creates a input stream for the specified certificate data.
     *
     * @param certData The certificate data.
     * @return The input stream.
     */
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
