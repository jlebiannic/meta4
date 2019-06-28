package parser.model;

import java.util.ArrayList;
import java.util.List;

import fr.maif.autop.referentiels.fregs.model.salarie.Salarie;
import lombok.Getter;
import lombok.Setter;

public class Regroupement extends StructureOrganisationnelle {

    @Getter
    @Setter
    private List<Entite> entites;

    public Regroupement() {
        super(TYPE.Regroupement);
        this.entites = new ArrayList<>();
    }

    @Override
    public void addChild(StructureOrganisationnelle so) {
        if (so instanceof Entite) {
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
