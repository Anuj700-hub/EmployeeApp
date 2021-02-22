package com.cooltechworks.creditcarddesign;

/**
 * Created by Harish on 01/01/16.
 */
public class CardSelector {

    public static final int CVV_LENGHT_DEFAULT = 3;
    public static final CardSelector VISA = new CardSelector(R.drawable.card_color_round_rect_purple, R.drawable.chip, android.R.color.transparent, R.drawable.ic_billing_visa_logo, CardSelector.CVV_LENGHT_DEFAULT);
    public static final CardSelector MASTER = new CardSelector(R.drawable.card_color_round_rect_pink, R.drawable.chip_yellow, android.R.color.transparent, R.drawable.ic_billing_mastercard_logo, CardSelector.CVV_LENGHT_DEFAULT);
    public static final CardSelector DISCOVER = new CardSelector(R.drawable.card_color_round_rect_brown, android.R.color.transparent, android.R.color.transparent, R.drawable.ic_billing_discover_logo, CardSelector.CVV_LENGHT_DEFAULT);
    public static final CardSelector DEFAULT = new CardSelector(R.drawable.card_color_round_rect_default, R.drawable.chip, android.R.color.transparent, android.R.color.transparent, CardSelector.CVV_LENGHT_DEFAULT);
    public static final CardSelector RUPAY = new CardSelector(R.drawable.card_color_round_rect_blue, R.drawable.chip, android.R.color.transparent, R.drawable.rupay_logo, CardSelector.CVV_LENGHT_DEFAULT);
    public static final int CVV_LENGHT_AMEX = 4;
    public static final CardSelector AMEX = new CardSelector(R.drawable.card_color_round_rect_green, android.R.color.transparent, R.drawable.img_amex_center_face, R.drawable.ic_billing_amex_logo1, CardSelector.CVV_LENGHT_AMEX);
    private int mResCardId;
    private int mResChipOuterId;
    private int mResCenterImageId;
    private int mResLogoId;
    private int mCvvLength = CVV_LENGHT_DEFAULT;

    public CardSelector(int mDrawableCard, int mDrawableChipOuter, int mDrawableCenterImage, int logoId, int cvvLength) {
        this.mResCardId = mDrawableCard;
        this.mResChipOuterId = mDrawableChipOuter;
        this.mResCenterImageId = mDrawableCenterImage;
        this.mResLogoId = logoId;
        this.mCvvLength = cvvLength;
    }

    public static CardSelector selectCardType(CreditCardUtils.CardType cardType) {
        switch (cardType) {
            case AMEX_CARD:
                return AMEX;
            case DISCOVER_CARD:
                return DISCOVER;
            case MASTER_CARD:
                return MASTER;
            case VISA_CARD:
                return VISA;
            case RUPAY_CARD:
                return RUPAY;
            default:
                return DEFAULT;
        }
    }

    public static CardSelector selectCard(String cardNumber) {
        if (cardNumber != null && cardNumber.length() >= 1) {
            CreditCardUtils.CardType cardType = CreditCardUtils.selectCardType(cardNumber);
            CardSelector selector = selectCardType(cardType);

            if ((selector != DEFAULT) && (cardNumber.length() >= 3)) {
                int[] drawables = {R.drawable.card_color_round_rect_brown, R.drawable.card_color_round_rect_green, R.drawable.card_color_round_rect_pink, R.drawable.card_color_round_rect_purple, R.drawable.card_color_round_rect_blue};
                int hash = cardNumber.substring(0, 3).hashCode();

                if (hash < 0) {
                    hash = hash * -1;
                }

                int index = hash % drawables.length;

                int chipIndex = hash % 3;
                int[] chipOuter = {R.drawable.card_chip, R.drawable.chip_yellow, android.R.color.transparent};

                selector.setResCardId(drawables[index]);
                selector.setResChipOuterId(chipOuter[chipIndex]);

                return selector;
            }
        }

        return DEFAULT;
    }

    public int getResCardId() {
        return mResCardId;
    }

    public void setResCardId(int mResCardId) {
        this.mResCardId = mResCardId;
    }

    public int getResChipOuterId() {
        return mResChipOuterId;
    }

    public void setResChipOuterId(int mResChipOuterId) {
        this.mResChipOuterId = mResChipOuterId;
    }



    public int getResCenterImageId() {
        return mResCenterImageId;
    }

    public void setResCenterImageId(int mResCenterImageId) {
        this.mResCenterImageId = mResCenterImageId;
    }

    public int getResLogoId() {
        return mResLogoId;
    }

    public void setResLogoId(int mResLogoId) {
        this.mResLogoId = mResLogoId;
    }

    public int getCvvLength() {
        return mCvvLength;
    }

    public void setCvvLength(int mCvvLength) {
        this.mCvvLength = mCvvLength;
    }
}