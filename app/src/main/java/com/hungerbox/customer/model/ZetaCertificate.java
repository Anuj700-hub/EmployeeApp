package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 1/11/18.
 */

public class ZetaCertificate implements Serializable {

    @SerializedName("base64EncodedPublicKey")
    String base64EncodedPublicKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEAHI0UIjZYLwhY0Jym643kSuSqIWafIUWt61LDcxjieHstt377v3htsoA52D4kwdyoO1D+RS9kAfLwIFq7yvFww==";
    @SerializedName("purposeWithAttributes")
    ZetaCertPurPoseAttributes purposeWithAttributes = new ZetaCertPurPoseAttributes();
    @SerializedName("certID")
    String certID = "cert_dc90ddc9-23e8-4b2c-84a3-af94cb61147c";
    @SerializedName("type")
    String type = "PUBLIC_KEY";
    @SerializedName("issuerJID")
    String issuerJID = "zetamerchant.services.olympus";
    @SerializedName("subjectJID")
    String subjectJID = "zeta_1-144501-1_1@zetamerchant.zeta.in";
    @SerializedName("issuedOn")
    long issuedOn = 1515595534821L;
    @SerializedName("validTill")
    long validTill = 3093432334822L;
    @SerializedName("isRevoked")
    boolean isRevoked = false;
    @SerializedName("certificateAttributes")
    ZetaCertAttributes certificateAttributes = new ZetaCertAttributes();
    @SerializedName("headers")
    ZetaCertHeaders headers = new ZetaCertHeaders();


    public ZetaCertificate() {
    }

    public ZetaCertificate(String buildType) {
        if (buildType.equalsIgnoreCase("PROD")) {
            base64EncodedPublicKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAESJAfBzzzVd/+yJOm0LF6he9kJdBflSmKW9dhQYNPXbkKo7DZK6bmyPCHnAWt+sHGBsDQx/X5SwcuZoFyUyGo+g==";
            certID = "cert_58efeb9a-2591-48c7-bf95-9c25bf5b4438";
            issuerJID = "zetamerchant.services.olympus";
            subjectJID = "zeta_1-185144-1_2@business.zeta.in";
            issuedOn = 1516698285096L;
            validTill = 3094535085096L;
            certificateAttributes.setEnviroment("production");
            certificateAttributes.setPaymentTo("185144:1:2");
            certificateAttributes.setName("HungerBox");
            certificateAttributes.setAllowedIFIs("ZETA_ANY");
            headers.setSignatoryJID("zetamerchant.services.olympus");
            headers.setSignature("AAH/PA4wRAIgJClOUQk15yzs0LYbOYbm7OzM2O0Yx1UEwqhIxnEPVGkCIGlhNVV1hSrnFfrOUeImHgP/UrdIwzfw5adr+bya8f8T");
        }
    }


    public String getBase64EncodedPublicKey() {
        return base64EncodedPublicKey;
    }

    public void setBase64EncodedPublicKey(String base64EncodedPublicKey) {
        this.base64EncodedPublicKey = base64EncodedPublicKey;
    }

    public ZetaCertPurPoseAttributes getPurposeWithAttributes() {
        return purposeWithAttributes;
    }

    public void setPurposeWithAttributes(ZetaCertPurPoseAttributes purposeWithAttributes) {
        this.purposeWithAttributes = purposeWithAttributes;
    }

    public String getCertID() {
        return certID;
    }

    public void setCertID(String certID) {
        this.certID = certID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIssuerJID() {
        return issuerJID;
    }

    public void setIssuerJID(String issuerJID) {
        this.issuerJID = issuerJID;
    }

    public String getSubjectJID() {
        return subjectJID;
    }

    public void setSubjectJID(String subjectJID) {
        this.subjectJID = subjectJID;
    }

    public long getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(long issuedOn) {
        this.issuedOn = issuedOn;
    }

    public long getValidTill() {
        return validTill;
    }

    public void setValidTill(long validTill) {
        this.validTill = validTill;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public ZetaCertAttributes getCertificateAttributes() {
        return certificateAttributes;
    }

    public void setCertificateAttributes(ZetaCertAttributes certificateAttributes) {
        this.certificateAttributes = certificateAttributes;
    }

    public ZetaCertHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(ZetaCertHeaders headers) {
        this.headers = headers;
    }
}
