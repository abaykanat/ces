package com.company.ces_productive.screen.books;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Books;

@UiController("CES_Books.edit")
@UiDescriptor("books-edit.xml")
@EditedEntityContainer("booksDc")
public class BooksEdit extends StandardEditor<Books> {
}