package de.qaware.cloud.deployer.kubernetes.resource.base;

import de.qaware.cloud.deployer.kubernetes.config.ClusterConfig;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

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
            builder.sslSocketFactory(createSSLSocketFactory());
            builder.hostnameVerifier(createHostnameVerifier());
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

    private SSLSocketFactory createSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        final TrustManager[] trustAllCerts = new TrustManager[]{
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

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        return sslContext.getSocketFactory();
    }

    private HostnameVerifier createHostnameVerifier() {
        return (hostname, session) -> true;
    }
}
