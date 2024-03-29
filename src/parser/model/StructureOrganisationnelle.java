package parser.model;

import java.util.List;

import lombok.Data;
import parser.model.salarie.Salarie;

@Data
public abstract class StructureOrganisationnelle {

    public static enum TYPE {
        Filiere,
        Regroupement,
        Entite,
        Groupe
    }

    private TYPE type;
    private String codeUT;
    private String dateDebut;
    private String dateFin;
    private String libelle;
    private String codeSociete;
    private String codeEtablissement;
    private String referentMatricule;

    public StructureOrganisationnelle(TYPE type) {
        this.type = type;
    }

    public abstract void addChild(StructureOrganisationnelle so);

    public abstract void addSalaries(List<Salarie> salaries);
}
