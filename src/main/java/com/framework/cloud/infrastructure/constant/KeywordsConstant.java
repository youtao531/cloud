package com.framework.cloud.infrastructure.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @author youtao531 on 2023/10/17 14:58
 */
public class KeywordsConstant {

    private final static String ghIdFrontWords = "ECOWAS|IDENTITE|CEDEAO|BILHETE|NATIONALITY|DOCUMENT";
    private final static String ghIdBackWords = "OFFICE|POLICE|FOUND";
    private final static String ghVoterFrontWords = "VOTER|ELECTORAL|COMMISSION|OTHERNAMES";

    private final static String tzIdFrontWords = "JAMHURI|MUUNGANO|TANZANIA|KITAMBULISHO|TAIFA|REPUBLIC|CITIZEN|IDENTITY";
    private final static String tzDriverFrontWords = "DRIVING|LICENCE|FAMILY";
    private final static String tzVoterFrontWords = "FULLNAME|UCHAGUZI|KADI|MPIGA|KURA";

    public final static String ID_KEY = "id";
    public final static String GENDER_KEY = "gender";
    public final static String FULL_NAME_KEY = "fullName";
    public final static String BIRTH_KEY = "dateOfBirth";

    public static List<String> tzIdFrontWords() {
        return Arrays.stream(KeywordsConstant.tzIdFrontWords.split("\\|")).toList();
    }

    public static List<String> tzDriverFrontWords() {
        return Arrays.stream(KeywordsConstant.tzDriverFrontWords.split("\\|")).toList();
    }

    public static List<String> tzVoterFrontWords() {
        return Arrays.stream(KeywordsConstant.tzVoterFrontWords.split("\\|")).toList();
    }

    public static List<String> ghIdFrontWords() {
        return Arrays.stream(KeywordsConstant.ghIdFrontWords.split("\\|")).toList();
    }

    public static List<String> ghIdBackWords() {
        return Arrays.stream(KeywordsConstant.ghIdBackWords.split("\\|")).toList();
    }

    public static List<String> ghVoterFrontWords() {
        return Arrays.stream(KeywordsConstant.ghVoterFrontWords.split("\\|")).toList();
    }

    public static List<String> keIdFrontWords() {
        return Arrays.stream(KeywordsConstant.ghIdFrontWords.split("\\|")).toList();
    }

    public static List<String> keIdBackWords() {
        return Arrays.stream(KeywordsConstant.ghIdBackWords.split("\\|")).toList();
    }

    public static List<String> keVoterFrontWords() {
        return Arrays.stream(KeywordsConstant.ghVoterFrontWords.split("\\|")).toList();
    }

    public static List<String> ciIdFrontWords() {
        return Arrays.stream(KeywordsConstant.ghIdFrontWords.split("\\|")).toList();
    }

    public static List<String> ciIdBackWords() {
        return Arrays.stream(KeywordsConstant.ghIdBackWords.split("\\|")).toList();
    }

    public static List<String> ciVoterFrontWords() {
        return Arrays.stream(KeywordsConstant.ghVoterFrontWords.split("\\|")).toList();
    }
}