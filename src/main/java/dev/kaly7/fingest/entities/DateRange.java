package dev.kaly7.fingest.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kaly7.fingest.common.validation.StartDateBeforeEndDate;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.time.LocalDate;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@StartDateBeforeEndDate
public class DateRange {

    private static final LocalDate MIN_DATE = LocalDate.of(0, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);

    @Column(name = "start", nullable = false)
    private LocalDate start = MIN_DATE;

    @Column(name = "end", nullable = false)
    private LocalDate end = MAX_DATE;

    @JsonCreator
    public DateRange(@JsonProperty("start") LocalDate start, @JsonProperty("end") LocalDate end) {
        this.start = defaultIfNull(start, MIN_DATE);
        this.end = defaultIfNull(end, MAX_DATE);
    }

    public DateRange(String start, String end) {
        this.start = defaultIfEmpty(start, MIN_DATE);
        this.end = defaultIfEmpty(end, MAX_DATE);
    }

    public boolean containsDate(String date) {
        return containsDate(LocalDate.parse(date));
    }

    public boolean containsDate(LocalDate date) {
        return date != null && !date.isBefore(start) && !date.isAfter(end);
    }

    public static DateRange withoutBounds() {
        return new DateRange((LocalDate) null, null);
    }

    private static LocalDate defaultIfNull(LocalDate date, LocalDate defaultValue) {
        return date != null ? date : defaultValue;
    }

    private static LocalDate defaultIfEmpty(String date, LocalDate defaultValue) {
        return (date == null || date.isBlank()) ? defaultValue : LocalDate.parse(date);
    }

}
