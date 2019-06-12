package parser.model;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class Salarie extends ObjectWithUtParent {
    //	Salarie matricule="00009J" nomFamille="GONVER" prenom="VERONIQUE" mail="veronique.gonver@toto.com"
    @Getter
    @Setter
    private String identifiant;
    @Getter
    @Setter
    private String matricule;
    @Getter
    @Setter
    private String nom;
    @Getter
    @Setter
    private String prenom;
    @Getter
    @Setter
    private String mail;
    @Getter
    @Setter
    private String typeContrat;
    @Getter
    @Setter
    private Double horaireContractuel;
    @Getter
    @Setter
    private Double forfaitJourIndividuel;
    @Getter
    @Setter
    private String typeHoraire;
    @Getter
    @Setter
    private String typeVariabilite;
    @Getter
    @Setter
    private String fonctionCode;
    @Getter
    @Setter
    private String fonctionLibelle;
    @Getter
    @Setter
    private String type;

    public Salarie() {
        super();
        this.identifiant = UUID.randomUUID().toString();
    }

    public Salarie(String matricule, String nomFamille, String prenom, String mail) {
        this();
        this.matricule = matricule;
        this.nom = nomFamille;
        this.prenom = prenom;
        this.mail = mail;
        this.type = "Salarie";
    }

}
