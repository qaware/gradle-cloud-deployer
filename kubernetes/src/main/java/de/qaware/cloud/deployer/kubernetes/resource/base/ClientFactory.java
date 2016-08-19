package de.qaware.cloud.deployer.kubernetes.resource.base;

import de.qaware.cloud.deployer.kubernetes.config.ClusterConfig;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ClientFactory {

    private final Retrofit retrofit;

    public ClientFactory(ClusterConfig clusterConfig) {
        this.retrofit = createRetrofit(clusterConfig);
    }

    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    private Retrofit createRetrofit(ClusterConfig clusterConfig) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            String credentials = Credentials.basic(clusterConfig.getUsername(), clusterConfig.getPassword());
            Request original = chain.request();
            Request request = original.newBuilder()
                    .addHeader("Authorization", credentials)
                    .build();
            return chain.proceed(request);
        });

//        TODO: add logic for certs here...
//        try {
//            httpClient = CertHelper.addCerts(clusterConfig.getCertificate(), httpClient);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        OkHttpClient client = httpClient.build();

        return new Retrofit.Builder()
                .baseUrl(clusterConfig.getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
    }
}
