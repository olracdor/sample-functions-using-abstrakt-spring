package cloud.stuff.azure.function;

import java.util.ArrayList;

import cloud.stuff.azure.function.transformer.PayloadTransformer;
import com.microsoft.azure.functions.*;
import cloud.stuff.abstrakt.spring.annotation.*;
import cloud.stuff.abstrakt.spring.AbstractSpring;
import cdi.spring.service.*;
import org.springframework.stereotype.Component;

@Component
abstract class AbstractSampleFunction extends AbstractSpring {

    @ApiClient
    SampleServiceWithOauth sampleServiceWithOauth;

    @MessageTransformer
    PayloadTransformer transformer;

    public Object process(String siteCode, String payload, byte[] mapping, byte[] refData, ExecutionContext context)
            throws Exception {

        context.getLogger().info("Payload: " + payload + " Id - " + getCorrelationId());
        context.getLogger().info("Fetching Details. Id - " + getCorrelationId());

        ArrayList<Object> site = (ArrayList<Object>) sampleServiceWithOauth.getSite(siteCode).getBody();

        context.getLogger().info("Site Details: " + site.toString() + " Id - " + getCorrelationId());

        return transformer.transform(payload, new String(refData)
                , site
                , new String(mapping));

    }

}
