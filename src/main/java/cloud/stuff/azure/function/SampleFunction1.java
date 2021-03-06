package cloud.stuff.azure.function;

import cloud.stuff.azure.function.model.PayloadSnippet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import cloud.stuff.abstrakt.spring.annotation.ApiClient;
import cloud.stuff.abstrakt.spring.exception.TransformerException;
import cdi.spring.service.*;

public class SampleFunction1 extends AbstractSampleFunction {

    private static String CLIENT_ID = System.getenv("ApiClientId");
    private static String CLIENT_SECRET = System.getenv("ApiClientSecret");

    @ApiClient
    SampleService sampleService;

    @FunctionName("SampleFunction1")
    public void run(
            @ServiceBusTopicTrigger(name = "ServiceBusConnectionString", topicName = "%Topic%", connection = "ServiceBusConnectionString", subscriptionName = "%Subscription%") String payload,
            @BlobInput(name = "MessageTransformer", dataType = "binary", path = "%Mapper%", connection = "AzureWebJobsStorage") byte[] mapping,
            @BlobInput(name = "ReferenceData", dataType = "binary", path = "%RefData%", connection = "AzureWebJobsStorage") byte[] refData,
            ExecutionContext context) throws Exception {

        try {

            ObjectMapper om = new ObjectMapper();
            PayloadSnippet snip = om.readValue(payload, PayloadSnippet.class);
            String siteCode = snip.getData().getSiteCode();

            setCorrelationId(snip.getId());

            Object transformedPayload = super.process(siteCode, payload, mapping, refData, context);
            context.getLogger().info("Transformed payload: " + transformedPayload + " Id - " + getCorrelationId());

            sampleService.postEOD(SampleFunction1.CLIENT_ID
                    , SampleFunction1.CLIENT_SECRET
                    , getCorrelationId()
                    , transformedPayload.toString());

        } catch (TransformerException error) {
            context.getLogger().info("Catch error from mapper or throw it - throwing the error will send the message to the DLQ");
        }

    }

}
