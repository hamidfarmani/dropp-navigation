package model.entity.persistent;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 2/3/2017.
 */

@NamedQueries({

        @NamedQuery(name = "device.find.byUniqueID", query = "select d from device d where d.uniqueID=:uniqueID"),
        @NamedQuery(name = "device.isExist.byUniqueID", query = "select d.id from device d where d.uniqueID=:uniqueID"),
        @NamedQuery(name = "device.all", query = "select d from device d")

})

@Entity(name = "device")
@Table(name = "DEVICE" ,indexes = {
        @Index(name = "UNIQUE_ID_INDEX",columnList ="UNIQUE_ID")
})
public class Device implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "DeviceGen", sequenceName = "DeviceSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DeviceGen")
    private Long id;

    @Basic
    @Column(name = "MANUFACTURER", columnDefinition = "VARCHAR2(10)")
    private String manufacturer;

    @Basic
    @Column(name = "MODEL", columnDefinition = "VARCHAR2(20)")
    private String Model;

    @Basic
    @Column(name = "OS", columnDefinition = "VARCHAR2(10)")
    private String OS;

    @Basic
    @Column(name = "OS_VERSION", columnDefinition = "VARCHAR2(6)")
    private String OSVersion;

    @Basic
    @Column(name = "UNIQUE_ID", columnDefinition = "VARCHAR2(36)")
    private String uniqueID;

    @Basic
    @Column(name = "FCM_TOKEN", columnDefinition = "VARCHAR2(512)")
    private String FCMToken;


    public Device() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getOSVersion() {
        return OSVersion;
    }

    public void setOSVersion(String OSVersion) {
        this.OSVersion = OSVersion;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String GCMKey) {
        this.FCMToken = GCMKey;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }


}
