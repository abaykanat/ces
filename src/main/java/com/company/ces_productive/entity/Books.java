package com.company.ces_productive.entity;

import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_BOOKS", indexes = {
        @Index(name = "IDX_CES_BOOKS_BOOK_ORDER", columnList = "BOOK_ORDER_ID"),
        @Index(name = "IDX_CES_BOOKS_BOOK_TYPE", columnList = "BOOK_TYPE_ID"),
        @Index(name = "IDX_CES_BOOKS_BOOK_BRANCH", columnList = "BOOK_BRANCH_ID"),
        @Index(name = "IDX_CES_BOOKS_BOOK_STUDENT", columnList = "BOOK_STUDENT_ID"),
        @Index(name = "IDX_CES_BOOKS_BOOK_ACCEPT_USER", columnList = "BOOK_ACCEPT_USER_ID"),
        @Index(name = "IDX_CES_BOOKS_BOOK_SOLD_USER", columnList = "BOOK_SOLD_USER_ID"),
        @Index(name = "IDX_CES_BOOKS_BOOK_SEND_USER", columnList = "BOOK_SEND_USER_ID"),
        @Index(name = "IDX_CES_BOOKS_BOOK_SEND_BRANCH", columnList = "BOOK_SEND_BRANCH_ID")
})
@Entity(name = "CES_Books")
public class Books {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @Column(name = "BOOK_NUMBER", nullable = false, length = 50)
    private String bookNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BOOK_TYPE_ID", nullable = false)
    @NotNull
    private BookType bookType;

    @Column(name = "BOOK_STATUS", nullable = false)
    @NotNull
    private String bookStatus;

    @JoinColumn(name = "BOOK_ACCEPT_USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User bookAcceptUser;

    @JoinColumn(name = "BOOK_BRANCH_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Branches bookAcceptBranch;

    @NotNull
    @Column(name = "BOOK_ACCEPT_DATE", nullable = false)
    private LocalDate bookAcceptDate;

    @JoinColumn(name = "BOOK_SEND_USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User bookSendUser;

    @JoinColumn(name = "BOOK_SEND_BRANCH_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Branches bookSendBranch;

    @Column(name = "BOOK_SEND_DATE")
    private LocalDate bookSendDate;

    @JoinColumn(name = "BOOK_SOLD_USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User bookSoldUser;

    @Column(name = "BOOK_SOLD_DATE")
    private LocalDate bookSoldDate;

    @JoinColumn(name = "BOOK_STUDENT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Students bookStudent;

    @JoinColumn(name = "BOOK_ORDER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Orders bookOrder;

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

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;

    public void setBookAcceptDate(LocalDate bookAcceptDate) {
        this.bookAcceptDate = bookAcceptDate;
    }

    public LocalDate getBookAcceptDate() {
        return bookAcceptDate;
    }

    public void setBookSendDate(LocalDate bookSendDate) {
        this.bookSendDate = bookSendDate;
    }

    public LocalDate getBookSendDate() {
        return bookSendDate;
    }

    public void setBookSoldDate(LocalDate bookSoldDate) {
        this.bookSoldDate = bookSoldDate;
    }

    public LocalDate getBookSoldDate() {
        return bookSoldDate;
    }

    public Branches getBookSendBranch() {
        return bookSendBranch;
    }

    public void setBookSendBranch(Branches bookSendBranch) {
        this.bookSendBranch = bookSendBranch;
    }

    public User getBookSendUser() {
        return bookSendUser;
    }

    public void setBookSendUser(User bookSendUser) {
        this.bookSendUser = bookSendUser;
    }

    public User getBookSoldUser() {
        return bookSoldUser;
    }

    public void setBookSoldUser(User bookSoldUser) {
        this.bookSoldUser = bookSoldUser;
    }

    public User getBookAcceptUser() {
        return bookAcceptUser;
    }

    public void setBookAcceptUser(User bookAcceptUser) {
        this.bookAcceptUser = bookAcceptUser;
    }

    public Students getBookStudent() {
        return bookStudent;
    }

    public void setBookStudent(Students bookStudent) {
        this.bookStudent = bookStudent;
    }

    public Branches getBookAcceptBranch() {
        return bookAcceptBranch;
    }

    public void setBookAcceptBranch(Branches bookAcceptBranch) {
        this.bookAcceptBranch = bookAcceptBranch;
    }

    public Orders getBookOrder() {
        return bookOrder;
    }

    public void setBookOrder(Orders bookOrder) {
        this.bookOrder = bookOrder;
    }

    public BookStatus getBookStatus() {
        return bookStatus == null ? null : BookStatus.fromId(bookStatus);
    }

    public void setBookStatus(BookStatus bookStatus) {
        this.bookStatus = bookStatus == null ? null : bookStatus.getId();
    }

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }

    public String getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
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
    @DependsOnProperties({"bookNumber"})
    public String getInstanceName() {
        return String.format("%s", bookNumber);
    }
}