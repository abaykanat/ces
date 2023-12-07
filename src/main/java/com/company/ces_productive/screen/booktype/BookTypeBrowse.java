package com.company.ces_productive.screen.booktype;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.BookType;

@UiController("CES_BookType.browse")
@UiDescriptor("book-type-browse.xml")
@LookupComponent("table")
public class BookTypeBrowse extends MasterDetailScreen<BookType> {
}