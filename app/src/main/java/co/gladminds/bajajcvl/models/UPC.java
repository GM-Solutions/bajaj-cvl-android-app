package co.gladminds.bajajcvl.models;

/**
 * Created by Nikhil on 01-12-2017.
 */

public class UPC {
    private String code;
    private int point;
    private int status;
    private String message;
    private boolean isShowingProgress;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }


    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShowingProgress() {
        return isShowingProgress;
    }

    public void setShowingProgress(boolean showingProgress) {
        isShowingProgress = showingProgress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UPC upc = (UPC) o;

        return code.equals(upc.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "UPC{" +
                "code='" + code + '\'' +
                ", point=" + point +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
