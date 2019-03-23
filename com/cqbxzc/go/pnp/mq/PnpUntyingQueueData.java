package com.cqbxzc.go.pnp.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PnpUntyingQueueData implements Serializable {

    /**
     * 绑定关系id
     */
    protected String subsId;

    /**
     * 中间号
     */
    protected String secretNo;

    /**
     * 使用的号码池
     */
    protected String poolKey;

    private Boolean isSuccess;

    private String message;

}
