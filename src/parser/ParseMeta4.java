package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import parser.model.StructureOrganisationnelle;
import parser.model.common.Period;
import parser.model.meta4.ObjectWithUtParent;
import parser.model.meta4.ParentLink;
import parser.model.meta4.Ut;
import parser.model.salarie.ParametrageSalarie;
import parser.model.salarie.PeriodWithSalarieProps;
import parser.model.salarie.Salarie;
import parser.model.salarie.contrat.Fonction;
import parser.model.salarie.contrat.TempsTravail;
import parser.model.salarie.type.TypeContrat;
import parser.model.salarie.type.TypeHoraire;
import parser.model.salarie.type.TypeTempsTravail;
import parser.model.salarie.type.TypeVariabilite;
import parser.util.DateUtil;
import parser.util.TempsUtil;

public class ParseMeta4 {

    private static Map<String, Ut> code2ut = new HashMap<>();
    private static Map<Ut, List<Ut>> parentUt2children = new HashMap<>();
    private static List<Ut> rootUTs = new ArrayList<>();
    private static List<Ut> allUts = new ArrayList<>();
    private static List<Salarie> allSalaries = new ArrayList<>();
    private static Map<Ut, List<Salarie>> ut2salaries = new HashMap<>();
    private static Map<String, Salarie> matricule2salarie = new HashMap<>();
    private static Map<Ut, Salarie> ut2manager = new HashMap<>();
    private static Map<String, String> matricule2utCode = new HashMap<>();

    private static final String pad = "\t";
    private static final Map<Ut, StructureOrganisationnelle> utFregsMap = new HashMap<>();

    private static int maxDeep = 0;

    public static void main(String[] args) {
        File fXmlFile = new File("input/sirh-sample_all_uts.xml");
        DocumentBuilder dBuilder;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            // UNITE DE TRAVAIL
            // First pass : create and register element
            NodeList unitesTravail = doc.getElementsByTagName("UniteTravail");

            for (int indexUT = 0; indexUT < unitesTravail.getLength(); indexUT++) {
                Element utMarkup = (Element) unitesTravail.item(indexUT);
                Ut ut = new Ut(
                        utMarkup.getAttribute("codeUT"),
                        utMarkup.getAttribute("libelle"),
                        utMarkup.getAttribute("codeSociete"),
                        utMarkup.getAttribute("codeEtablissement"));
                code2ut.put(ut.getCodeUT(), ut);
                allUts.add(ut);

            }

            for (int indexUT = 0; indexUT < unitesTravail.getLength(); indexUT++) {
                Element utMarkup = (Element) unitesTravail.item(indexUT);
                String codeUt = utMarkup.getAttribute("codeUT");
                linkWithUtParentForTags(utMarkup, code2ut.get(codeUt), "UTMere");
            }

            // transformation de la collection d'UTs en JSON
            objectToJsonFile(allUts, "target/uts.json");

            // SALARIE
            NodeList salaries = doc.getElementsByTagName("Salarie");
            for (int indexSalarie = 0; indexSalarie < salaries.getLength(); indexSalarie++) {
                Element salarieMarkup = (Element) salaries.item(indexSalarie);
                // Construction des paramètres à date
                List<Period> periods = new ArrayList<>();
                Map<String, List<PeriodWithSalarieProps>> mapAtributeNamePeriod = new HashMap<>();
                Salarie salarie = new Salarie(
                        salarieMarkup.getAttribute("matricule"),
                        salarieMarkup.getAttribute("nomFamille"),
                        salarieMarkup.getAttribute("prenom"),
                        salarieMarkup.getAttribute("mail"));
                System.out.println("Matricule: " + salarie.getMatricule());
                matricule2salarie.put(salarie.getMatricule(), salarie);
                allSalaries.add(salarie);
                linkWithUtParentForTags(salarieMarkup, salarie, "RattachementUniteTravail");
                // Rattachement UnitÃ© de travail
                if (salarieMarkup.getElementsByTagName("RattachementUniteTravail").getLength() > 0) {
                    String codeUt = ((Element) salarieMarkup.getElementsByTagName("RattachementUniteTravail").item(0)).getAttribute("codeUT");
                    if (code2ut.containsKey(codeUt)) {
                        Ut uniteTravail = code2ut.get(codeUt);
                        if (!ut2salaries.containsKey(uniteTravail)) {
                            ut2salaries.put(uniteTravail, new ArrayList<>());
                        }
                        ut2salaries.get(uniteTravail).add(salarie);
                        matricule2utCode.put(salarie.getMatricule(), uniteTravail.getCodeUT());
                    } else {
                        System.err.println("Mother UT is not known for Salarie : " + salarie.getMatricule() + "->" + codeUt);
                    }
                }


                // CONTRAT
                PeriodsManager pm = new PeriodsManager();
                NodeList contratTags = salarieMarkup.getElementsByTagName("Contrat");
                for (int i = 0; i < contratTags.getLength(); i++) {
                    Element markup = (Element) contratTags.item(i);
                    String type = markup.getAttribute("type");
                    TypeContrat typeContrat = TypeContrat.valueOf(type);
                    // Gestion des périodes
                    addPeriod(pm, markup);
                    addAttributePeriod("Contrat", mapAtributeNamePeriod, markup, p -> p.getValues().setTypeContrat(typeContrat));
                }

                NodeList temptravailTags = salarieMarkup.getElementsByTagName("TempsTravail");
                for (int i = 0; i < temptravailTags.getLength(); i++) {
                    Element markup = (Element) temptravailTags.item(i);
                    TempsTravail tempsTravail = new TempsTravail();
                    String type = markup.getAttribute("type");
                    tempsTravail.setType(TypeTempsTravail.valueOf(type));
                    String horaireContractuel = markup.getAttribute("horaireContractuel");
                    if (horaireContractuel != null && !horaireContractuel.equals("")) {
                        tempsTravail.setHoraireContractuel(TempsUtil.strToQuardHeure(horaireContractuel));
                    }
                    String horaireReference = markup.getAttribute("horaireReference");
                    if (horaireReference != null && !horaireReference.equals("")) {
                        tempsTravail.setHoraireReference(TempsUtil.strToQuardHeure(horaireReference));
                    }
                    String forfaitJourIndividuel = markup.getAttribute("forfaitJourIndividuel");
                    if (forfaitJourIndividuel != null && !forfaitJourIndividuel.equals("")) {
                        tempsTravail.setForfaitJourIndividuel(Double.parseDouble(forfaitJourIndividuel));
                    }
                    String typeHoraire = markup.getAttribute("typeHoraire");
                    if (typeHoraire != null && !typeHoraire.equals("")) {
                    tempsTravail.setTypeHoraire(TypeHoraire.valueOf(typeHoraire.toUpperCase().replaceAll(" ", "_")));
                    }
                    String typeVariabilite = markup.getAttribute("typeVariabilite");
                    if (typeVariabilite != null && !typeVariabilite.equals("")) {
                        tempsTravail.setTypeVariabilite(TypeVariabilite.valueOf(typeVariabilite.toUpperCase().replaceAll(" ", "_")));
                    }
                    // Gestion des périodes
                    addPeriod(pm, markup);
                    addAttributePeriod("TempsTravail", mapAtributeNamePeriod, markup, p -> p.getValues().setTempsTravail(tempsTravail));
                }
                NodeList fonctionTags = salarieMarkup.getElementsByTagName("Fonction");
                for (int i = 0; i < fonctionTags.getLength(); i++) {
                    Element markup = (Element) fonctionTags.item(i);
                    Fonction fonctionSalarie = new Fonction();
                    String fonction = markup.getAttribute("libelle");
                    fonctionSalarie.setLibelle(fonction);
                    String code = markup.getAttribute("code");
                    fonctionSalarie.setCode(code);
                    // Gestion des périodes
                    addPeriod(pm, markup);
                    addAttributePeriod("Fonction", mapAtributeNamePeriod, markup, p -> p.getValues().setFonction(fonctionSalarie));

                }
                NodeList teleTravailTags = salarieMarkup.getElementsByTagName("Teletravail");
                for (int i = 0; i < teleTravailTags.getLength(); i++) {
                    Element markup = (Element) teleTravailTags.item(i);
                    addPeriod(pm, markup);
                    addAttributePeriod("Teletravail", mapAtributeNamePeriod, markup, p -> p.getValues().setTeletravail(true));

                }

                for (PeriodWithSalarieProps p : pm.getPeriods()) {
                    salarie.getProps().add(p);
                    if (p.getValues() == null) {
                        p.setValues(new ParametrageSalarie());
                    }
                    List<PeriodWithSalarieProps> contratPeriods = mapAtributeNamePeriod.get("Contrat");
                    setParamValue(p, contratPeriods, period -> p.getValues().setTypeContrat(period.getValues().getTypeContrat()));

                    List<PeriodWithSalarieProps> tempsTravailPeriods = mapAtributeNamePeriod.get("TempsTravail");
                    setParamValue(p, tempsTravailPeriods, period -> p.getValues().setTempsTravail(period.getValues().getTempsTravail()));

                    List<PeriodWithSalarieProps> fonctionPeriods = mapAtributeNamePeriod.get("Fonction");
                    setParamValue(p, fonctionPeriods, period -> p.getValues().setFonction(period.getValues().getFonction()));

                    List<PeriodWithSalarieProps> teletravailPeriods = mapAtributeNamePeriod.get("Teletravail");
                    setParamValue(p, teletravailPeriods, period -> p.getValues().setTeletravail(true));
                }

                salarie.getProps().sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));

            }

            // transformation de la collection de salaries en JSON
            objectToJsonFile(allSalaries, "target/salaries.json");

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void setParamValue(Period period, List<PeriodWithSalarieProps> periods, Consumer<PeriodWithSalarieProps> c) {
        if (periods != null) {
            Optional<PeriodWithSalarieProps> periodFound = periods.stream().filter(
                    p -> p.getStartDate() <= period.getStartDate() && p.getEndDate() >= period.getEndDate()).findFirst();
            if (periodFound.isPresent()) {
                c.accept(periodFound.get());
            }
        }
    }

    private static void addAttributePeriod(String attrName, Map<String, List<PeriodWithSalarieProps>> mapAtributeNamePeriod, Element markup,
            Consumer<PeriodWithSalarieProps> c) {
        List<PeriodWithSalarieProps> periods = mapAtributeNamePeriod.get(attrName);
        if (periods == null) {
            periods = new ArrayList<>();
        }
        PeriodWithSalarieProps period = new PeriodWithSalarieProps(DateUtil.strDateToInteger(markup.getAttribute("dateDebut")), DateUtil
                .strDateToInteger(markup.getAttribute("dateFin")));
        periods.add(period);
        mapAtributeNamePeriod.put(attrName, periods);
        c.accept(period);
    }

    private static void addPeriod(PeriodsManager pm, Element markup) {
        pm.addPeriod(
                DateUtil.strDateToInteger(markup.getAttribute("dateDebut")),
                DateUtil.strDateToInteger(markup.getAttribute("dateFin")));
    }

    public static class PeriodsManager {
        List<Integer> dates = new ArrayList<>();
        Set<Integer> startdates = new HashSet<>();
        Set<Integer> enddates = new HashSet<>();

        public void addPeriod(Integer startDate, Integer endDate) {
            addDate(startDate);
            addDate(endDate);
            startdates.add(startDate);
            enddates.add(endDate);
        }

        private void addDate(Integer date) {
            if (!this.dates.contains(date)) {
            int index = Collections.binarySearch(this.dates, date);
            if (index < 0) {
                index = -index - 1;
            }
            this.dates.add(index, date);
            }
        }

        public List<PeriodWithSalarieProps> getPeriods() {
            List<PeriodWithSalarieProps> periods = new ArrayList<>();
            for (int i = 0; i < this.dates.size(); i++) {
                Integer currentDate = this.dates.get(i);
                if (i + 1 < this.dates.size()) {
                    Integer currentDate2 = this.dates.get(i + 1);
                    if (this.isStartDate(currentDate) && this.isEndDate(currentDate2)) {
                        periods.add(new PeriodWithSalarieProps(currentDate, currentDate2));
                    } else if (this.isStartDate(currentDate) && this.isStartDate(currentDate2)) {
                        periods.add(new PeriodWithSalarieProps(currentDate, DateUtil.addDay(currentDate2, -1)));
                    } else if (this.isEndDate(currentDate) && this.isEndDate(currentDate2)) {
                        periods.add(new PeriodWithSalarieProps(DateUtil.addDay(currentDate, 1), currentDate2));
                    } else if (this.isEndDate(currentDate) && this.isStartDate(currentDate2)) {
                        // periods.add(new PeriodWithSalarieProps(DateUtil.addDay(currentDate, 1), DateUtil.addDay(currentDate2, -1)));
                        System.out.println("===============> Periode vide");
                    } else {
                        // TODO LOG
                        throw new RuntimeException("cas non traité");
                    }
                }
            }
            return periods;
        }

        private boolean isEndDate(Integer date) {
            return this.enddates.contains(date);
        }

        private boolean isStartDate(Integer date) {
            return this.startdates.contains(date);
        }

    }

    

    private static void linkWithUtParentForTags(Element element, ObjectWithUtParent objectModel, String tagname) {
        NodeList utChildrenNodeList = element.getElementsByTagName(tagname);
        for (int idxUtChildrenNodeList = 0; idxUtChildrenNodeList < utChildrenNodeList.getLength(); idxUtChildrenNodeList++) {
            Element utChildElement = (Element) utChildrenNodeList.item(idxUtChildrenNodeList);

            String codeUt = utChildElement.getAttribute("codeUT");
            Ut ut = code2ut.get(codeUt);
            if (ut != null) {
                ParentLink utParent = new ParentLink(ut.getIdentifiant(), utChildElement.getAttribute("dateDebut"),
                        utChildElement.getAttribute("dateFin"));
                objectModel.getUtParentLinks().add(utParent);
            }

        }
    }

    private static void objectToJsonFile(Object object, String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
