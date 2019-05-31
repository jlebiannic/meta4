package parser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UniteTravail {
	//	<UniteTravail codeUT="0020" datedebut="1999-09-01" dateFin="4000-01-01" libelle="XX - Direction" codeSociete="001" codeEtablissement="S7901">
	private String codeUT;
	private String dateDebut;
	private String dateFin;
	private String libelle;
	private String codeSociete;
	private String codeEtablissement;


}
