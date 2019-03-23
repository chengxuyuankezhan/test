package com.cqbxzc.go.pay.rest.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Builder;
import lombok.Data;

/**
 * 响应支付回调的dto
 */
@Data
@Builder
@JacksonXmlRootElement(localName = "xml")
public class ResponseDTO {

	@Builder.Default
	@JacksonXmlProperty(localName = "return_code")
	private String code = "FAIL";

	@Builder.Default
	@JacksonXmlProperty(localName = "return_msg")
	private String msg = "接口交互发生异常";

}
