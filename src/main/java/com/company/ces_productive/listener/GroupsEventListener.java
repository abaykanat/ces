package com.company.ces_productive.listener;

import com.company.ces_productive.app.CreateOrder;
import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.event.EntityChangedEvent;
import io.jmix.core.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Component("CESTWO_GroupsEventListener")
public class GroupsEventListener {

    @Autowired
    private CreateOrder createOrder;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private DocNumbGenerate docNumbGenerate;

    private boolean executeCode = true;

    @EventListener
    public void onGroupsChangedBeforeCommit(EntityChangedEvent<Groups> event) {
        if (!executeCode) {
            return;
        }

        Id<Groups> groupId = event.getEntityId();
        Groups newGroup = dataManager.load(groupId).one();
        List<Students> studLists = newGroup.getGroupStudents();

        if (event.getType() == EntityChangedEvent.Type.CREATED) {
            processStudents(studLists, newGroup);
        } else if (event.getType() == EntityChangedEvent.Type.UPDATED && event.getChanges().isChanged("groupStudents")) {
            Collection<Id<Students>> oldStudId = event.getChanges().getOldCollection("groupStudents", Students.class);
            List<Students> oldStudList = oldStudId.stream()
                    .map(studId -> dataManager.load(Students.class).id(studId).one())
                    .toList();
            studLists.removeAll(oldStudList);
            processStudents(studLists, newGroup);
        }
    }

    private void processStudents(List<Students> students, Groups newGroup) {
        for (Students student : students) {
            String branchCode = newGroup.getGroupBranch().getBranchCode();
            String ordNum = docNumbGenerate.getNextNumber("ORD", branchCode);
            Branches branch = student.getStudBranch();
            BigDecimal discount = student.getStudDiscount();
            BigDecimal cost = newGroup.getGroupCost();
            LocalDate period = student.getStudOrderPeriod();
            LocalDate paramPeriod = LocalDate.now();
            student.setStudStatus(StudentStatus.STUDY);
            if (student.getStudActualAmount() == null) {
                student.setStudActualAmount(BigDecimal.ZERO);
            }
            if (discount != null) {
                if (discount.compareTo(BigDecimal.ZERO) != 0) {
                    processDiscountedOrder(student, newGroup, branch, ordNum, discount, cost, period);
                } else
                {
                    processRegularOrder(student, newGroup, branch, ordNum, cost, paramPeriod);
                }
            } else {
                processRegularOrder(student, newGroup, branch, ordNum, cost, period);
            }
            student.setStudOrderPeriod(LocalDate.EPOCH);
            if (student.getStudDiscountReason() == null) {
                student.setStudDiscountReason(null);
                student.setStudDiscount(BigDecimal.ZERO);
            }
            student.setStudDiscount(BigDecimal.ZERO);
            dataManager.save(student);
        }
    }

    private void processDiscountedOrder(Students student, Groups newGroup, Branches branch, String ordNum, BigDecimal discount, BigDecimal cost, LocalDate period) {
        BigDecimal percentAmount = (cost.multiply(discount)).divide(BigDecimal.valueOf(100), RoundingMode.UP);
        BigDecimal finalAmount = cost.subtract(percentAmount);
        DiscountReason reason = student.getStudDiscountReason();
        createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, finalAmount, OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, student, period, newGroup, null);
        createOrder.createNewPayParameter(student, newGroup, period, reason, discount);
    }

    private void processRegularOrder(Students student, Groups newGroup, Branches branch, String ordNum, BigDecimal cost, LocalDate paramPeriod) {
        createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, cost, OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, student, paramPeriod, newGroup, null);
        createOrder.createNewPayParameter(student, newGroup, paramPeriod, null, BigDecimal.ZERO);
    }

    public void setExecuteCode(boolean executeCode) {
        this.executeCode = executeCode;
    }
}