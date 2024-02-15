
package app.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CdnRawObj {

    @SerializedName("_id")
    @Expose
    private String _id;
    @SerializedName("Overflow")
    @Expose
    private String overflow;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("CDN")
    @Expose
    private String cdn;
    @SerializedName("_mby")
    @Expose
    private String _mby;
    @SerializedName("_by")
    @Expose
    private String _by;
    @SerializedName("_modified")
    @Expose
    private Integer _modified;
    @SerializedName("_created")
    @Expose
    private Integer _created;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Scheduled")
    @Expose
    private String scheduled;
    @SerializedName("Duration")
    @Expose
    private String duration;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOverflow() {
        return overflow;
    }

    public void setOverflow(String overflow) {
        this.overflow = overflow;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCdn() {
        return cdn;
    }

    public void setCdn(String cdn) {
        this.cdn = cdn;
    }

    public String get_mby() {
        return _mby;
    }

    public void set_mby(String _mby) {
        this._mby = _mby;
    }

    public String get_by() {
        return _by;
    }

    public void set_by(String _by) {
        this._by = _by;
    }

    public Integer get_modified() {
        return _modified;
    }

    public void set_modified(Integer _modified) {
        this._modified = _modified;
    }

    public Integer get_created() {
        return _created;
    }

    public void set_created(Integer _created) {
        this._created = _created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}
