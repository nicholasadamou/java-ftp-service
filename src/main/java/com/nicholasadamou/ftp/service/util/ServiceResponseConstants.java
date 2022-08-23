package com.nicholasadamou.ftp.service.util;

import javax.ws.rs.core.Response;

public interface ServiceResponseConstants {

    String DEFAULT_SERVICE_ERROR_MSG = "An unexpected error occurred";
    String NO_ACCESS_MSG = "The user does not have access";

    Response DEFAULT_ERROR_RESPONSE = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(DEFAULT_SERVICE_ERROR_MSG).build();
    Response DEFAULT_AUTH_ERROR_RESPONSE = Response.status(Response.Status.FORBIDDEN).entity(NO_ACCESS_MSG).build();

}
