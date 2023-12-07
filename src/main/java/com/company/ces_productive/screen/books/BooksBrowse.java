package com.company.ces_productive.screen.books;

import com.company.ces_productive.entity.BookStatus;
import com.company.ces_productive.entity.User;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Books;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("CES_Books.browse")
@UiDescriptor("books-browse.xml")
@LookupComponent("booksesTable")
public class BooksBrowse extends StandardLookup<Books> {
    @Autowired
    private CollectionLoader<Books> booksesDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        booksesDl.setParameter("currBranch", ((User) currentAuthentication.getUser()).getBranch());
        booksesDl.setParameter("status", BookStatus.IN_BRANCH);
        booksesDl.load();
    }


}