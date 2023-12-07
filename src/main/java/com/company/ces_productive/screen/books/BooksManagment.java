package com.company.ces_productive.screen.books;

import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Books;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@UiController("CES_Books.managment")
@UiDescriptor("books-Managment.xml")
@LookupComponent("booksesTable")
public class BooksManagment extends StandardLookup<Books> {

    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private CollectionLoader<Books> booksesDl;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    public List<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return roles;
    }

    public List<Branches> getBranch() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b where b = :currBranch")
                .parameter("currBranch", ((User) currentAuthentication.getUser()).getBranch())
                .list();
    }

    public List<Branches> getBranches() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b")
                .list();
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (getUserRoles().contains("manager")) {
            booksesDl.setParameter("currBookBranch", getBranch());
            booksesDl.load();
        } else {
            booksesDl.setParameter("currBookBranch", getBranches());
            booksesDl.load();
        }
    }

    @Subscribe("booksesTable.createParty")
    public void onBooksesTableCreateParty(Action.ActionPerformedEvent event) {
        Screen bookAcceptEditor = screenBuilders.editor(Books.class, this)
                .newEntity()
                .withScreenClass(BooksAccept.class)
                .withOpenMode(OpenMode.DIALOG)
                .build()
                .show();
        bookAcceptEditor.addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.closedWith(StandardOutcome.DISCARD))  {

                getScreenData().loadAll();
            }
        });
    }

    @Subscribe("booksesTable.moveToBranch")
    public void onBooksesTableMoveToBranch(Action.ActionPerformedEvent event) {
        Screen bookMoveEditor = screenBuilders.editor(Books.class, this)
                .newEntity()
                .withScreenClass(BooksMove.class)
                .withOpenMode(OpenMode.DIALOG)
                .build()
                .show();
        bookMoveEditor.addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.closedWith(StandardOutcome.DISCARD))  {
                getScreenData().loadAll();
            }
        });
    }

    @Subscribe("booksesTable.acceptBranch")
    public void onBooksesTableAcceptBranch(Action.ActionPerformedEvent event) {
        Screen bookAcceptBranchEditor = screenBuilders.editor(Books.class, this)
                .newEntity()
                .withScreenClass(BooksAcceptBranch.class)
                .withOpenMode(OpenMode.DIALOG)
                .build()
                .show();
        bookAcceptBranchEditor.addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.closedWith(StandardOutcome.DISCARD))  {
                getScreenData().loadAll();
            }
        });
    }
}