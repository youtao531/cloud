package com.framework.cloud.infrastructure.sdk.paddle;

import com.framework.cloud.domain.model.CardType;
import com.framework.cloud.infrastructure.sdk.paddle.model.BlockMap;
import com.framework.cloud.infrastructure.sdk.paddle.model.OcrBlock;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author youtao531 2023/10/14 16:03
 */
public interface PaddleCardHelper extends InitializingBean {

    /**
     * 获取 卡片类型
     *
     * @param blocks 区块信息
     * @return 卡片类型
     */
    CardType fetchCardType(List<OcrBlock> blocks);

    /**
     * 获取 区块信息
     *
     * @param blocks   区块信息
     * @param cardType 卡片类型
     * @return 卡片信息
     */
    BlockMap fetchBlockInfo(List<OcrBlock> blocks, CardType cardType);

    /**
     * 获取 区块信息
     *
     * @param blocks 区块信息
     * @return 卡片信息
     */
    default BlockMap fetchBlockInfo(List<OcrBlock> blocks) {
        CardType cardType = fetchCardType(blocks);
        return this.fetchBlockInfo(blocks, cardType);
    }

    Map<String, PaddleCardHelper> helpers = new ConcurrentHashMap<>();

    static PaddleCardHelper of(String countryCode) {
        return helpers.get(countryCode);
    }

    static void set(String countryCode, PaddleCardHelper helper) {
        helpers.put(countryCode, helper);
    }

    void init();

    @Override
    default void afterPropertiesSet() {
        init();
    }
}
