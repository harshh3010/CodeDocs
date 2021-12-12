package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

public abstract class AppRequest implements Serializable {
    public abstract RequestType getRequestType();
}
