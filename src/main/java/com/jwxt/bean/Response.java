package com.jwxt.bean;

import org.springframework.http.HttpStatus;


public class Response {
    private static final String OK = "ok";
    private static final String ERROR = "error";

    private Meta meta;
    private Object data;

    public Response success() {
        this.meta = new Meta(true, OK, HttpStatus.OK.value());
        return this;
    }

    public Response success(Object data) {
        this.meta = new Meta(true, OK, HttpStatus.OK.value());
        this.data = data;
        return this;
    }

    public Response failure() {
        this.meta = new Meta(false, ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), "error");
        return this;
    }

    public Response failure(String message, int statusCode) {
        this.meta = new Meta(false, message, statusCode, "error");
        return this;
    }

    public Response failure(String message, int statusCode, String type) {
        this.meta = new Meta(false, message, statusCode, type);
        return this;
    }

    public Meta getMeta() {
        return meta;
    }

    public Object getData() {
        return data;
    }

    public static class Meta {

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        private String type;
        private boolean success;
        private String message;
        private int statusCode;

        public Meta(boolean success) {
            this.success = success;
        }

        public Meta(boolean success, String message, int statusCode) {
            this.success = success;
            this.message = message;
            this.statusCode = statusCode;
        }

        public Meta(boolean success, String message, int statusCode, String type) {
            this.success = success;
            this.message = message;
            this.statusCode = statusCode;
            this.type = type;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
