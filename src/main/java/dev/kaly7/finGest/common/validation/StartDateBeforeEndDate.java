package dev.kaly7.finGest.common.validation;

import dev.kaly7.finGest.entities.DateRange;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StartDateBeforeEndDate.StartDateBeforeEndDateValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StartDateBeforeEndDate {

    String message() default "Start date must be before end date.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Component // Register the validator as a Spring bean
    class StartDateBeforeEndDateValidator implements ConstraintValidator<StartDateBeforeEndDate, DateRange> {

        @Override
        public void initialize(StartDateBeforeEndDate startDateBeforeEndDate) {

        }

        @Override
        public boolean isValid(DateRange dateRange, ConstraintValidatorContext context) {

            if (dateRange == null || dateRange.getStart() == null || dateRange.getEnd() == null) {
                return true;
            }

            return dateRange.getStart().isBefore(dateRange.getEnd());
        }
    }
}
