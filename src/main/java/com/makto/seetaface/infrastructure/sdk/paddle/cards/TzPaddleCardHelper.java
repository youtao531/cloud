package com.makto.seetaface.infrastructure.sdk.paddle.cards;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.makto.seetaface.domain.model.CardType;
import com.makto.seetaface.domain.model.basic.ComCodes;
import com.makto.seetaface.infrastructure.constant.KeywordsConstant;
import com.makto.seetaface.infrastructure.exception.constant.ErrorException;
import com.makto.seetaface.infrastructure.sdk.paddle.PaddleCardHelper;
import com.makto.seetaface.infrastructure.sdk.paddle.model.BlockInfo;
import com.makto.seetaface.infrastructure.sdk.paddle.model.BlockMap;
import com.makto.seetaface.infrastructure.sdk.paddle.model.OcrBlock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lcc 2023/10/14 16:09
 */
@Slf4j
@Component(value = "tzPaddleCardHelper")
public class TzPaddleCardHelper implements PaddleCardHelper {

    @Override
    public void init() {
        PaddleCardHelper.set("TZ", this);
    }

    @Override
    public CardType fetchCardType(List<OcrBlock> blocks) {
        List<String> blockTexts = blocks.stream()
                .map(OcrBlock::getText)
                .filter(StrUtil::isNotBlank)
                .map(String::toUpperCase)
                .toList();

        List<String> driverFrontWords = KeywordsConstant.tzDriverFrontWords();
        boolean matched = blockTexts.stream().anyMatch(x -> driverFrontWords.stream().anyMatch(x::contains));
        if (matched) {
            return CardType.DRIVER_FRONT;
        }

        List<String> voterFrontWords = KeywordsConstant.tzVoterFrontWords();
        matched = blockTexts.stream().anyMatch(x -> voterFrontWords.stream().anyMatch(x::contains));
        if (matched) {
            return CardType.VOTER_FRONT;
        }

        List<String> idFrontWords = KeywordsConstant.tzIdFrontWords();
        matched = blockTexts.stream().anyMatch(x -> idFrontWords.stream().anyMatch(x::contains));
        if (matched) {
            return CardType.ID_FRONT;
        }

        throw new ErrorException(ComCodes.COMMON_FAIL.getCode(), "NOT_SUPPORT_CARD_TYPE");
    }

    @Override
    public BlockMap fetchBlockInfo(List<OcrBlock> blocks, CardType cardType) {
        BlockMap blockMap = new BlockMap();
        switch (cardType) {
            case ID_FRONT -> parseIdFront(blocks, blockMap);
            case VOTER_FRONT -> parseVoterFront(blocks, blockMap);
            case DRIVER_FRONT -> parseDriverFront(blocks, blockMap);
        }
        return blockMap;
    }

    private void parseIdFront(List<OcrBlock> blocks, BlockMap blockMap) {
        List<OcrBlock> blockList = BeanUtil.copyToList(blocks, OcrBlock.class);

        //卡号和出生日期
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> x.getText().length() == 23)
                .filter(x -> StrUtil.count(x.getText(), "-") == 3)
                .findFirst()
                .ifPresent(block -> {
                    String id = block.getText().toUpperCase();
                    Double reliability = block.getReliability();
                    String dateOfBirth = StrUtil.split(id, "-").get(0);

                    blockMap.setId(new BlockInfo(KeywordsConstant.ID_KEY, id, reliability));
                    blockMap.setDateOfBirth(new BlockInfo(KeywordsConstant.BIRTH_KEY, dateOfBirth, reliability));
                });
        //性别
        blockList.stream()
                .filter(x -> {
                    String text = x.getText();
                    if (StrUtil.isBlank(text)) {
                        return false;
                    }
                    text = text.toUpperCase();
                    return text.contains("JINSI") || text.contains(":F") || text.contains(":M") || text.equals("F") || text.equals("M");
                })
                .filter(block -> {
                    String text = block.getText().toUpperCase();
                    return text.contains("F") || text.contains("M");
                })
                .findFirst()
                .ifPresent(block -> {
                    Double reliability = block.getReliability();
                    String text = block.getText().toUpperCase();
                    if (text.contains("F") || text.contains("M")) {
                        String gender = text.contains("F") ? "F" : "M";

                        blockMap.setGender(new BlockInfo(KeywordsConstant.GENDER_KEY, gender, reliability));
                    }
                });
        //全名
        List<OcrBlock> names = blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> x.getText().toUpperCase().contains("JINA"))
                .toList();
        if (CollUtil.isNotEmpty(names)) {
            List<OcrBlock> nameList = new ArrayList<>();
            boolean oldCard = names.stream().anyMatch(x -> x.getText().toUpperCase().contains("KATI"));
            if (oldCard) {
                names.stream()
                        .filter(block -> block.getText().toUpperCase().contains("KWANZA"))
                        .findFirst()
                        .map(x -> formatterBlockText(x, "KWANZA"))
                        .ifPresent(nameList::add);
                names.stream()
                        .filter(block -> block.getText().toUpperCase().contains("KATI"))
                        .findFirst()
                        .map(x -> formatterBlockText(x, "KATI"))
                        .ifPresent(nameList::add);
            } else {
                names.stream()
                        .filter(block -> !block.getText().toUpperCase().contains("MWISHO"))
                        .findFirst()
                        .map(x -> formatterBlockText(x, "JINA"))
                        .stream().findFirst()
                        .ifPresent(nameList::add);
            }
            names.stream()
                    .filter(block -> block.getText().toUpperCase().contains("MWISHO"))
                    .findFirst()
                    .map(x -> formatterBlockText(x, "MWISHO"))
                    .ifPresent(nameList::add);


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

    private void parseVoterFront(List<OcrBlock> blocks, BlockMap blockMap) {
        List<OcrBlock> blockList = BeanUtil.copyToList(blocks, OcrBlock.class);

        //卡号
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> x.getText().length() == 17)
                .filter(x -> StrUtil.count(x.getText(), "-") == 4)
                .findFirst()
                .ifPresent(block -> {
                    String id = block.getText().toUpperCase();
                    Double reliability = block.getReliability();

                    blockMap.setId(new BlockInfo(KeywordsConstant.ID_KEY, id, reliability));
                });
        //性别
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> StrUtil.equalsAnyIgnoreCase(x.getText(), "ME", "KE"))
                .findFirst()
                .ifPresent(block -> {
                    String text = block.getText().toUpperCase();
                    String gender = text.contains("KE") ? "F" : "M";
                    Double reliability = block.getReliability();

                    blockMap.setGender(new BlockInfo(KeywordsConstant.GENDER_KEY, gender, reliability));
                });
        if (null == blockMap.getGender()) {
            blockList.stream()
                    .filter(x -> StrUtil.isNotBlank(x.getText()))
                    .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "SEX", "GENDER"))
                    .findFirst()
                    .ifPresent(block -> {
                        String text = block.getText().toUpperCase();
                        String splitName = text.contains("SEX") ? "SEX" : "GENDER";
                        List<String> strings = StrUtil.split(text, splitName, true, false);
                        if (strings.size() == 2) {
                            String str = strings.get(1);
                            if (StrUtil.equalsAnyIgnoreCase(str, "ME", "KE")) {
                                String gender = text.contains("KE") ? "F" : "M";
                                Double reliability = block.getReliability();

                                blockMap.setGender(new BlockInfo(KeywordsConstant.GENDER_KEY, gender, reliability));
                            }
                        }
                    });
        }
        if (null == blockMap.getGender()) {
            blockList.stream()
                    .filter(x -> StrUtil.isNotBlank(x.getText()))
                    .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "SEX", "GENDER"))
                    .findFirst()
                    .ifPresent(block -> {
                        int indexOf = blockList.indexOf(block);
                        if (blockList.size() > indexOf) {
                            OcrBlock tmpBlock = blockList.get(indexOf + 1);
                            String text = tmpBlock.getText().toUpperCase();
                            if (StrUtil.equalsAnyIgnoreCase(text, "ME", "KE")) {
                                String gender = text.contains("KE") ? "F" : "M";
                                Double reliability = block.getReliability();

                                blockMap.setGender(new BlockInfo(KeywordsConstant.GENDER_KEY, gender, reliability));
                            }
                        }
                    });
        }
        //出生日期
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> x.getText().length() == 10)
                .filter(x -> StrUtil.count(x.getText(), "/") == 2 || StrUtil.count(x.getText(), "-") == 2)
                .findFirst()
                .ifPresent(block -> {
                    String text = block.getText();
                    Double reliability = block.getReliability();

                    String formatter = text.contains("/") ? "dd/MM/yyyy" : "dd-MM-yyyy";
                    LocalDate localDate = LocalDate.parse(text, DateTimeFormatter.ofPattern(formatter));
                    String dateOfBirth = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                    blockMap.setDateOfBirth(new BlockInfo(KeywordsConstant.BIRTH_KEY, dateOfBirth, reliability));
                });
        //姓名
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "JINA", "KAMILI", "FULL"))
                .findFirst()
                .ifPresent(block -> {
                    int indexOf = blockList.indexOf(block);
                    if (blockList.size() > indexOf) {
                        OcrBlock nameBlock = blockList.get(indexOf + 1);
                        String fullName = nameBlock.getText().replaceAll("[^a-zA-Z\\s]", " ").trim();
                        Double reliability = nameBlock.getReliability();

                        blockMap.setFullName(new BlockInfo(KeywordsConstant.FULL_NAME_KEY, fullName, reliability));
                    }
                });
    }

    private void parseDriverFront(List<OcrBlock> blocks, BlockMap blockMap) {
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
        //性别 - 无
        //出生日期
        blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "BIRTH"))
                .findFirst()
                .ifPresent(block -> {
                    int indexOf = blockList.indexOf(block);
                    if (blockListSize > indexOf) {
                        for (int i = indexOf + 1; i < blockListSize; i++) {
                            OcrBlock tmpBlock = Stream.of(blockList.get(i))
                                    .filter(x -> x.getText().length() == 10)
                                    .filter(x -> StrUtil.count(x.getText(), "/") == 2)
                                    .findFirst()
                                    .orElse(null);
                            if (null == tmpBlock) {
                                continue;
                            }

                            String text = tmpBlock.getText();
                            Double reliability = tmpBlock.getReliability();

                            LocalDate localDate = LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            String dateOfBirth = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                            blockMap.setDateOfBirth(new BlockInfo(KeywordsConstant.BIRTH_KEY, dateOfBirth, reliability));
                            break;
                        }
                    }
                });
        //全名
        List<OcrBlock> names = blockList.stream()
                .filter(x -> StrUtil.isNotBlank(x.getText()))
                .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "FAMILY", "GIVEN"))
                .toList();
        if (CollUtil.isNotEmpty(names)) {
            List<OcrBlock> nameList = new ArrayList<>();
            names.stream()
                    .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "GIVEN"))
                    .findFirst()
                    .ifPresent(block -> {
                        int indexOf = blockList.indexOf(block);
                        if (blockListSize > indexOf) {
                            nameList.add(blockList.get(indexOf + 1));
                        }
                    });
            names.stream()
                    .filter(x -> StrUtil.containsAnyIgnoreCase(x.getText(), "FAMILY"))
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
        List<String> strings = StrUtil.split(text, split, true, false);
        if (strings.size() != 2) {
            return null;
        }

        text = strings.get(1).toUpperCase();
        x.setText(text);
        return x;
    }

    public static void main(String[] args) {
        String str = "Jinsia-GenderKE";

        System.out.println(str.replaceAll("\\p{P}", "")); //仅删除特殊字符
        System.out.println(str.replaceAll("[^a-zA-Z\\s]", "")); //删除特殊字符和数字
        System.out.println(str.replaceAll("[^a-zA-Z]", "")); //删除空格、特殊字符和数字
        System.out.println(str.replaceAll("\\s+", "")); //仅删除空格
        System.out.println(str.replaceAll("\\p{Punct}", "")); //仅删除特殊字符
        System.out.println(str.replaceAll("\\W", "")); //删除空格、特殊字符但不删除数字
        System.out.println(str.replaceAll("\\p{Punct}+", "")); //仅删除特殊字符
        System.out.println(str.replaceAll("\\p{Punct}|\\d", "")); //删除特殊字符和数字
    }
}
