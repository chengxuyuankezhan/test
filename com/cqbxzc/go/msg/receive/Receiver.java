package com.cqbxzc.go.msg.receive;


import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 接收者
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Receiver implements Serializable {

    /**
     * 是否是所有
     */
    @Builder.Default
    private Boolean isAll = false;

    /**
     * 会员ID集合
     */
    private List<Long> member;

    /**
     * 商家ID集合
     */
    private List<Long> supplier;

    /**
     * 手机号集合
     */
    private List<String> mobile;

    /**
     * 微信openId集合
     */
    private List<String> openId;

    /**
     * 邮箱集合
     */
    private List<String> email;
}
