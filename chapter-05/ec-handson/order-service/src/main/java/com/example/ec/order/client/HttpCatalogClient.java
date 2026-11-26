package com.example.ec.order.client;

import com.example.ec.common.BusinessException;
import com.example.ec.common.web.ErrorBody;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import org.springframework.web.client.RestClientException;

/** HTTPでcatalog-serviceを呼び出すCatalogClient。 */
@Component
public class HttpCatalogClient implements CatalogClient {

    private record ReserveRequest(String orderNo, List<ReserveLine> lines) {
    }

    private record ReleaseRequest(String orderNo) {
    }

    private final RestClient restClient;

    /** @param baseUrl catalog-serviceのURL（設定値catalog.base-url） */
    public HttpCatalogClient(RestClient.Builder restClientBuilder, @Value("${catalog.base-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * {@inheritDoc}
     * 2xxの応答を成功とする。
     *
     * @throws BusinessException     在庫不足（409でE0202）の場合（E0108）
     * @throws IllegalStateException それ以外の応答の場合
     */
    @Override
    public void reserve(String orderNo, List<ReserveLine> lines) {
        restClient.post()
                .uri("/stocks/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ReserveRequest(orderNo, lines))
                .exchange((request, response) -> {
                    HttpStatusCode status = response.getStatusCode();
                    if (status.is2xxSuccessful()) {
                        return null;
                    }
                    if (status.isSameCodeAs(HttpStatus.CONFLICT)) {
                        ErrorBody error = readErrorBody(response);
                        if (error != null && "E0202".equals(error.messageId())
                                && error.args() != null && !error.args().isEmpty()) {
                            throw BusinessException.conflict("E0108", error.args().get(0));
                        }
                    }
                    throw unexpected(request, status);
                });
    }

    /**
     * {@inheritDoc}
     * 204の応答だけを成功とする。
     *
     * @throws IllegalStateException 204以外の応答の場合
     */
    @Override
    public void release(String orderNo) {
        restClient.post()
                .uri("/stocks/release")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ReleaseRequest(orderNo))
                .retrieve()
                .onStatus(status -> !status.isSameCodeAs(HttpStatus.NO_CONTENT), (request, response) -> {
                    throw unexpected(request, response.getStatusCode());
                })
                .toBodilessEntity();
    }

    private static ErrorBody readErrorBody(ConvertibleClientHttpResponse response) {
        try {
            return response.bodyTo(ErrorBody.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    private static IllegalStateException unexpected(HttpRequest request, HttpStatusCode status) {
        return new IllegalStateException("Unexpected response from catalog-service: "
                + request.getMethod() + " " + request.getURI() + " status=" + status.value());
    }
}
