package pl.gov.sejm.isap.client;

/**
 * Exception thrown when an ISAP API call fails.
 */
public class IsapApiException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public IsapApiException(String message, int statusCode, String responseBody) {
        super(message + " (status: " + statusCode + ")");
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
