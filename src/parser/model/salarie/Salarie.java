package parser.model.salarie;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import parser.model.ObjectModel;
import parser.model.meta4.ObjectWithUtParent;

public class Salarie extends ObjectWithUtParent {
    public static final String TYPE = "Salarie";

    @Getter
    @Setter
    private String identifiant;

    @Getter
    @Setter
    private String nom;
    @Getter
    @Setter
    private String prenom;
    @Getter
    @Setter
    private String matricule;
    @Getter
    @Setter
    private String mail;

    @Getter
    @Setter
    private List<PeriodWithSalarieProps> props = new ArrayList<>();

    public Salarie() {
        super(TYPE);
        this.identifiant = UUID.randomUUID().toString();
    }

    public Salarie(String matricule) {
        this();
        this.matricule = matricule;
    }

    public Salarie(String matricule, String nomFamille, String prenom, String mail) {
        this(matricule);
        this.nom = nomFamille;
        this.prenom = prenom;
        this.mail = mail;
    }

    @Override
    public List<ObjectModel> getChildren() {
        return List.of();
    }

    @Override
    public void addChild(ObjectModel so) {
        // nothing
    }

    @Override
    public void removeChildren() {
        // nothing
    }

    @Override
    public void removeChild(ObjectModel so) {
        // nothing
    }

    @Override
    public void copyChildren(ObjectModel so) {
        // nothing
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
