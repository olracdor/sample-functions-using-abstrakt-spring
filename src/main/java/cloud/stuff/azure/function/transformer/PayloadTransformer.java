package cloud.stuff.azure.function.transformer;

import java.util.ArrayList;

import cloud.stuff.abstrakt.spring.annotation.*;
import cloud.stuff.abstrakt.spring.exception.TransformerException;

public interface PayloadTransformer {

    Object transform(@MapperParam("payload") String payload
            , @MapperParam("refData") String refData
            , @MapperParam("siteDetails") ArrayList<Object> siteDetails
            , @Mapper String script) throws TransformerException;

}