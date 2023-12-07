package com.company.ces_productive.screen.books;

import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UiController("CES_Books.AcceptBranch")
@UiDescriptor("books-AcceptBranch.xml")
@EditedEntityContainer("booksDc")
public class BooksAcceptBranch extends StandardEditor<Books> {
    @Autowired
    private EntityPicker<Branches> bookAcceptBranchField;
    @Autowired
    private DateField<LocalDate> bookAcceptDateField;
    @Autowired
    private EntityPicker<User> bookAcceptUserField;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private TextField<Long> bookCount;
    @Autowired
    private EntityPicker<User> bookSendUserField;
    @Autowired
    private DateField<LocalDate> bookSendDateField;
    @Autowired
    private EntityComboBox<BookStatus> bookStatusField;
    @Autowired
    private EntityComboBox<BookType> bookTypeField;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Notifications notifications;
    @Autowired
    private CollectionLoader<Books> booksesDl;
    @Autowired
    private CollectionContainer<Books> booksesDc;
    @Autowired
    private CollectionLoader<BookType> bookTypesDl;
    @Autowired
    private CollectionLoader<Branches> branchesesDl;
    @Autowired
    private EntityComboBox<Branches> bookSendBranchField;

    public List<Branches> getBranch() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b where b = :currBranch")
                .parameter("currBranch", ((User) currentAuthentication.getUser()).getBranch())
                .list();
    }

    public List<BookType> getBookType() {
        return dataManager.load(BookType.class)
                .query("select b.bookType from CES_Books b where b.bookSendBranch = :currBranch")
                .parameter("currBranch", ((User) currentAuthentication.getUser()).getBranch())
                .list();
    }

    public List<Branches> getBranchCode() {
        return dataManager.load(Branches.class)
                .query("select b.bookAcceptBranch from CES_Books b where b.bookSendBranch = :currBranch")
                .parameter("currBranch", ((User) currentAuthentication.getUser()).getBranch())
                .list();
    }

    public List<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return roles;
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        booksesDl.setParameter("currBookBranch", getBranch());
        booksesDl.load();
        bookTypesDl.setParameter("currBookType", getBookType());
        bookTypesDl.load();
        branchesesDl.setParameter("currBookBranch", getBranchCode());
        branchesesDl.load();
        bookAcceptBranchField.setValue(((User) currentAuthentication.getUser()).getBranch());
        bookAcceptDateField.setValue(LocalDate.now());
        bookAcceptUserField.setValue((User) currentAuthentication.getUser());
    }

    @Subscribe("bookTypeField")
    public void onBookTypeFieldValueChange(HasValue.ValueChangeEvent<BookType> event) {
        bookSendBranchField.setEnabled(true);
        bookStatusField.setValue(BookStatus.IN_TRANSIT);
    }

    @Subscribe("bookSendBranchField")
    public void onBookSendBranchFieldValueChange(HasValue.ValueChangeEvent<Branches> event) {
        Long allCount = booksesDc.getItems().stream()
                .filter(b -> (Objects.equals(Objects.requireNonNull(bookTypeField.getValue()).getBookTypeCode(), b.getBookType().getBookTypeCode())))
                .filter(b -> (Objects.equals(Objects.requireNonNull(bookAcceptBranchField.getValue()).getBranchCode(), b.getBookSendBranch().getBranchCode())))
                .count();
        bookCount.setValue(allCount);
        Books sendBooks = booksesDc.getItems().stream()
                .filter(b -> (Objects.equals(Objects.requireNonNull(bookTypeField.getValue()).getBookTypeCode(), b.getBookType().getBookTypeCode())))
                .filter(b -> (Objects.equals(Objects.requireNonNull(bookAcceptBranchField.getValue()).getBranchCode(), b.getBookSendBranch().getBranchCode())))
                .findFirst()
                .orElse(null);
        if (sendBooks != null) {
            bookSendUserField.setValue(sendBooks.getBookSendUser());
            bookSendDateField.setValue(sendBooks.getBookSendDate());
        }
    }

    @Subscribe("acceptBooks")
    public void onAcceptBooks(Action.ActionPerformedEvent event) {
        List<Books> booksList = booksesDc.getItems();
        if (bookCount.getValue() != null ) {
            BookType bookType = bookTypeField.getValue();
            List<Books> allBooks = new ArrayList<>();
            for (Books bookList : booksList) {
                if (bookList.getBookType() == bookType && bookList.getBookStatus() == BookStatus.IN_TRANSIT
                && Objects.equals(Objects.requireNonNull(bookSendBranchField.getValue()).getBranchCode(), bookList.getBookAcceptBranch().getBranchCode())) {
                    allBooks.add(bookList);
                }
            }
            allBooks.sort(Comparator.comparing(Books::getBookNumber));
            for (int i = 0; i < bookCount.getValue(); i++) {
                Books books = allBooks.get(i);
                if (getUserRoles().contains("manager")) {
                    books.setBookStatus(BookStatus.IN_BRANCH);
                } else {
                    books.setBookStatus(BookStatus.IN_STOCK);
                }
                books.setBookAcceptUser((User) currentAuthentication.getUser());
                books.setBookAcceptBranch(bookAcceptBranchField.getValue());
                books.setBookAcceptDate(LocalDate.now());
                dataManager.save(books);
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Fill Dates")
                    .withDescription("Select not null book type")
                    .show();
            return;
        }
        closeWithDiscard();
    }
}