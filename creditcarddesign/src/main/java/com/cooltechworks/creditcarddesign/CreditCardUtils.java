package com.cooltechworks.creditcarddesign;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by Harish on 03/01/16.
 */
public class CreditCardUtils {
    public static final String EXTRA_TOKEN = "token";
    public static final int MAX_LENGTH_CARD_NUMBER = 16;
    public static final int MAX_LENGTH_CARD_NUMBER_AMEX = 15;
    public static final Interval MAX_LENGTH_CARD_NUMBER_MAESTRO = new Interval(12, 19);
    public static final String CARD_NUMBER_FORMAT = "XXXX XXXX XXXX XXXX";
    public static final String CARD_NUMBER_FORMAT_AMEX = "XXXX XXXXXX XXXXX";
    public static final String EXTRA_CARD_NUMBER = "card_number";
    public static final String EXTRA_CARD_CVV = "card_cvv";
    public static final String EXTRA_CARD_EXPIRY = "card_expiry";
    public static final String EXTRA_CARD_HOLDER_NAME = "card_holder_name";
    public static final String EXTRA_CARD_SHOW_CARD_SIDE = "card_side";
    public static final String EXTRA_VALIDATE_EXPIRY_DATE = "expiry_date";
    public static final String EXTRA_ENTRY_START_PAGE = "start_page";
    public static final String EXTRA_CARD_BRAND = "card_brand";
    public static final int CARD_SIDE_FRONT = 1, CARD_SIDE_BACK = 0;
    public static final int CARD_NUMBER_PAGE = 0, CARD_EXPIRY_PAGE = 1;
    public static final int CARD_CVV_PAGE = 3, CARD_NAME_PAGE = 2;
    public static final String SPACE_SEPERATOR = " ";
    public static final String SLASH_SEPERATOR = "/";
    public static final char CHAR_X = 'X';
    private static final String PATTERN_AMEX = "^3(4|7)[0-9 ]*";
    private static final String PATTERN_VISA = "^4[0-9 ]*";
    private static final String PATTERN_MASTER = "^5[1-5][0-9 ]*";
    private static final String PATTERN_DISCOVER = "^6[0-9 ]*";
    private static final String PATTERN_RUPAY = "^60[0-9]*|^6521[0-9]*";
    private static final String PATTER_MAESTRO = "^(5018|5081|5044|5020|5038|603845|6304|6759|676[1-3]|6220|504834|504817|504645)[0-9]*";

    public static boolean ifValidRupayCardNumber(String cardNumberWithoutSpaces) {
        ArrayList<Interval> rangeList = new ArrayList<Interval>();
        rangeList.add(new Interval(508500, 508999));
        rangeList.add(new Interval(606985, 607384));
        rangeList.add(new Interval(607385, 607484));
        rangeList.add(new Interval(607485, 607984));
        rangeList.add(new Interval(608001, 608100));
        rangeList.add(new Interval(608101, 608200));
        rangeList.add(new Interval(608201, 608300));
        rangeList.add(new Interval(608301, 608350));
        rangeList.add(new Interval(608351, 608500));
        rangeList.add(new Interval(652150, 652849));
        rangeList.add(new Interval(652850, 653049));
        rangeList.add(new Interval(653050, 653149));
        rangeList.add(new Interval(817200, 819899));
        rangeList.add(new Interval(819900, 820199));
        cardNumberWithoutSpaces = cardNumberWithoutSpaces.replace(CreditCardUtils.SPACE_SEPERATOR, "");
        long cardNumber = 0;
        try {
            cardNumber = Long.parseLong(cardNumberWithoutSpaces.substring(0, 6));
        } catch (NumberFormatException nfe) {
            if (cardNumberWithoutSpaces.contains("XXXX"))
                return true;
        }
        for (Interval range :
                rangeList) {
            if (cardNumber >= range.start && cardNumber <= range.end) {
                return true;
            }
        }
        return false;

    }

    public static boolean validateCreditCardNumber(String cardNumber) {

        int[] ints = new int[cardNumber.length()];
        for (int i = 0; i < cardNumber.length(); i++) {
            ints[i] = Integer.parseInt(cardNumber.substring(i, i + 1));
        }
        for (int i = ints.length - 2; i >= 0; i = i - 2) {
            int j = ints[i];
            j = j * 2;
            if (j > 9) {
                j = j % 10 + 1;
            }
            ints[i] = j;
        }
        int sum = 0;
        for (int i = 0; i < ints.length; i++) {
            sum += ints[i];
        }
        if (sum % 10 == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String handleCardNumber(String inputCardNumber) {

        return handleCardNumber(inputCardNumber, SPACE_SEPERATOR);
    }

    public static CardType selectCardType(String cardNumber) {
        if (CardEditActivity.mCardBrand != null && CardEditActivity.mCardBrand.length() > 0) {
            if (CardEditActivity.mCardBrand.equalsIgnoreCase("MASTERCARD"))
                return CardType.MASTER_CARD;
            else if (CardEditActivity.mCardBrand.equalsIgnoreCase("VISA"))
                return CardType.VISA_CARD;
            else if (CardEditActivity.mCardBrand.equalsIgnoreCase("AMEX"))
                return CardType.AMEX_CARD;
            else if (CardEditActivity.mCardBrand.equalsIgnoreCase("MAESTRO"))
                return CardType.MAESTRO;
            else if (CardEditActivity.mCardBrand.equalsIgnoreCase("RUPAY"))
                return CardType.RUPAY_CARD;
            else
                return CardType.UNKNOWN_CARD;
        }
        Pattern pCardType = Pattern.compile(PATTERN_VISA);
        if (pCardType.matcher(cardNumber).matches())
            return CardType.VISA_CARD;
        pCardType = Pattern.compile(PATTERN_MASTER);
        if (pCardType.matcher(cardNumber).matches())
            return CardType.MASTER_CARD;
        pCardType = Pattern.compile(PATTERN_AMEX);
        if (pCardType.matcher(cardNumber).matches())
            return CardType.AMEX_CARD;
        if (cardNumber.length() >= 7 && ifValidRupayCardNumber(cardNumber))
            return CardType.RUPAY_CARD;
        pCardType = Pattern.compile(PATTERN_DISCOVER);
        if (pCardType.matcher(cardNumber).matches())
            return CardType.DISCOVER_CARD;
        pCardType = Pattern.compile(PATTER_MAESTRO);
        if (pCardType.matcher(cardNumber).matches())
            return CardType.MAESTRO;
        return CardType.UNKNOWN_CARD;
    }

    public static int selectCardLength(CardType cardType) {

        switch (cardType) {
            case VISA_CARD:
                return MAX_LENGTH_CARD_NUMBER;
            case MASTER_CARD:
                return MAX_LENGTH_CARD_NUMBER;
            case MAESTRO:
                return -1;
            case AMEX_CARD:
                if (CardEditActivity.mCardBrand != null && CardEditActivity.mCardBrand.length() > 0
                        && CardEditActivity.mCardBrand.equalsIgnoreCase("AMEX"))
                    return MAX_LENGTH_CARD_NUMBER;
                else
                    return MAX_LENGTH_CARD_NUMBER_AMEX;
            case RUPAY_CARD:
                return MAX_LENGTH_CARD_NUMBER;
            case UNKNOWN_CARD:
                return MAX_LENGTH_CARD_NUMBER;
            default:
                return MAX_LENGTH_CARD_NUMBER;
        }
    }

    public static String handleCardNumber(String inputCardNumber, String seperator) {
        String unformattedText = inputCardNumber.replace(seperator, "");
        CardType cardType = selectCardType(inputCardNumber);
        String format = (cardType == CardType.AMEX_CARD) ? CARD_NUMBER_FORMAT_AMEX : CARD_NUMBER_FORMAT;
        StringBuilder sbFormattedNumber = new StringBuilder();
        for (int iIdx = 0, jIdx = 0; (iIdx < format.length()) && (unformattedText.length() > jIdx); iIdx++) {
            if (format.charAt(iIdx) == CHAR_X)
                sbFormattedNumber.append(unformattedText.charAt(jIdx++));
            else
                sbFormattedNumber.append(format.charAt(iIdx));
        }

        return sbFormattedNumber.toString();
    }

    public static String formatCardNumber(String inputCardNumber, String seperator) {
        String unformattedText = inputCardNumber.replace(seperator, "");
        CardType cardType = selectCardType(inputCardNumber);
        String format = (cardType == CardType.AMEX_CARD) ? CARD_NUMBER_FORMAT_AMEX : CARD_NUMBER_FORMAT;
        StringBuilder sbFormattedNumber = new StringBuilder();
        for (int iIdx = 0, jIdx = 0; iIdx < format.length(); iIdx++) {
            if ((format.charAt(iIdx) == CHAR_X) && (unformattedText.length() > jIdx))
                sbFormattedNumber.append(unformattedText.charAt(jIdx++));
            else
                sbFormattedNumber.append(format.charAt(iIdx));
        }

        return sbFormattedNumber.toString().replace(SPACE_SEPERATOR, SPACE_SEPERATOR + SPACE_SEPERATOR);
    }

    public static String handleExpiration(String month, String year) {

        return handleExpiration(month + year);
    }

    public static String handleExpiration(@NonNull String dateYear) {

        String expiryString = dateYear.replace(SLASH_SEPERATOR, "");

        String text;
        if (expiryString.length() >= 2) {
            String mm = expiryString.substring(0, 2);
            String yy;
            text = mm;

            try {
                if (Integer.parseInt(mm) > 12) {
                    mm = "12"; // Cannot be more than 12.
                }
            } catch (Exception e) {
                mm = "01";
            }

            if (expiryString.length() >= 4) {
                yy = expiryString.substring(2, 4);

                try {
                    Integer.parseInt(yy);
                } catch (Exception e) {

                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    yy = String.valueOf(year).substring(2);
                }

                text = mm + SLASH_SEPERATOR + yy;

            } else if (expiryString.length() == 2) {
                text = mm + SLASH_SEPERATOR;
            } else if (expiryString.length() > 2) {
                yy = expiryString.substring(2);
                text = mm + SLASH_SEPERATOR + yy;
            }
        } else {
            text = expiryString;
        }

        return text;
    }


    public enum CardType {
        UNKNOWN_CARD, AMEX_CARD, MASTER_CARD, VISA_CARD, DISCOVER_CARD, RUPAY_CARD, MAESTRO
    }
}
