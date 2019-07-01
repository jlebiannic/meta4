package parser.model.salarie.contrat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Fonction {
    @Getter
    @Setter
    private String code;
    @Getter
    @Setter
    private String libelle;
}
