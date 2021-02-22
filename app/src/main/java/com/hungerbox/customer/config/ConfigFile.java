package com.hungerbox.customer.config;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 5/9/16.
 */
public class ConfigFile {

    @SerializedName("client")
    public String client;
    @SerializedName("company_key")
    public String companyKey;
    @SerializedName("file_path")
    public String filePath;
}
