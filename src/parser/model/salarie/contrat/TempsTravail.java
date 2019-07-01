package parser.model.salarie.contrat;

import lombok.Getter;
import lombok.Setter;
import parser.model.salarie.type.TypeCategoriePointage;
import parser.model.salarie.type.TypeHoraire;
import parser.model.salarie.type.TypeTempsTravail;
import parser.model.salarie.type.TypeVariabilite;

public class TempsTravail {
    @Getter
    @Setter
    private TypeTempsTravail type;
    @Getter
    @Setter
    private int horaireContractuel;
    @Getter
    @Setter
    private double forfaitJourIndividuel;
    @Getter
    @Setter
    private TypeHoraire typeHoraire;
    @Getter
    @Setter
    private TypeVariabilite typeVariabilite;
    @Getter
    @Setter
    private TypeCategoriePointage categoriePointage;

}
