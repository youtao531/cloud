package com.framework.cloud.infrastructure.sdk.paddle.cards;

import com.framework.cloud.domain.core.ComCodes;
import com.framework.cloud.domain.model.CardType;
import com.framework.cloud.infrastructure.constant.KeywordsConstant;
import com.framework.cloud.infrastructure.exception.constant.ErrorException;
import com.framework.cloud.infrastructure.sdk.paddle.PaddleCardHelper;
import com.framework.cloud.infrastructure.sdk.paddle.model.BlockInfo;
import com.framework.cloud.infrastructure.sdk.paddle.model.BlockMap;
import com.framework.cloud.infrastructure.sdk.paddle.model.OcrBlock;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.math.NumberUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.text.split.SplitUtil;
import org.dromara.hutool.core.util.ObjUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author youtao531 on 2024/9/7 16:09
 */
@Slf4j
@Component(value = "ciPaddleCardHelper")
public class CiPaddleCardHelper implements PaddleCardHelper {

    @Override
    public void init() {
        PaddleCardHelper.set("CI", this);
    }

    @Override
    public CardType fetchCardType(List<OcrBlock> blocks) {
        List<String> blockTexts = blocks.stream()
                .map(OcrBlock::getText)
                .filter(StrUtil::isNotBlank)
                .map(String::toUpperCase)
                .toList();

        List<String> idFrontWords = KeywordsConstant.ciIdFrontWords();
        boolean matched = blockTexts.stream().anyMatch(x -> idFrontWords.stream().anyMatch(x::contains));
        if (matched) {
            return CardType.ID_FRONT;
        }

        List<String> idBackWords = KeywordsConstant.ciIdBackWords();
        matched = blockTexts.stream().anyMatch(x -> idBackWords.stream().anyMatch(x::contains));
        if (matched) {
            return CardType.ID_BACK;
        }

        List<String> voterFrontWords = KeywordsConstant.ciVoterFrontWords();
        matched = blockTexts.stream().anyMatch(x -> voterFrontWords.stream().anyMatch(x::contains));
        if (matched) {
            return CardType.VOTER_FRONT;
        }

        throw new ErrorException(ComCodes.COMMON_FAIL.getCode(), "NOT_SUPPORT_CARD_TYPE");
    }

    @Override
    public BlockMap fetchBlockInfo(List<OcrBlock> blocks, CardType cardType) {
        BlockMap blockMap = new BlockMap();
        switch (cardType) {
            case ID_FRONT -> parseIdFront(blocks, blockMap);
            case ID_BACK -> parseIdBack(blocks, blockMap);
            case VOTER_FRONT -> parseVoterFront(blocks, blockMap);
        }
        return blockMap;
    }

    private void parseIdFront(List<OcrBlock> blocks, BlockMap blockMap) {
        List<OcrBlock> blockList = BeanUtil.copyToList(blocks, OcrBlock.class);
        int blockListSize = blockList.size();

        //卡号
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> x.getText().length() == 15)
                .filter(x -> x.getText().toUpperCase().startsWith("GHA"))
                .filter(x -> StrUtil.count(x.getText(), "-") == 2)
                .findFirst()
                .ifPresent(block -> {
                    String id = block.getText().toUpperCase();
                    Double reliability = block.getReliability();

                    blockMap.setId(new BlockInfo(KeywordsConstant.ID_KEY, id, reliability));
                });
        //出生日期
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> x.getText().length() == 10)
                .filter(x -> StrUtil.count(x.getText(), "/") == 2)
                .map(x -> {
                    LocalDate localDate = null;
                    try {
                        localDate = LocalDate.parse(x.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (Exception ignored) {
                    }
                    return null == localDate ? null : x;
                })
                .filter(Objects::nonNull)
                .min(Comparator.comparing(OcrBlock::getText))
                .ifPresent(block -> {
                    String text = block.getText();
                    Double reliability = block.getReliability();

                    LocalDate localDate = LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    String dateOfBirth = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                    blockMap.setDateOfBirth(new BlockInfo(KeywordsConstant.BIRTH_KEY, dateOfBirth, reliability));
                });
        //性别
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> StrUtil.equalsAnyIgnoreCase(x.getText(), "F", "M"))
                .findFirst()
                .ifPresent(block -> {
                    String text = block.getText().toUpperCase();
                    String gender = text.contains("F") ? "F" : "M";
                    Double reliability = block.getReliability();

                    blockMap.setGender(new BlockInfo(KeywordsConstant.GENDER_KEY, gender, reliability));
                });
        //全名
        List<OcrBlock> names = blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "SURNAME", "NOM", "FIRSTNAMES", "PRENOMS"))
                .toList();
        if (CollUtil.isNotEmpty(names)) {
            List<OcrBlock> nameList = new ArrayList<>();
            names.stream()
                    .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "FIRSTNAMES", "PRENOMS"))
                    .findFirst()
                    .ifPresent(block -> {
                        int indexOf = blockList.indexOf(block);
                        if (blockListSize > indexOf) {
                            nameList.add(blockList.get(indexOf + 1));
                        }
                    });
            names.stream()
                    .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "SURNAME", "NOM"))
                    .findFirst()
                    .ifPresent(block -> {
                        int indexOf = blockList.indexOf(block);
                        if (blockListSize > indexOf) {
                            nameList.add(blockList.get(indexOf + 1));
                        }
                    });
            if (CollUtil.isNotEmpty(nameList)) {
                String fullName = nameList.stream()
                        .map(OcrBlock::getText)
                        .map(String::toUpperCase)
                        .map(str -> str.replaceAll("[^a-zA-Z\\s]", " ").trim())
                        .filter(StrUtil::isNotBlank)
                        .collect(Collectors.joining(" "));
                double reliability = nameList.stream()
                        .map(OcrBlock::getReliability)
                        .reduce(0D, Double::sum) / nameList.size();

                blockMap.setFullName(new BlockInfo(KeywordsConstant.FULL_NAME_KEY, fullName, reliability));
            }
        }
    }

    private void parseIdBack(List<OcrBlock> blocks, BlockMap blockMap) {
        List<OcrBlock> blockList = BeanUtil.copyToList(blocks, OcrBlock.class);

        //卡号
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> x.getText().length() == 15)
                .filter(x -> x.getText().toUpperCase().startsWith("GHA"))
                .filter(x -> StrUtil.count(x.getText(), "-") == 2)
                .findFirst()
                .ifPresent(block -> {
                    String id = block.getText().toUpperCase();
                    Double reliability = block.getReliability();

                    blockMap.setId(new BlockInfo(KeywordsConstant.ID_KEY, id, reliability));
                });
        //出生日期 - 无
        //性别 - 无
        //全名 - 无
    }

    private void parseVoterFront(List<OcrBlock> blocks, BlockMap blockMap) {
        List<OcrBlock> blockList = BeanUtil.copyToList(blocks, OcrBlock.class);
        int blockListSize = blockList.size();

        //卡号
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> x.getText().length() == 10)
                .filter(x -> NumberUtil.isLong(x.getText()))
                .findFirst()
                .ifPresent(block -> {
                    String id = block.getText().toUpperCase();
                    Double reliability = block.getReliability();

                    blockMap.setId(new BlockInfo(KeywordsConstant.ID_KEY, id, reliability));
                });
        //性别
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> StrUtil.equalsAnyIgnoreCase(x.getText(), "MALE", "FEMALE"))
                .findFirst()
                .ifPresent(block -> {
                    String text = block.getText().toUpperCase();
                    String gender = text.contains("FEMALE") ? "F" : "M";
                    Double reliability = block.getReliability();

                    blockMap.setGender(new BlockInfo(KeywordsConstant.GENDER_KEY, gender, reliability));
                });
        //出生日期
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> x.getText().length() == 10)
                .filter(x -> StrUtil.count(x.getText(), "/") == 2)
                .map(x -> {
                    LocalDate localDate = null;
                    try {
                        localDate = LocalDate.parse(x.getText(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    } catch (Exception ignored) {
                    }
                    return null == localDate ? null : x;
                })
                .filter(Objects::nonNull)
                .filter(x -> ObjUtil.isNotNull(x.getText()))
                .min(Comparator.comparing(OcrBlock::getText))
                .ifPresent(block -> {
                    String text = block.getText();
                    Double reliability = block.getReliability();

                    LocalDate localDate = LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    String dateOfBirth = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                    blockMap.setDateOfBirth(new BlockInfo(KeywordsConstant.BIRTH_KEY, dateOfBirth, reliability));
                });
        //全名
        List<OcrBlock> names = blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "SURNAME", "OTHERNAMES"))
                .toList();
        if (CollUtil.isNotEmpty(names)) {
            List<OcrBlock> nameList = new ArrayList<>();
            names.stream()
                    .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "OTHERNAMES"))
                    .findFirst()
                    .ifPresent(block -> {
                        int indexOf = blockList.indexOf(block);
                        if (blockListSize > indexOf) {
                            nameList.add(blockList.get(indexOf + 1));
                        }
                    });
            names.stream()
                    .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "SURNAME"))
                    .findFirst()
                    .ifPresent(block -> {
                        int indexOf = blockList.indexOf(block);
                        if (blockListSize > indexOf) {
                            nameList.add(blockList.get(indexOf + 1));
                        }
                    });
            if (CollUtil.isNotEmpty(nameList)) {
                String fullName = nameList.stream()
                        .map(OcrBlock::getText)
                        .map(String::toUpperCase)
                        .map(str -> str.replaceAll("[^a-zA-Z\\s]", " ").trim())
                        .filter(StrUtil::isNotBlank)
                        .collect(Collectors.joining(" "));
                double reliability = nameList.stream()
                        .map(OcrBlock::getReliability)
                        .reduce(0D, Double::sum) / nameList.size();

                blockMap.setFullName(new BlockInfo(KeywordsConstant.FULL_NAME_KEY, fullName, reliability));
            }
        }
    }

    /**
     * 替换区块中的文字
     */
    private static OcrBlock formatterBlockText(OcrBlock x, String splitName) {
        String text = x.getText();
        String split = text.contains(":") ? ":" : splitName;
        List<String> strings = SplitUtil.split(text, split, true, false);
        if (strings.size() != 2) {
            return null;
        }

        text = strings.get(1).toUpperCase();
        x.setText(text);
        return x;
    }
}
