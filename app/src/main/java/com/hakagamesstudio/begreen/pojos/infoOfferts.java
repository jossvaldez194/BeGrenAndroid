package com.hakagamesstudio.begreen.pojos;

import java.io.Serializable;

public class infoOfferts implements Serializable {

    private int coupans_id;
    private int amount;
    private int usage_count;
    private int individual_use;
    private int usage_limit;
    private int usage_limit_per_user;
    private int limit_usage_to_x_items;
    private int free_shipping;
    private int exclude_sale_items;

    private String code;
    private String date_created;
    private String date_modified;
    private String description;
    private String discount_type;
    private String expiry_date;
    private String minimum_amount;
    private String maximum_amount;
    private String product_ids;
    private String exclude_product_ids;
    private String product_categories;
    private String email_restrictions;
    private String excluded_product_categories;
    private String used_by;
    private String photo;

    public int getCoupans_id() {
        return coupans_id;
    }

    public void setCoupans_id(int coupans_id) {
        this.coupans_id = coupans_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getUsage_count() {
        return usage_count;
    }

    public void setUsage_count(int usage_count) {
        this.usage_count = usage_count;
    }

    public int getIndividual_use() {
        return individual_use;
    }

    public void setIndividual_use(int individual_use) {
        this.individual_use = individual_use;
    }

    public int getUsage_limit() {
        return usage_limit;
    }

    public void setUsage_limit(int usage_limit) {
        this.usage_limit = usage_limit;
    }

    public int getUsage_limit_per_user() {
        return usage_limit_per_user;
    }

    public void setUsage_limit_per_user(int usage_limit_per_user) {
        this.usage_limit_per_user = usage_limit_per_user;
    }

    public int getLimit_usage_to_x_items() {
        return limit_usage_to_x_items;
    }

    public void setLimit_usage_to_x_items(int limit_usage_to_x_items) {
        this.limit_usage_to_x_items = limit_usage_to_x_items;
    }

    public int getFree_shipping() {
        return free_shipping;
    }

    public void setFree_shipping(int free_shipping) {
        this.free_shipping = free_shipping;
    }

    public int getExclude_sale_items() {
        return exclude_sale_items;
    }

    public void setExclude_sale_items(int exclude_sale_items) {
        this.exclude_sale_items = exclude_sale_items;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getMinimum_amount() {
        return minimum_amount;
    }

    public void setMinimum_amount(String minimum_amount) {
        this.minimum_amount = minimum_amount;
    }

    public String getMaximum_amount() {
        return maximum_amount;
    }

    public void setMaximum_amount(String maximum_amount) {
        this.maximum_amount = maximum_amount;
    }

    public String getProduct_ids() {
        return product_ids;
    }

    public void setProduct_ids(String product_ids) {
        this.product_ids = product_ids;
    }

    public String getExclude_product_ids() {
        return exclude_product_ids;
    }

    public void setExclude_product_ids(String exclude_product_ids) {
        this.exclude_product_ids = exclude_product_ids;
    }

    public String getProduct_categories() {
        return product_categories;
    }

    public void setProduct_categories(String product_categories) {
        this.product_categories = product_categories;
    }

    public String getEmail_restrictions() {
        return email_restrictions;
    }

    public void setEmail_restrictions(String email_restrictions) {
        this.email_restrictions = email_restrictions;
    }

    public String getExcluded_product_categories() {
        return excluded_product_categories;
    }

    public void setExcluded_product_categories(String excluded_product_categories) {
        this.excluded_product_categories = excluded_product_categories;
    }

    public String getUsed_by() {
        return used_by;
    }

    public void setUsed_by(String used_by) {
        this.used_by = used_by;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
