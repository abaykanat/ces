package com.company.ces_productive.screen.books;

import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UiController("CES_Books.move")
@UiDescriptor("books-move.xml")
@EditedEntityContainer("booksDc")
public class BooksMove extends StandardEditor<Books> {

    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DateField<LocalDate> bookSendDateField;
    @Autowired
    private TextField<Long> bookAllCountField;
    @Autowired
    private TextField<Long> bookSendCountField;
    @Autowired
    private EntityComboBox<BookType> bookTypeField;
    @Autowired
    private EntityComboBox<Branches> bookSendBranchField;
    @Autowired
    private CollectionContainer<Books> booksesDc;
    @Autowired
    private Notifications notifications;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private EntityPicker<User> bookSendUserField;

    public List<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return roles;
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        bookSendDateField.setValue(LocalDate.now());
        bookSendUserField.setValue((User) currentAuthentication.getUser());
    }

    @Subscribe("bookTypeField")
    public void onBookTypeFieldValueChange(HasValue.ValueChangeEvent<BookType> event) {
        if (getUserRoles().contains("manager")) {
            Long allCount = booksesDc.getItems().stream()
                    .filter(b -> (event.getValue() == b.getBookType()))
                    .filter(b -> (b.getBookStatus() == BookStatus.IN_BRANCH))
                    .count();
            bookAllCountField.setValue(allCount);
        } else {
            Long allCount = booksesDc.getItems().stream()
                    .filter(b -> (event.getValue() == b.getBookType()))
                    .filter(b -> (b.getBookStatus() == BookStatus.IN_STOCK))
                    .count();
            bookAllCountField.setValue(allCount);
        }
    }

    @Subscribe("moveBookBtn")
    public void onMoveBookBtnClick(Button.ClickEvent event) {
        List<Books> booksList = booksesDc.getItems();
        if (bookSendCountField.getValue() != null && bookSendCountField.getValue() <= bookAllCountField.getValue()) {
            BookType bookType = bookTypeField.getValue();
            List<Books> allBooks = new ArrayList<>();
            for (Books bookList : booksList) {
                if (getUserRoles().contains("manager")) {
                    if (bookList.getBookType() == bookType && bookList.getBookStatus() == BookStatus.IN_BRANCH) {
                        allBooks.add(bookList);
                    }
                } else {
                    if (bookList.getBookType() == bookType && bookList.getBookStatus() == BookStatus.IN_STOCK) {
                        allBooks.add(bookList);
                    }
                }
            }
            allBooks.sort(Comparator.comparing(Books::getBookNumber));
            for (int i = 0; i < bookSendCountField.getValue(); i++) {
                Books books = allBooks.get(i);
                books.setBookStatus(BookStatus.IN_TRANSIT);
                books.setBookSendUser((User) currentAuthentication.getUser());
                books.setBookSendBranch(bookSendBranchField.getValue());
                books.setBookSendDate(LocalDate.now());
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