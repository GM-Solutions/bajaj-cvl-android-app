package co.gladminds.bajajcvl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nikhil on 14-02-2018.
 */

public class Product {
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("points")
    @Expose
    private String points;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("point_req_no")
    @Expose
    private int pointReqNo;
    @SerializedName("target")
    @Expose
    private String target;
    @SerializedName("point_collect")
    @Expose
    private int pointCollect;


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPointReqNo() {
        return pointReqNo;
    }

    public void setPointReqNo(int pointReqNo) {
        this.pointReqNo = pointReqNo;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getPointCollect() {
        return pointCollect;
    }

    public void setPointCollect(int pointCollect) {
        this.pointCollect = pointCollect;
    }
}
