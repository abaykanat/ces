package com.company.ces_productive.screen.books;

import com.company.ces_productive.app.BookNumbGenerate;
import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.DateField;
import io.jmix.ui.component.EntityPicker;
import io.jmix.ui.component.TextField;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

@UiController("CES_Books.accept")
@UiDescriptor("books-Accept.xml")
@EditedEntityContainer("booksDc")
public class BooksAccept extends StandardEditor<Books> {
    @Autowired
    private BookNumbGenerate bookNumbGenerate;
    @Autowired
    private TextField<Integer> bookCount;
    @Autowired
    private EntityPicker<BookType> bookTypeField;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DateField<LocalDate> bookAcceptDateField;
    @Autowired
    private EntityPicker<User> bookAcceptUserField;
    @Autowired
    private ComboBox<BookStatus> bookStatusField;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        bookStatusField.setValue(BookStatus.IN_STOCK);
        bookAcceptDateField.setValue(LocalDate.now());
        bookAcceptUserField.setValue((User) currentAuthentication.getUser());
    }

    @Subscribe("acceptBooks")
    public void onAcceptBooks(Action.ActionPerformedEvent event) {
        Integer bookCountValue = bookCount.getValue();
        BookType bookType = bookTypeField.getValue();
        String bookTypeCode = bookTypeField.getValue().getBookTypeCode();
        int i = 1;
        if (bookCountValue != null) {
            while (i <= bookCountValue) {
                Books books = dataManager.create(Books.class);
                String bookNum = bookNumbGenerate.getNextNumber(bookTypeCode, i, LocalDate.now());
                books.setBookNumber(bookNum);
                books.setBookType(bookType);
                books.setBookStatus(BookStatus.IN_STOCK);
                books.setBookAcceptUser((User) currentAuthentication.getUser());
                books.setBookAcceptBranch(((User) currentAuthentication.getUser()).getBranch());
                books.setBookAcceptDate(LocalDate.now());
                books.setBookSendUser((User) currentAuthentication.getUser());
                books.setBookSendBranch(((User) currentAuthentication.getUser()).getBranch());
                dataManager.save(books);
                i++;
            }
        }
        closeWithDiscard();
    }
}