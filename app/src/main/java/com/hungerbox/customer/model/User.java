package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.ApplicationConstants;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by peeyush on 21/6/16.
 */
public class User implements Comparable<User>, Serializable {

    long id;
    @SerializedName("username")
    String email;
    @SerializedName("name")
    String name;
    String password;
    @SerializedName("card_code")
    String tagId;
    @SerializedName("card_no")
    String tagCode;
    @SerializedName("card_type")
    String cardType;
    @SerializedName("access_token")
    String token;
    @SerializedName("refresh_token")
    String refreshToken;
    @SerializedName("expires_in")
    Long tokenExpiry;
    @SerializedName("mobile_no")
    String phoneNumber;
    @SerializedName("grant_type")
    String grantType = "password";
    @SerializedName("client_id")
    String clientId = UrlConstant.CLIENT_ID;
    @SerializedName("client_secret")
    String clientSecret = UrlConstant.CLIENT_SECRET;
    @SerializedName("wallet")
    double walletBalance = 0;
    @SerializedName("company_id")
    long companyId;
    @SerializedName("reset_required")
    int resetRequired = 1;

    @SerializedName("allowed_in_group_order")
    boolean allowedInGroup;

    @SerializedName("current_wallets")
    WalletResponse currentWallets;
    @SerializedName("card_check")
    int cardCheck = 0;
    @SerializedName("employee_type_id")
    int employeeTypeId = 0;
    @SerializedName("email")
    String empEmail = "";
    @SerializedName("default_location_id")
    long locationId;
    @SerializedName("default_location_name")
    String locationName;
    @SerializedName("default_location_capacity")
    float locationCapacity;
    @SerializedName("desk_ordering_enabled")
    int deskOrderingEnabled = 0;
    @SerializedName("desk_reference")
    String deskReference=null;
    @SerializedName("required_email_verification")
    boolean emailVerificationRequired = false;
    @SerializedName("is_email_verified")
    boolean emailVerified = false;
    @SerializedName("is_email_verified_flag")
    int emailVerifiedflag = 0;
    @SerializedName("updated_email")
    String updatedEmail = "";
    @SerializedName("free_category_ids")
    ArrayList<String> freeCategoryIDs = new ArrayList<>();
    @SerializedName("has_due_bills")
    int hasDueBills = 0;
    @SerializedName("forced_redirect_url")
    String declarationFromLink;
    @SerializedName("registration_type")
    String registration_type = "";
    @SerializedName("gender")
    String gender = "";

    public String getGender() {
        return (gender == null) ? "" : gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegistration_type() {
        if(registration_type == null){
            return "";
        }
        return registration_type;
    }

    public void setRegistration_type(String registration_type) {
        this.registration_type = registration_type;
    }

    public String getDeclarationFromLink() {
        return declarationFromLink;
    }

    public void setDeclarationFromLink(String declarationFromLink) {
        this.declarationFromLink = declarationFromLink;
    }

    public boolean getHasDueBills() {
        return hasDueBills==1?true:false;
    }

    public void setHasDueBills(int hasDueBills) {
        this.hasDueBills = hasDueBills;
    }

    public boolean isEmailVerificationRequired() {
        return emailVerificationRequired;
    }

    public void setEmailVerificationRequired(boolean emailVerificationRequired) {
        this.emailVerificationRequired = emailVerificationRequired;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getUpdatedEmail() {
        return updatedEmail;
    }

    public void setUpdatedEmail(String updatedEmail) {
        this.updatedEmail = updatedEmail;
    }

    public ArrayList<String> getFreeCategoryIDs() {
        return freeCategoryIDs;
    }

    public void setFreeCategoryIDs(ArrayList<String> freeCategoryIDs) {
        this.freeCategoryIDs = freeCategoryIDs;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public int getEmployeeTypeId() {
        return employeeTypeId;
    }

    public void setEmployeeTypeId(int employeeTypeId) {
        this.employeeTypeId = employeeTypeId;
    }

    public float getLocationCapacity() {
        return locationCapacity;
    }

    public void setLocationCapacity(float locationCapacity) {
        this.locationCapacity = locationCapacity;
    }

    public boolean isCardCheck() {
        if (cardCheck == 0)
            return false;
        else
            return true;
    }

    public void setCardCheck(boolean cardCheck) {
        if (cardCheck)
            this.cardCheck = 1;
        else
            this.cardCheck = 0;
    }

    public long getId() {
        return id;
    }

    public User setId(long id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        if (email == null)
            email = "";
        return email;
    }

    public User setUserName(String userName) {
        this.email = userName;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getAuthToken() {
        return token;
    }

    public User setAuthToken(String authToken) {
        this.token = authToken;
        return this;
    }

    public Long getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(Long tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getWalletBalanceString() {
        return String.format("₹ %.2f", getCurrentWalletBalance());
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getEmailVerifiedflag() {
        return emailVerifiedflag;
    }

    public void setEmailVerifiedflag(int emailVerifiedflag) {
        this.emailVerifiedflag = emailVerifiedflag;
    }

    public String getPhoneNumber() {
        if (phoneNumber == null)
            return "";
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getTagId() {
        return tagId;
    }

    public User setTagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    public String getTagCode() {
        if (tagCode == null)
            tagCode = "";
        return tagCode;
    }

    public User setTagCode(String tagCode) {
        this.tagCode = tagCode;
        return this;
    }

    public long getCompanyId() {
        return companyId;
    }

    public User setCompanyId(long companyId) {
        this.companyId = companyId;
        return this;
    }


    public boolean isResetRequired() {
        return resetRequired > 0;
    }

    public int getResetRequired() {
        return resetRequired;
    }

    public void setResetRequired(int resetRequired) {
        this.resetRequired = resetRequired;
    }

    public double getCurrentWalletBalance() {
        double walletBalance = 0;
        if (currentWallets != null) {
            for (Wallet wallet : currentWallets.getWallets()) {
                walletBalance += wallet.amount;
            }
        } else {
            return this.walletBalance;
        }

        return walletBalance;
    }

    public double getCurrentWalletBalance(boolean isDisplayOnly) {
        double walletBalance = 0;
        if(currentWallets!=null){
            for(Wallet wallet: currentWallets.getWallets()){
                walletBalance += wallet.getDisplayAmount(isDisplayOnly);
            }
        }else{
            return this.walletBalance;
        }

        return walletBalance;
    }


    public WalletResponse getCurrentWallets() {
        return currentWallets;
    }

    public void setCurrentWallets(WalletResponse currentWallets) {
        this.currentWallets = currentWallets;
    }

    public boolean isAllowedInGroup() {
        return allowedInGroup;
    }

    public void setAllowedInGroup(boolean allowedInGroup) {
        this.allowedInGroup = allowedInGroup;
    }

    public String getFirstWalletString() {
        if (currentWallets != null && currentWallets.getWallets() != null
                && currentWallets.getWallets().size() > 0) {
            return currentWallets.getWallets().get(0).getAmountString();
        } else {
            return String.format("\u20B9 %.2f", 0);
        }
    }

    public int getDeskOrderingEnabled() {
        return deskOrderingEnabled;
    }

    public void setDeskOrderingEnabled(int deskOrderingEnabled) {
        this.deskOrderingEnabled = deskOrderingEnabled;
    }

    public String getDeskReference() {
        return deskReference;
    }

    public void setDeskReference(String deskReference) {
        this.deskReference = deskReference;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User other = (User) obj;
            return other.id == id;
        }
        return false;
    }

    @Override
    public int compareTo(User user) {
        return (int) (id - user.id);
    }

    public double getFirstWalletAmount() {
        double amount = 0;
        if (currentWallets != null && currentWallets.getWallets() != null
                && currentWallets.getWallets().size() > 0) {
            return currentWallets.getWallets().get(0).getAmount();
        } else {
            return amount;
        }
    }


    public String getCardType() {
        if (cardType == null)
            cardType = "";
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardMessage() {
        if (!getTagCode().isEmpty()) {
            return getTagCode();
        } else if (getCardType().isEmpty()) {
            return "Please collect your card from the helpdesk";
        } else if (getCardType().equalsIgnoreCase(ApplicationConstants.CARD_TYPE_STICKER)) {
            return "You have a sticker attached to this account";
        } else if (getCardType().equalsIgnoreCase(ApplicationConstants.CARD_TYPE_COMPANY)) {
            return "Your company card attached to this account";
        } else {
            return "";
        }
    }
}
