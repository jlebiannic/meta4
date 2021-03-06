package parser.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import parser.model.salarie.Salarie;

public class Groupe extends StructureOrganisationnelle {

    @Getter
    @Setter
    private List<Salarie> salaries;

    public Groupe() {
        super(TYPE.Groupe);
        this.salaries = new ArrayList<>();
    }

    @Override
    public void addChild(StructureOrganisationnelle so) {
        throw new RuntimeException("Erreur interne");
    }

    @Override
    public void addSalaries(List<Salarie> salaries) {
        if (salaries != null) {
            this.salaries.addAll(salaries);
        }
    }
}
