package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ControllerConnection {


    @JsonProperty("E1_IN")
    private Boolean E1_IN;

    @JsonProperty("E1_OUT")
    private Boolean E1_OUT;

    @JsonProperty("E2_IN")
    private Boolean E2_IN;

    @JsonProperty("E2_OUT")
    private Boolean E2_OUT;

}
