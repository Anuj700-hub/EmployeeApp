package com.hungerbox.customer.ksnetwork;


public interface KSContextErrorListener {

    int BAD_REQ = 400;
    int NOT_AUTH = 401;
    int DATA_NOT_FOUND = 404;
    int CREDENTIAL_CONFLICT = 409;
    int EMAIL_VERIFICATION = 417;
    int SERVER_FAILED = 500;
    int PARSE_ERROR = 007;
    int DONT_KNOW = 006;
    int NOT_ACCEPTABLE = 406;

    int TIMED_OUT = 408;
    int NO_NET_CONNECTION = 0;

    void handleError(int errorCode, String error, KSErrorResponse errorResponse);
}
