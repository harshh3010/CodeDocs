package requests.appRequests;

import utilities.RequestType;

import java.io.Serializable;

/**
 * Class for creating requests to write them to server
 */
public abstract class AppRequest implements Serializable {
    public abstract RequestType getRequestType();
}
