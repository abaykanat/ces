package com.company.ces_productive.screen.parents;

import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.Students;
import com.company.ces_productive.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.component.data.TableItems;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Parents;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UiController("CES_Parents.edit")
@UiDescriptor("parents-edit.xml")
@EditedEntityContainer("parentsDc")
public class ParentsEdit extends StandardEditor<Parents> {
    @Autowired
    private CollectionLoader<Students> studentsDl;
    @Autowired
    private TextField<Branches> parentBranchField;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private GroupTable<Students> studentsesTable;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private TextField<String> parentIINField;
    @Autowired
    private MaskedField<String> parentMobileNumberField;
    @Autowired
    private TextField<String> parentFirstNameField;
    @Autowired
    private TextField<String> parentLastNameField;
    @Autowired
    private TextField<String> parentMiddleNameField;
    @Autowired
    private TextField<String> parentEmailField;
    @Autowired
    private TextArea<String> parentAddressField;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        Parents parent = getEditedEntity();
        parentBranchField.setValue(((User) currentAuthentication.getUser()).getBranch());
        studentsDl.setParameter("currParents", parent);
        studentsDl.load();
    }

    @Subscribe("commitStudAdd")
    public void onCommitStudAdd(Action.ActionPerformedEvent event) {
        Parents parent = getEditedEntity();
        parent.setParentIIN(parentIINField.getValue());
        parent.setParentMobileNumber(parentMobileNumberField.getValue());
        parent.setParentFirstName(parentFirstNameField.getValue());
        parent.setParentLastName(parentLastNameField.getValue());
        parent.setParentMiddleName(parentMiddleNameField.getValue());
        parent.setParentEmail(parentEmailField.getValue());
        parent.setParentAddress(parentAddressField.getValue());
        dataManager.save(parent);

        TableItems<Students> selectedStudents = studentsesTable.getItems();
        if (selectedStudents != null) {
            List<Students> students = new ArrayList<>(Objects.requireNonNull(selectedStudents).getItems());
            for (Students student : students) {
                student.setStudParent(parent);
                dataManager.save(student);
            }
        }
        closeWithDiscard();
    }
}