package parser.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import parser.model.salarie.Salarie;

public class Entite extends StructureOrganisationnelle {

    @Getter
    @Setter
    private List<Groupe> groupes;
    @Getter
    @Setter
    private List<Salarie> salaries;

    public Entite() {
        super(TYPE.Entite);
        this.groupes = new ArrayList<>();
        this.salaries = new ArrayList<>();
    }

    @Override
    public void addChild(StructureOrganisationnelle so) {
        if (so instanceof Groupe) {
            this.groupes.add((Groupe) so);
        } else {
            throw new RuntimeException("Erreur interne");
        }
    }

    @Override
    public void addSalaries(List<Salarie> salaries) {
        if (salaries != null) {
            this.salaries.addAll(salaries);
        }
    }

}
