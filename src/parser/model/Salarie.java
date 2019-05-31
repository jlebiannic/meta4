package parser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Salarie {
    //	Salarie matricule="00009J" nomFamille="GONVER" prenom="VERONIQUE" mail="veronique.gonver@toto.com"
    private String matricule;
    private String nomFamille;
    private String prenom;
    private String mail;
    private String typeContrat;
    private Double horaireContractuel;
    private Double forfaitJourIndividuel;
    private String typeHoraire;
    private String typeVariabilite;
    private String fonctionCode;
    private String fonctionLibelle;
    private String type;

    public Salarie(String matricule, String nomFamille, String prenom, String mail) {
        super();
        this.matricule = matricule;
        this.nomFamille = nomFamille;
        this.prenom = prenom;
        this.mail = mail;
        this.type = "Salarie";
    }

}
