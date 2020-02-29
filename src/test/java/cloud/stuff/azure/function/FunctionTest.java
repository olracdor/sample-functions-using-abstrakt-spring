package cloud.stuff.azure.function;


import cdi.spring.service.SampleService;
import cdi.spring.service.SampleServiceWithOauth;
import cloud.stuff.azure.function.transformer.PayloadTransformer;
import com.microsoft.azure.functions.ExecutionContext;
import cloud.stuff.abstrakt.spring.AbstractSpring;
import cloud.stuff.abstrakt.spring.annotation.ApiClient;
import cloud.stuff.abstrakt.spring.annotation.MessageTransformer;
import cloud.stuff.abstrakt.spring.exception.TransformerException;
import org.junit.BeforeClass;

import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.junit.*;

import org.mockserver.model.Header;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class FunctionTest {


    private static ClientAndServer mockServer;


    @BeforeClass
    public static void startServer() {
        mockServer = startClientAndServer(8080);

    }

    @Test
    public void testSiteService() throws Exception {
        startMockForSites();
        startMockForOauth();

        class SiteServiceTest extends AbstractSpring {
            @ApiClient
            SampleServiceWithOauth service;

            void run(){

                ResponseEntity response = service.getSite("123");
                ArrayList sites = (ArrayList) response.getBody();
                LinkedHashMap site = (LinkedHashMap) sites.get(0);
                assertEquals(site.get("site_code"),"123");
            }

        }
        SiteServiceTest service = new SiteServiceTest();
        service.run();
    }

    @Test
    public void testMessageTransformer() throws Exception {


        class SiteServiceTest extends AbstractSpring {
            @MessageTransformer
            PayloadTransformer transformer;

            void run() throws TransformerException {
                ArrayList<Object> sampleList = new ArrayList<>();
                sampleList.add(1);
                //Test payload variable. We return the variable payload and it should contain the samplepayload string value
                Object transformed = transformer.transform("samplepayload","",null,"payload");
                assertEquals(transformed,"samplepayload");
                //Test refData variable. We return the variable refData and it should contain the sampleRefData string value
                transformed = transformer.transform("","sampleRefData",null,"refData");
                assertEquals(transformed,"sampleRefData");
                //Test siteDetails variable. We return the variable siteDetails and it should contain the sampleList arraylist value
                transformed = transformer.transform("","",sampleList,"siteDetails");
                assertEquals(transformed,sampleList);

            }

        }
        SiteServiceTest service = new SiteServiceTest();
        service.run();
    }

    @Test
    public void testSampleService() throws Exception {
        startMockForSampleService();

        class SampleServiceTest extends AbstractSpring {
            @ApiClient
            SampleService service;

            void run(){

                ResponseEntity response = service.postPriceChange("123","1231"
                        ,"123","{\"id\":\"1223456\", \"data\":{\"site_code\":\"32132\"}}");
                assertEquals(response.getStatusCodeValue(),200);
                LinkedHashMap status = (LinkedHashMap) response.getBody();
                assertEquals(status.get("status"),"processed");

                response = service.postEOD("123","1231"
                        ,"123","{\"id\":\"1223456\", \"data\":{\"site_code\":\"32132\"}}");
                assertEquals(response.getStatusCodeValue(),200);
                status = (LinkedHashMap) response.getBody();
                assertEquals(status.get("status"),"processed");
            }

        }
        SampleServiceTest service = new SampleServiceTest();
        service.run();
    }

    @Test
    public void testFunctions() throws Exception {
        startMockForOauth();
        startMockForSites();
        startMockForSampleService();
        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();
        SampleFunction1 function = new SampleFunction1();
        function.run("{\"id\":\"1223456\", \"data\":{\"site_code\":\"32132\"}}", "1".getBytes(), "{}".getBytes(), context);

        SampleFunction2 function2 = new SampleFunction2();
        function2.run("{\"id\":\"1223456\", \"data\":{\"site_code\":\"32132\"}}", "1".getBytes(), "{}".getBytes(), context);
    }

    private void startMockForSites() {
        new MockServerClient("127.0.0.1", 8080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/v1/sites")
                )

                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(Header.header("Content-Type", "application/json"))
                                .withBody("[{ \"site_code\": \"123\" }]")
                                .withDelay(TimeUnit.SECONDS, 1)
                );
    }

    private void startMockForSampleService() {
        new MockServerClient("127.0.0.1", 8080)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/v1/price/post"),
                        exactly(1)
                )

                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(Header.header("Content-Type", "application/json"))
                                .withBody("{ \"status\": \"processed\" }")
                                .withDelay(TimeUnit.SECONDS, 1)
                );

        new MockServerClient("127.0.0.1", 8080)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/v1/sales/post"),
                        exactly(1)
                )

                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(Header.header("Content-Type", "application/json"))
                                .withBody("{ \"status\": \"processed\" }")
                                .withDelay(TimeUnit.SECONDS, 1)
                );
    }

    private void startMockForOauth() {
        new MockServerClient("127.0.0.1", 8080)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/oauth")
                )

                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(Header.header("Content-Type", "application/json"))
                                .withBody("{\n" +
                                        "  \"access_token\":\"MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3\",\n" +
                                        "  \"token_type\":\"bearer\",\n" +
                                        "  \"expires_in\":3600,\n" +
                                        "  \"refresh_token\":\"IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk\",\n" +
                                        "  \"scope\":\"create\"\n" +
                                        "}")
                                .withDelay(TimeUnit.SECONDS, 1)
                );
    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }
}