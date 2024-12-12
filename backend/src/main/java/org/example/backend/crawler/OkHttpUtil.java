package org.example.backend.crawler;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-12 20:17
 */
@Slf4j
public class OkHttpUtil {
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    /**
     * GET 请求
     *
     * @param url     请求的 URL
     * @param headers 请求头（可选）
     * @return 响应内容
     */
    public static String get(String url, Map<String, String> urlParams, Map<String, String> headers) {
        // 创建HttpUrl.Builder对象
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

        // 添加查询参数
        if (urlParams != null) {
            for (Map.Entry<String, String> entry : urlParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        // 构建最终的URL
        String finalUrl = urlBuilder.build().toString();
        Request.Builder requestBuilder = new Request.Builder().url(finalUrl);

        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        Request request = requestBuilder.build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Unexpected code {}", response);
                throw new IOException("Unexpected code " + response);
            }
            return response.body() != null ? response.body().string() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
