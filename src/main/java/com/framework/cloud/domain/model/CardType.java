package com.framework.cloud.domain.model;

import com.framework.cloud.domain.model.basic.ComCodes;
import com.framework.cloud.infrastructure.exception.constant.ErrorException;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * @author youtao531 on 2023/10/14 15:27
 */
@AllArgsConstructor
public enum CardType {

    ID_FRONT,
    ID_BACK,
    VOTER_FRONT,
    VOTER_BACK,
    DRIVER_FRONT,
    ;

    public final static List<CardType> ghCards = List.of(ID_FRONT, ID_BACK, VOTER_FRONT);
    public final static List<CardType> tzCards = List.of(ID_FRONT, ID_BACK, VOTER_FRONT, VOTER_BACK, DRIVER_FRONT);

    public String getValue() {
        return this.name();
    }

    /**
     * 根据标识获取枚举
     *
     * @param type 标识
     * @return 枚举
     */
    @Nullable
    public static CardType of(String type) {
        return Arrays.stream(values())
                .filter(x -> x.getValue().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new ErrorException(ComCodes.COMMON_FAIL.getCode(), "NOT_SUPPORT_CARD_TYPE"));
    }
}
