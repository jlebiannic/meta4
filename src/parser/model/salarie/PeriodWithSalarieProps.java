package parser.model.salarie;


import lombok.Data;
import lombok.EqualsAndHashCode;
import parser.model.common.Period;

@Data
@EqualsAndHashCode(callSuper = true)
public class PeriodWithSalarieProps extends Period {

    private ParametrageSalarie values;

    public PeriodWithSalarieProps(Integer startDate, Integer endDate) {
        super(startDate, endDate);
    }

    @Override
    public Period clonePeriod() {
        PeriodWithSalarieProps periodWithProps = new PeriodWithSalarieProps(this.startDate, this.endDate);
        periodWithProps.values = this.values;
        return periodWithProps;
    }

}
