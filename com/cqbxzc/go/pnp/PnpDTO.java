package com.cqbxzc.go.pnp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PnpDTO implements Serializable {

    /**
     * 电话号码-A
     */
    private String phoneA;

    /**
     * 电话号码-B
     */
    private String phoneB;

    /**
     * 中间号码-X
     */
    private String phoneX;

    /**
     * 绑定关系id
     */
    private String subsId;

    /**
     * 绑定使用号池
     */
    private String poolKey;

    /**
     * 绑定记录id
     */
    private Long recordId;

    /**
     * 业务结果
     */
    @Builder.Default
    private boolean isSuccess = false;

}
