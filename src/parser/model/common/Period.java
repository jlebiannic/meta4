package parser.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Period {
    protected Integer startDate; // YYYYMMDD (inclu)
    protected Integer endDate; // YYYYMMDD (inclu)

    public Period clonePeriod() {
        return Period.builder().startDate(this.startDate).endDate(this.endDate).build();
    }
}
