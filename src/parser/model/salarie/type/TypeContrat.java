package parser.model.salarie.type;

import lombok.Getter;
import lombok.Setter;

public enum TypeContrat {
    CAP("Contrat d'apprentissage"),
    CDD("Contrat à durée déterminée"),
    CDI("Contrat à durée indéterminée"),
    CQUA("Contrat de qualification"),
    MAN("Mandataire"),
    PREM("Pré-retraité"),
    PRO1("Contrat de professionnalisation avec Bac"),
    PRO2("Contrat de professionnalisation sans Bac"),
    STA("Stagiaire");

    @Getter
    @Setter
    private String description = "";

    TypeContrat(String description) {
        this.description = description;
    }

}
