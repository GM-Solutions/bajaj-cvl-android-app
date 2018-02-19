package co.gladminds.bajajcvl.models;

/**
 * Created by Nikhil on 22-12-2017.
 */

public class AppVersion {
    private int versionCode;
    private String versionMessage;
    private String versionName;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionMessage() {
        return versionMessage;
    }

    public void setVersionMessage(String versionMessage) {
        this.versionMessage = versionMessage;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public String toString() {
        return "AppVersion{" +
                "versionCode=" + versionCode +
                ", versionMessage='" + versionMessage + '\'' +
                ", versionName='" + versionName + '\'' +
                '}';
    }
}
