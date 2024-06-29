package com.company.ces_productive.app;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GetCourseRealAmount {
    public static class CalculationResult {
        private BigDecimal amount;
        private int count;
        private String reason;

        public CalculationResult(BigDecimal amount, int count, String reason) {
            this.amount = amount;
            this.count = count;
            this.reason = reason;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public int getCount() {
            return count;
        }

        public String getReason() {
            return reason;
        }
    }

    public static class CalculationAmount {
        private BigDecimal calcAmount;
        private int calcCount;
        private String calcReason;

        public CalculationAmount(BigDecimal calcAmount, int calcCount, String calcReason) {
            this.calcAmount = calcAmount;
            this.calcCount = calcCount;
            this.calcReason = calcReason;
        }

        public BigDecimal getCalcAmount() {
            return calcAmount;
        }

        public int getCalcCount() {
            return calcCount;
        }

        public String getCalcReason() {
            return calcReason;
        }
    }

    public static CalculationResult getCourseRealAmount(Students student, Groups group, String courseName) {
        List<Courses> courses = group.getGroupCourse().stream()
                .filter(course -> courseName.equals(course.getCourseName()) && course.getCourseStatus().equals(CourseStatus.NEW))
                .toList();
        List<Orders> paidOrders = new ArrayList<>();
        for (Orders orders : student.getStudOrders()) {
            if (orders.getOrderGroup() != null) {
                if (orders.getOrderGroup().equals(group)
                        && orders.getOrderStatus() == OrderStatus.PAID
                        && !orders.getOrderNumber().contains("ORDDIF")
                        && orders.getOrderPeriodEnd().isAfter(LocalDate.now().minusMonths(1).minusDays(10))) {
                    paidOrders.add(orders);
                }
            }
        }
        int orderCount = paidOrders.size();
        List<PaymentParam> paymentParams = student.getStudPayParam();
        if (!paymentParams.isEmpty()) {
            for (PaymentParam paymentParam : paymentParams) {
                LocalDate payDayDate = paymentParam.getPayParamPayDay();
                if (paymentParam.getPayParamGroups().getId().equals(group.getId())) {
                    BigDecimal amount = calculateRealAmount(orderCount, group, courses, payDayDate).getCalcAmount();
                    int count = calculateRealAmount(orderCount, group, courses, payDayDate).getCalcCount();
                    String reason = calculateRealAmount(orderCount, group, courses, payDayDate).getCalcReason();
                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        return new CalculationResult(amount, count, "Корректно");
                    } else {
                        return new CalculationResult(BigDecimal.ZERO, 0, reason);
                    }
                }
            }
            return new CalculationResult(BigDecimal.ZERO, 0, "По параметру платежа отсутствует группа");
        }
        return null;
    }

    private static CalculationAmount calculateRealAmount(int orderCount, Groups group, List<Courses> courses, LocalDate baseDate) {
        LocalDate payDate = baseDate.minusMonths(orderCount);
        LocalDate newDate = payDate.plusMonths(1);
        BigDecimal directionAmount = group.getGroupDirection().getDirectionMinCost();
        Optional<Courses> minDateCourses = courses.stream()
                .min(Comparator.comparing(Courses::getCourseStartDate));
        if (minDateCourses.isPresent()) {
            Courses minDateCourse = minDateCourses.get();
            LocalDate minDate = minDateCourse.getCourseStartDate().toLocalDate();
            if (payDate.equals(minDate) || payDate.isAfter(minDate)) {
                List<Courses> filteredCourses = courses.stream()
                        .filter(course -> (course.getCourseStartDate().isEqual(payDate.atStartOfDay()) || course.getCourseStartDate().isAfter(payDate.atStartOfDay()))
                                && course.getCourseStartDate().isBefore(newDate.atStartOfDay()))
                        .toList();
                int courseCount = filteredCourses.size();
                if (courseCount == 0) {
                    return new CalculationAmount(BigDecimal.ZERO, 0,"Не найдены занятия для расчета стоимости. Дата платежа больше даты занятии");
                }
                BigDecimal calcAmount = directionAmount.divide(BigDecimal.valueOf(courseCount), RoundingMode.UP);
                if (calcAmount.compareTo(BigDecimal.ZERO) == 0) {
                    return new CalculationAmount(BigDecimal.ZERO, 0,"Не найдены занятия для расчета стоимости. Дата платежа больше даты оплаты");
                }
                return new CalculationAmount(calcAmount, courseCount,"Успешно");
            } else if (payDate.isBefore(minDate)) {
                List<Courses> filteredCourses = courses.stream()
                        .filter(course -> (course.getCourseStartDate().isEqual(LocalDate.now().atStartOfDay()) || course.getCourseStartDate().isAfter(LocalDate.now().atStartOfDay()))
                                && course.getCourseStartDate().isBefore(LocalDate.now().atStartOfDay().plusMonths(1)))
                        .toList();
                int courseCount = filteredCourses.size();
                if (courseCount == 0) {
                    return new CalculationAmount(BigDecimal.ZERO, 0,"Не найдены занятия для расчета стоимости. Дата платежа меньше даты занятии");
                }
                BigDecimal calcAmount = directionAmount.divide(BigDecimal.valueOf(courseCount), RoundingMode.UP);
                if (calcAmount.compareTo(BigDecimal.ZERO) == 0) {
                    return new CalculationAmount(BigDecimal.ZERO, 0,"Не найдены занятия для расчета стоимости. Дата платежа меньше даты оплаты");
                }
                return new CalculationAmount(calcAmount, courseCount,"Успешно");
            }
        } else {
            return new CalculationAmount(BigDecimal.ZERO, 0,"Не найдено актуальное расписание по группе");
        }
        return new CalculationAmount(BigDecimal.ZERO, 0,"Другая ошибка расчета");
    }
}
