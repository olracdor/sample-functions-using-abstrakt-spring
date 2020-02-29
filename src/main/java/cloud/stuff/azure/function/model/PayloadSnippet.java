package cloud.stuff.azure.function.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayloadSnippet {

    @JsonProperty("data")
    private Data data;
    
    @JsonProperty("id")
    private String id;

    @JsonProperty("data")
    public Data getData() {
        if(data == null)
            data = new Data();
        return data;
    }

    @JsonProperty("data")
    public void setData(Data data) {
        this.data = data;
    }

    @JsonProperty("id")
    public String getId() {
        return this.id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Data {

        @JsonProperty("site_code")
        private String siteCode;

        @JsonProperty("site_code")
        public String getSiteCode() {
            return siteCode;
        }

        @JsonProperty("site_code")
        public void setSiteCode(String siteCode) {
            this.siteCode = siteCode;
        }
    }
}