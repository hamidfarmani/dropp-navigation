package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 6/22/2017.
 */
@NamedQueries({
        @NamedQuery(name = "systemSetting.all", query = "select s from systemSetting s"),
})
@Table(name = "SYSTEM_SETTING")
@Entity(name = "systemSetting")
public class SystemSetting implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "settingGen", sequenceName = "settingSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "settingGen")
    private long id;

    @Basic
    @Column(name = "SS_STATE", columnDefinition = "CHAR")//sms sender state
    private Boolean smsSenderState;

    @Basic
    @Column(name = "ES_STATE", columnDefinition = "CHAR") //email sender state
    private Boolean emailSenderState;

    @Basic
    @Column(name = "DSRS_STATE", columnDefinition = "CHAR")//daily sms report state
    private Boolean smsDailyReportState;

    @Basic
    @Column(name = "WSRS_STATE", columnDefinition = "CHAR")//weekly sms report state
    private Boolean smsWeeklyReportState;

    @Basic
    @Column(name = "DERS_STATE", columnDefinition = "CHAR") //daily email report state
    private Boolean emailDailyReportState;

    @Basic
    @Column(name = "WERS_STATE", columnDefinition = "CHAR") //weekly email report state
    private Boolean emailWeeklyReportState;

    @Basic
    @Column(name = "MERS_STATE", columnDefinition = "CHAR")//monthly email report state
    private Boolean emailMonthlyReportState;

    @Basic
    @Column(name = "EOSS_STATE", columnDefinition = "CHAR")//exception occurrence sms sender
    private Boolean smsExceptionOccurrenceState;

    @Basic
    @Column(name = "EOES_STATE", columnDefinition = "CHAR")//exception occurrence email sender
    private Boolean emailExceptionOccurrenceState;


    @Basic
    @Column(name = "IOS_UPDATE", columnDefinition = "CHAR")
    private Boolean iosUpdate;

    @Basic
    @Column(name = "CRITICAL_IOS_UPDATE", columnDefinition = "CHAR")
    private Boolean criticalIosUpdate;


    @Basic
    @Column(name = "ANDROID_UPDATE", columnDefinition = "CHAR")
    private Boolean androidUpdate;


    @Basic
    @Column(name = "CRITICAL_ANDROID_UPDATE", columnDefinition = "CHAR")
    private Boolean criticalAndroidUpdate;

    @Basic
    @Column(name = "STIS_STATE", columnDefinition = "CHAR")//sms trip info state
    private Boolean smsTripInfoState;

    @Basic
    @Column(name = "ALLOW_COMPETITORS", columnDefinition = "CHAR")
    private Boolean allowCompetitors;


    public SystemSetting() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getSmsSenderState() {
        return smsSenderState;
    }

    public void setSmsSenderState(Boolean smsSender) {
        this.smsSenderState = smsSender;
    }

    public Boolean getEmailSenderState() {
        return emailSenderState;
    }

    public void setEmailSenderState(Boolean emailSender) {
        this.emailSenderState = emailSender;
    }

    public Boolean getSmsDailyReportState() {
        return smsDailyReportState;
    }

    public void setSmsDailyReportState(Boolean smsDailyReportState) {
        this.smsDailyReportState = smsDailyReportState;
    }

    public Boolean getSmsWeeklyReportState() {
        return smsWeeklyReportState;
    }

    public void setSmsWeeklyReportState(Boolean smsWeeklyReportState) {
        this.smsWeeklyReportState = smsWeeklyReportState;
    }

    public Boolean getEmailDailyReportState() {
        return emailDailyReportState;
    }

    public void setEmailDailyReportState(Boolean emailDailyReportState) {
        this.emailDailyReportState = emailDailyReportState;
    }

    public Boolean getEmailWeeklyReportState() {
        return emailWeeklyReportState;
    }

    public void setEmailWeeklyReportState(Boolean emailWeeklyReportState) {
        this.emailWeeklyReportState = emailWeeklyReportState;
    }

    public Boolean getEmailMonthlyReportState() {
        return emailMonthlyReportState;
    }

    public void setEmailMonthlyReportState(Boolean emailMonthlyReportState) {
        this.emailMonthlyReportState = emailMonthlyReportState;
    }

    public Boolean getSmsExceptionOccurrenceState() {
        return smsExceptionOccurrenceState;
    }

    public void setSmsExceptionOccurrenceState(Boolean smsExceptionOccurrenceState) {
        this.smsExceptionOccurrenceState = smsExceptionOccurrenceState;
    }

    public Boolean getEmailExceptionOccurrenceState() {
        return emailExceptionOccurrenceState;
    }

    public void setEmailExceptionOccurrenceState(Boolean emailExceptionOccurrenceState) {
        this.emailExceptionOccurrenceState = emailExceptionOccurrenceState;
    }

    public Boolean getIosUpdate() {
        return iosUpdate;
    }

    public void setIosUpdate(Boolean iosUpdate) {
        this.iosUpdate = iosUpdate;
    }

    public Boolean getCriticalIosUpdate() {
        return criticalIosUpdate;
    }

    public void setCriticalIosUpdate(Boolean criticalIosUpdate) {
        this.criticalIosUpdate = criticalIosUpdate;
    }

    public Boolean getAndroidUpdate() {
        return androidUpdate;
    }

    public void setAndroidUpdate(Boolean androidUpdate) {
        this.androidUpdate = androidUpdate;
    }

    public Boolean getCriticalAndroidUpdate() {
        return criticalAndroidUpdate;
    }

    public void setCriticalAndroidUpdate(Boolean criticalAndroidUpdate) {
        this.criticalAndroidUpdate = criticalAndroidUpdate;
    }

    public Boolean getAllowCompetitors() {
        return allowCompetitors;
    }

    public void setAllowCompetitors(Boolean allowCompetitors) {
        this.allowCompetitors = allowCompetitors;
    }

    public Boolean getSmsTripInfoState() {
        return smsTripInfoState;
    }

    public void setSmsTripInfoState(Boolean smsTripInfoState) {
        this.smsTripInfoState = smsTripInfoState;
    }
}