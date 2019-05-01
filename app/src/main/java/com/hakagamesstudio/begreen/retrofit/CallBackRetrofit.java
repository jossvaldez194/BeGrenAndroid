package com.hakagamesstudio.begreen.retrofit;

import com.hakagamesstudio.begreen.pojos.AllOffertsList;
import com.hakagamesstudio.begreen.pojos.infoOfferts;
import com.hakagamesstudio.begreen.pojos.address_model.AddressData;
import com.hakagamesstudio.begreen.pojos.banner_model.BannerData;
import com.hakagamesstudio.begreen.pojos.category_model.CategoryData;
import com.hakagamesstudio.begreen.pojos.coupons_model.CouponsData;
import com.hakagamesstudio.begreen.pojos.device_model.AppSettingsData;
import com.hakagamesstudio.begreen.pojos.filter_model.get_filters.FilterData;
import com.hakagamesstudio.begreen.pojos.news_model.all_news.NewsData;
import com.hakagamesstudio.begreen.pojos.news_model.news_categories.NewsCategoryData;
import com.hakagamesstudio.begreen.pojos.order_model.OrderData;
import com.hakagamesstudio.begreen.pojos.order_model.PostOrder;
import com.hakagamesstudio.begreen.pojos.pages_model.PagesData;
import com.hakagamesstudio.begreen.pojos.product_model.GetAllProducts;
import com.hakagamesstudio.begreen.pojos.product_model.ProductData;
import com.hakagamesstudio.begreen.pojos.shipping_model.PostTaxAndShippingData;
import com.hakagamesstudio.begreen.pojos.shipping_model.ShippingRateData;
import com.hakagamesstudio.begreen.pojos.user_model.UserData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CallBackRetrofit {

    @GET("getBanners")
    Call<BannerData> getBanners();

    @FormUrlEncoded
    @POST("allCategories")
    Call<CategoryData> getAllCategories(@Field("language_id") int language_id);

    @GET("siteSetting")
    Call<AppSettingsData> getAppSetting();

    @FormUrlEncoded
    @POST("getAllPages")
    Call<PagesData> getStaticPages(@Field("language_id") int language_id);

    @POST("getAllProducts")
    Call<ProductData> getAllProducts(@Body GetAllProducts getAllProducts);

    @FormUrlEncoded
    @POST("getFilters")
    Call<FilterData> getFilters(@Field("categories_id") int categories_id,
                                @Field("language_id") int language_id);

    @FormUrlEncoded
    @POST("getAllNews")
    Call<NewsData> getAllNews(@Field("language_id") int language_id,
                              @Field("page_number") int page_number,
                              @Field("is_feature") int is_feature,
                              @Field("categories_id") String categories_id);

    @FormUrlEncoded
    @POST("allNewsCategories")
    Call<NewsCategoryData> allNewsCategories(@Field("language_id") int language_id,
                                             @Field("page_number") int page_number);

    @FormUrlEncoded
    @POST("likeProduct")
    Call<ProductData> likeProduct(@Field("liked_products_id") int liked_products_id,
                                  @Field("liked_customers_id") String liked_customers_id);

    @FormUrlEncoded
    @POST("unlikeProduct")
    Call<ProductData> unlikeProduct(@Field("liked_products_id") int liked_products_id,
                                    @Field("liked_customers_id") String liked_customers_id);

    @FormUrlEncoded
    @POST("processLogin")
    Call<UserData> processLogin(@Field("customers_email_address") String customers_email_address,
                                @Field("customers_password") String customers_password);

    @FormUrlEncoded
    @POST("processRegistration")
    Call<UserData> processRegistration(@Field("customers_firstname") String customers_firstname,
                                       @Field("customers_lastname") String customers_lastname,
                                       @Field("customers_email_address") String customers_email_address,
                                       @Field("customers_password") String customers_password,
                                       @Field("customers_telephone") String customers_telephone,
                                       @Field("customers_picture") String customers_picture);

    @FormUrlEncoded
    @POST("getCoupon")
    Call<CouponsData> getCouponInfo(@Field("code") String code);

    @FormUrlEncoded
    @POST("addShippingAddress")
    Call<AddressData> addUserAddress(@Field("customers_id") String customers_id,
                                     @Field("entry_firstname") String entry_firstname,
                                     @Field("entry_lastname") String entry_lastname,
                                     @Field("entry_street_address") String entry_street_address,
                                     @Field("entry_postcode") String entry_postcode,
                                     @Field("entry_city") String entry_city,
                                     @Field("entry_country_id") String entry_country_id,
                                     @Field("entry_zone_id") String entry_zone_id,
                                     @Field("customers_default_address_id") String customers_default_address_id);

    @FormUrlEncoded
    @POST("updateShippingAddress")
    Call<AddressData> updateUserAddress(@Field("customers_id") String customers_id,
                                        @Field("address_id") String address_id,
                                        @Field("entry_firstname") String entry_firstname,
                                        @Field("entry_lastname") String entry_lastname,
                                        @Field("entry_street_address") String entry_street_address,
                                        @Field("entry_postcode") String entry_postcode,
                                        @Field("entry_city") String entry_city,
                                        @Field("entry_country_id") String entry_country_id,
                                        @Field("entry_zone_id") String entry_zone_id,
                                        @Field("customers_default_address_id") String customers_default_address_id);

    @FormUrlEncoded
    @POST("getAllAddress")
    Call<AddressData> getAllAddress(@Field("customers_id") String customers_id);

    @FormUrlEncoded
    @POST("updateDefaultAddress")
    Call<AddressData> updateDefaultAddress(@Field("customers_id") String customers_id,
                                           @Field("address_book_id") String address_book_id);

    @FormUrlEncoded
    @POST("updateCustomerInfo")
    Call<UserData> updateCustomerInfo(@Field("customers_id") String customers_id,
                                      @Field("customers_firstname") String customers_firstname,
                                      @Field("customers_lastname") String customers_lastname,
                                      @Field("customers_telephone") String customers_telephone,
                                      @Field("customers_dob") String customers_dob,
                                      @Field("customers_picture") String customers_picture,
                                      @Field("customers_old_picture") String customers_old_picture,
                                      @Field("customers_password") String customers_password);

    @POST("addToOrder")
    Call<OrderData> addToOrder(@Body PostOrder postOrder);

    @POST("getRate")
    Call<ShippingRateData> getShippingMethodsAndTax(
            @Body PostTaxAndShippingData postTaxAndShippingData);

    @FormUrlEncoded
    @POST("getOrders")
    Call<OrderData> getOrders(@Field("customers_id") String customers_id,
                              @Field("language_id") int language_id);

    @GET("getAllCoupon")
    Call<List<infoOfferts>> getAllOferts();

}
