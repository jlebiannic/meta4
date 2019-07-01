package parser.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import parser.model.salarie.Salarie;

public class Filiere extends StructureOrganisationnelle {

    @Getter
    @Setter
    private List<Regroupement> regroupements;
    @Getter
    @Setter
    private List<Entite> entites;

    public Filiere() {
        super(TYPE.Filiere);
        this.regroupements = new ArrayList<>();
        this.entites = new ArrayList<>();
    }

    @Override
    public void addChild(StructureOrganisationnelle so) {
        if (so instanceof Regroupement) {
            this.regroupements.add((Regroupement) so);
        } else if (so instanceof Entite) {
            this.entites.add((Entite) so);
        } else {
            throw new RuntimeException("Erreur interne");
        }
    }

    @Override
    public void addSalaries(List<Salarie> salaries) {
        throw new RuntimeException("Erreur interne");
    }
}
