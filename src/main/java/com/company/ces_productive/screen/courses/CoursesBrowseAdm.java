package com.company.ces_productive.screen.courses;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.courses.Courses;

@UiController("CES_Courses.browseAdm")
@UiDescriptor("courses-browseAdm.xml")
@LookupComponent("coursesesTable")
public class CoursesBrowseAdm extends StandardLookup<Courses> {
}