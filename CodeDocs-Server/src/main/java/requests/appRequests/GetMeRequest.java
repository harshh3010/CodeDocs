package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Request to fetch current user's info using login token
 */
public class GetMeRequest extends AppRequest implements Serializable {

    private String token;

    public GetMeRequest() {
    }

    public GetMeRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET_ME_REQUEST;
    }
}
