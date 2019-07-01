package parser.util;

import java.util.ArrayList;
import java.util.List;

import parser.model.common.Period;

public final class PeriodsUtil {

    private PeriodsUtil() {
    }

    public static Period addPeriod(List<Period> periods, Integer startDate, Integer endDate) {
        return addPeriod(periods, startDate, endDate, true);
    }

    public static Period removePeriod(List<Period> periods, Integer startDate, Integer endDate) {
        return addPeriod(periods, startDate, endDate, false);
    }

    private static Period addPeriod(List<Period> periods, Integer startDate, Integer optionnalEndDate, boolean isPresent) {
        Integer endDate = optionnalEndDate != null ? optionnalEndDate : DateUtil.INFINITE_DATE;
        // Attention : l'ordre est important
        List<Period> periodsToRemove = new ArrayList<>();
        periods.stream()
                .filter(
                        p -> startDate <= p.getStartDate() && p.getStartDate() <= endDate)
                .forEach(p -> {
                    if (endDate < p.getEndDate()) {
                        // couvert partiellement par la gauche
                        Integer adaptedStartDate = DateUtil.addDay(endDate, 1);
                        p.setStartDate(adaptedStartDate != -1 ? adaptedStartDate : p.getStartDate());
                    } else {
                        // couvert complétement => suppression
                        periodsToRemove.add(p);
                    }
                });
        periods.removeAll(periodsToRemove);
        periods.stream()
                .filter(p -> startDate <= p.getEndDate() && p.getEndDate() <= endDate)
                .forEach(p -> {
                    // couvert partiellement par la droite (si complétement couvert => déjà retiré)
                    Integer adaptedEndDate = DateUtil.addDay(startDate, -1);
                    p.setEndDate(adaptedEndDate != -1 ? adaptedEndDate : p.getEndDate());
                });
        List<Period> periodsToAdd = new ArrayList<>();
        periods.stream()
                .filter(p -> p.getStartDate() < startDate && endDate < p.getEndDate())
                .forEach(p -> {
                    // inclut (attention ordre important getEndDate avant setEndDate)
                    periodsToAdd.add(
                            Period.builder()
                                    .startDate(DateUtil.addDay(endDate, 1))
                                    .endDate(p.getEndDate()).build());
                    p.setEndDate(DateUtil.addDay(startDate, -1));

                });
        periods.addAll(periodsToAdd);
        // Nouvelle période
        Period newPeriod = null;
        if (isPresent) {
            newPeriod = Period.builder().startDate(startDate).endDate(endDate).build();
            periods.add(newPeriod);
        }
        return newPeriod;
    }

}
