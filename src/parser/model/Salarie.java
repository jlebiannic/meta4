package parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Salarie extends ObjectModel {
    //	Salarie matricule="00009J" nomFamille="GONVER" prenom="VERONIQUE" mail="veronique.gonver@toto.com"
    private String matricule;
    private String nom;
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
        this.nom = nomFamille;
        this.prenom = prenom;
        this.mail = mail;
        this.type = "Salarie";
    }

}
