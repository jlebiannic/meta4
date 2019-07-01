package parser.model.salarie;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import parser.model.salarie.contrat.Fonction;
import parser.model.salarie.contrat.TempsTravail;
import parser.model.salarie.type.TypeCategoriePointage;
import parser.model.salarie.type.TypeContrat;

@NoArgsConstructor
public class ParametrageSalarie {

    /** Paramètres du contrat */
    @Getter
    @Setter
    private TypeContrat typeContrat;
    @Getter
    @Setter
    private TempsTravail tempsTravail;
    @Getter
    @Setter
    private TypeCategoriePointage categoriePointage;
    @Getter
    @Setter
    private Fonction fonction;
}
