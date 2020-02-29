package cdi.spring.service;

import cloud.stuff.abstrakt.spring.annotation.BaseUrl;
import cloud.stuff.abstrakt.spring.annotation.ContentType;
import cloud.stuff.abstrakt.spring.annotation.Payload;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v1")
public interface SampleService {

    @BaseUrl
    String baseurl = System.getenv("ApiUrl");

    @POST
    @Path("/price/post")
    @ContentType(MediaType.APPLICATION_JSON)
    ResponseEntity postPriceChange(@HeaderParam("client_id") String clientId
            , @HeaderParam("client_secret") String clientSecret
            , @HeaderParam("x-cid") String xid
            , @Payload String payload);

    @POST
    @Path("/sales/post")
    @ContentType(MediaType.APPLICATION_JSON)
    ResponseEntity postEOD(@HeaderParam("client_id") String clientId
            , @HeaderParam("client_secret") String clientSecret
            , @HeaderParam("x-cid") String xid
            , @Payload String payload);
}