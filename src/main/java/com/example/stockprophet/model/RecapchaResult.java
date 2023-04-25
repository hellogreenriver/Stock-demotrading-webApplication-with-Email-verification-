package com.example.stockprophet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@ToString
public class RecapchaResult {
    
	@Getter
    @Setter
    private boolean success;
	@Getter
    @Setter
	private float score;
	@Getter
    @Setter
	@JsonProperty("challenge_ts")
	private String challengeTs;
	@Getter
    @Setter
	private String hostname;
	@JsonProperty("error-codes")
	@Getter
    @Setter
	private String[] errorCodes;
}