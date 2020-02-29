package cdi.spring.service;

import cloud.stuff.abstrakt.spring.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v1")
@Oauth2
public interface SampleServiceWithOauth {

    @BaseUrl
    String baseUrl = System.getenv("ApiUrl2");

    @AccessTokenUri
    String accessTokenUri = System.getenv("OauthAccessTokenUri");
    @ClientId
    String clientId = System.getenv("ApiClientId");
    @ClientSecret
    String clientSecret = System.getenv("ApiClientSecret");
    @GrantType
    String grantType = "client_credentials";
    @Scope
    String scope = System.getenv("OauthScope");


    @GET
    @Path("/sites?branch={branch}")
    @ContentType(MediaType.APPLICATION_JSON)
    ResponseEntity getSite(@QueryParam("branch") String branch);

}