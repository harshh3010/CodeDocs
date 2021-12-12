package response.appResponse;

import utilities.ResponseType;

import java.io.Serializable;


public abstract class AppResponse implements Serializable {

    public abstract ResponseType getResponseType();
}