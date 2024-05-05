package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_STUDENTS", indexes = {
        @Index(name = "IDX_CES_STUDENTS_STUD_PARENT", columnList = "STUD_PARENT_ID"),
        @Index(name = "IDX_CES_STUDENTS_STUD_BRANCH", columnList = "STUD_BRANCH_ID"),
        @Index(name = "IDX_CES_STUDENTS_STUD_MANAGER", columnList = "STUD_MANAGER_ID"),
        @Index(name = "IDX_CES_STUDENTS_STUD_DISCOUNT_REASON", columnList = "STUD_DISCOUNT_REASON_ID")
}, uniqueConstraints = {
        @UniqueConstraint(name = "IDX_CES_STUDENTS_UNQ", columnNames = {"STUD_IIN"})
})
@Entity(name = "CES_Students")
public class Students {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "STUD_ID", nullable = false)
    @NotNull
    private String studID;

    @Column(name = "STUD_OLD_ID")
    private Long studOldId;

    @Column(name = "STUD_IIN", length = 12)
    private String studIIN;

    @Column(name = "STUD_FIRST_NAME", nullable = false, length = 50)
    @NotNull
    private String studFirstName;

    @NotNull
    @Column(name = "STUD_LAST_NAME", nullable = false, length = 100)
    private String studLastName;

    @Column(name = "STUD_MIDDLE_NAME", length = 100)
    private String studMiddleName;

    @Column(name = "STUD_STATUS", nullable = false)
    @NotNull
    private String studStatus;

    @NotNull
    @Column(name = "STUD_MOBLIE_NUMBER", nullable = false, length = 25)
    private String studMoblieNumber;

    @Email(message = "{msg://com.company.ces_productive.entity/Students.studEmail.validation.Email}")
    @Column(name = "STUD_EMAIL")
    private String studEmail;

    @Column(name = "STUD_DATE_OF_BIRTH", nullable = false)
    @NotNull
    private LocalDate studDateOfBirth;

    @NotNull
    @Column(name = "STUD_SEX", nullable = false)
    private String studSex;

    @Column(name = "STUD_ENG_LEVEL")
    private String studEngLevel;

    @Column(name = "STUD_PERIOD_DESC", length = 500)
    private String studPeriodDesc;

    @Column(name = "STUD_DESCRIPTION", length = 5000)
    private String studDescription;

    @NotNull
    @Column(name = "STUD_BEGIN_DATE", nullable = false)
    private LocalDate studBeginDate;

    @Column(name = "STUD_DISCOUNT", precision = 19, scale = 2)
    private BigDecimal studDiscount;

    @JoinColumn(name = "STUD_PARENT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Parents studParent;

    @JoinTable(name = "CES_GROUPS_STUDENTS_LINK",
            joinColumns = @JoinColumn(name = "STUDENTS_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "GROUPS_ID", referencedColumnName = "ID"))
    @ManyToMany
    private List<Groups> studGroups;

    @OneToMany(mappedBy = "visitStudent")
    private List<Visits> studVisits;

    @OneToMany(mappedBy = "orderStudent")
    private List<Orders> studOrders;

    @OneToMany(mappedBy = "payStudent")
    private List<Payments> studPayments;

    @OneToMany(mappedBy = "payParamStudent")
    private List<PaymentParam> studPayParam;

    @OneToMany(mappedBy = "bookStudent")
    private List<Books> studBooks;

    @OneToMany(mappedBy = "freezeStudent")
    private List<Freeze> studFreeze;

    @JoinColumn(name = "STUD_BRANCH_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Branches studBranch;

    @JoinColumn(name = "STUD_MANAGER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User studManager;

    @Column(name = "STUD_ACTUAL_AMOUNT", precision = 19, scale = 2)
    private BigDecimal studActualAmount;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @JoinColumn(name = "STUD_DISCOUNT_REASON_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private DiscountReason studDiscountReason;

    @NotNull
    @Column(name = "STUD_ORDER_PERIOD", nullable = false)
    private LocalDate studOrderPeriod;

    public List<Freeze> getStudFreeze() {
        return studFreeze;
    }

    public void setStudFreeze(List<Freeze> studFreeze) {
        this.studFreeze = studFreeze;
    }

    public List<Payments> getStudPayments() {
        return studPayments;
    }

    public void setStudPayments(List<Payments> studPayments) {
        this.studPayments = studPayments;
    }

    public void setStudPeriodDesc(String studPeriodDesc) {
        this.studPeriodDesc = studPeriodDesc;
    }

    public String getStudPeriodDesc() {
        return studPeriodDesc;
    }

    public List<PaymentParam> getStudPayParam() {
        return studPayParam;
    }

    public void setStudPayParam(List<PaymentParam> studPayParam) {
        this.studPayParam = studPayParam;
    }

    public String getStudDescription() {
        return studDescription;
    }

    public void setStudDescription(String studDescription) {
        this.studDescription = studDescription;
    }

    public List<Visits> getStudVisits() {
        return studVisits;
    }

    public void setStudVisits(List<Visits> studVisits) {
        this.studVisits = studVisits;
    }

    public void setStudOldId(Long studOldId) {
        this.studOldId = studOldId;
    }

    public Long getStudOldId() {
        return studOldId;
    }

    public Long getStudOldID() {
        return studOldId;
    }

    public void setStudOldID(Long studOldID) {
        this.studOldId = studOldID;
    }

    public void setStudID(String studID) {
        this.studID = studID;
    }

    public String getStudID() {
        return studID;
    }

    public void setStudIIN(String studIIN) {
        this.studIIN = studIIN;
    }

    public String getStudIIN() {
        return studIIN;
    }

    public DiscountReason getStudDiscountReason() {
        return studDiscountReason;
    }

    public void setStudDiscountReason(DiscountReason studDiscountReason) {
        this.studDiscountReason = studDiscountReason;
    }

    public LocalDate getStudOrderPeriod() {
        return studOrderPeriod;
    }

    public void setStudOrderPeriod(LocalDate studOrderPeriod) {
        this.studOrderPeriod = studOrderPeriod;
    }

    public BigDecimal getStudDiscount() {
        return studDiscount;
    }

    public void setStudDiscount(BigDecimal studDiscount) {
        this.studDiscount = studDiscount;
    }

    public void setStudOrders(List<Orders> studOrders) {
        this.studOrders = studOrders;
    }

    public List<Orders> getStudOrders() {
        return studOrders;
    }

    public void setStudBooks(List<Books> studBooks) {
        this.studBooks = studBooks;
    }

    public List<Books> getStudBooks() {
        return studBooks;
    }

    public void setStudSex(StudentSex studSex) {
        this.studSex = studSex == null ? null : studSex.getId();
    }

    public StudentSex getStudSex() {
        return studSex == null ? null : StudentSex.fromId(studSex);
    }

    public void setStudStatus(StudentStatus studStatus) {
        this.studStatus = studStatus == null ? null : studStatus.getId();
    }

    public StudentStatus getStudStatus() {
        return studStatus == null ? null : StudentStatus.fromId(studStatus);
    }

    public void setStudEngLevel(StudentLevel studEngLevel) {
        this.studEngLevel = studEngLevel == null ? null : studEngLevel.getId();
    }

    public StudentLevel getStudEngLevel() {
        return studEngLevel == null ? null : StudentLevel.fromId(studEngLevel);
    }

    public BigDecimal getStudActualAmount() {
        return studActualAmount;
    }

    public void setStudActualAmount(BigDecimal studActualAmount) {
        this.studActualAmount = studActualAmount;
    }

    public User getStudManager() {
        return studManager;
    }

    public void setStudManager(User studManager) {
        this.studManager = studManager;
    }

    public void setStudBeginDate(LocalDate studBeginDate) {
        this.studBeginDate = studBeginDate;
    }

    public LocalDate getStudBeginDate() {
        return studBeginDate;
    }

    public List<Groups> getStudGroups() {
        return studGroups;
    }

    public void setStudGroups(List<Groups> studGroups) {
        this.studGroups = studGroups;
    }

    public Branches getStudBranch() {
        return studBranch;
    }

    public void setStudBranch(Branches studBranch) {
        this.studBranch = studBranch;
    }

    public Parents getStudParent() {
        return studParent;
    }

    public void setStudParent(Parents studParent) {
        this.studParent = studParent;
    }

    public LocalDate getStudDateOfBirth() {
        return studDateOfBirth;
    }

    public void setStudDateOfBirth(LocalDate studDateOfBirth) {
        this.studDateOfBirth = studDateOfBirth;
    }

    public String getStudEmail() {
        return studEmail;
    }

    public void setStudEmail(String studEmail) {
        this.studEmail = studEmail;
    }

    public String getStudMoblieNumber() {
        return studMoblieNumber;
    }

    public void setStudMoblieNumber(String studMoblieNumber) {
        this.studMoblieNumber = studMoblieNumber;
    }

    public String getStudMiddleName() {
        return studMiddleName;
    }

    public void setStudMiddleName(String studMiddleName) {
        this.studMiddleName = studMiddleName;
    }

    public String getStudLastName() {
        return studLastName;
    }

    public void setStudLastName(String studLastName) {
        this.studLastName = studLastName;
    }

    public String getStudFirstName() {
        return studFirstName;
    }

    public void setStudFirstName(String studFirstName) {
        this.studFirstName = studFirstName;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @InstanceName
    @DependsOnProperties({"studFirstName", "studLastName"})
    public String getInstanceName() {
        return String.format("%s %s", studFirstName, studLastName);
    }
}