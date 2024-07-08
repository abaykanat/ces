package com.company.ces_productive.app;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

        List<Orders> paidOrders = student.getStudOrders().stream()
                .filter(order -> order.getOrderGroup() != null
                        && order.getOrderGroup().equals(group)
                        && order.getOrderStatus() == OrderStatus.PAID
                        && !order.getOrderNumber().contains("ORDDIF")
                        && order.getOrderPeriodEnd().isAfter(LocalDate.now().minusMonths(1).minusDays(10)))
                .toList();

        int orderCount = paidOrders.size();
        if (orderCount == 0) {
            return new CalculationResult(BigDecimal.ZERO, 0, "Студент не оплатил за занятия");
        }

        List<PaymentParam> paymentParams = student.getStudPayParam();
        if (!paymentParams.isEmpty()) {
            for (PaymentParam paymentParam : paymentParams) {
                if (paymentParam.getPayParamGroups().getId().equals(group.getId())) {
                    LocalDate payDayDate = paymentParam.getPayParamPayDay();
                    CalculationAmount calcAmountResult = calculateRealAmount(orderCount, group, courses, payDayDate);
                    if (calcAmountResult.getCalcAmount().compareTo(BigDecimal.ZERO) > 0) {
                        return new CalculationResult(calcAmountResult.getCalcAmount(), calcAmountResult.getCalcCount(), "Корректно");
                    } else {
                        return new CalculationResult(BigDecimal.ZERO, 0, calcAmountResult.getCalcReason());
                    }
                }
            }
            return new CalculationResult(BigDecimal.ZERO, 0, "По параметру платежа отсутствует группа");
        }
        return new CalculationResult(BigDecimal.ZERO, 0, "Параметры платежа отсутствуют");
    }

    private static CalculationAmount calculateRealAmount(int orderCount, Groups group, List<Courses> courses, LocalDate baseDate) {
        LocalDate payDate = baseDate.minusMonths(orderCount);
        LocalDate newDate = payDate.plusMonths(1);
        BigDecimal directionAmount = group.getGroupDirection().getDirectionMinCost();
        Optional<Courses> minDateCourses = courses.stream()
                .min(Comparator.comparing(Courses::getCourseStartDate));
        if (minDateCourses.isPresent()) {
            LocalDate minDate = minDateCourses.get().getCourseStartDate().toLocalDate();
            if (!payDate.isBefore(minDate)) {
                return calculateAmountForPeriod(payDate, newDate, directionAmount, courses, "Дата платежа больше даты занятий");
            } else {
                return calculateAmountForPeriod(LocalDate.now(), LocalDate.now().plusMonths(1), directionAmount, courses, "Дата платежа меньше даты занятий");
            }
        } else {
            return new CalculationAmount(BigDecimal.ZERO, 0, "Не найдено актуальное расписание по группе");
        }
    }

    private static CalculationAmount calculateAmountForPeriod(LocalDate startDate, LocalDate endDate, BigDecimal directionAmount, List<Courses> courses, String errorMessage) {
        List<Courses> filteredCourses = courses.stream()
                .filter(course -> !course.getCourseStartDate().isBefore(startDate.atStartOfDay())
                        && course.getCourseStartDate().isBefore(endDate.atStartOfDay()))
                .toList();
        int courseCount = filteredCourses.size();
        if (courseCount == 0) {
            return new CalculationAmount(BigDecimal.ZERO, 0, "Не найдены занятия для расчета стоимости. " + errorMessage);
        }
        BigDecimal calcAmount = directionAmount.divide(BigDecimal.valueOf(courseCount), RoundingMode.UP);
        if (calcAmount.compareTo(BigDecimal.ZERO) == 0) {
            return new CalculationAmount(BigDecimal.ZERO, 0, "Не найдены занятия для расчета стоимости. " + errorMessage);
        }
        return new CalculationAmount(calcAmount, courseCount, "Успешно");
    }
}
