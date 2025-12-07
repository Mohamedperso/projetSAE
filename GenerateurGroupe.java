import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GenerateurGroupe {

    private static final int MIN_FILLES = 4;

    public List<Etudiant> triEtudiant(List<Etudiant> promo){
        List<Etudiant> triEtu = new ArrayList<>(promo);
        triEtu.sort(Comparator.comparingDouble(Etudiant::getMoyenneGenerale).reversed()); //tri les etudiants des meilleurs au moins bon
        return triEtu;
    }

    public void repartitionEtudiant(List<Etudiant> triEtu, List<Groupe> groupes, int nbGroupesVoulus){
        for(int i = 0; i < triEtu.size(); i++){
            int indexGroupe;
            int tour = i / nbGroupesVoulus;

            if (tour % 2 == 0){ indexGroupe = i % nbGroupesVoulus; }
            else{ indexGroupe = (nbGroupesVoulus - 1) - (i % nbGroupesVoulus);}

            groupes.get(indexGroupe).ajouterEtudiant(triEtu.get(i));
        }

    }

    public List<Groupe> gloutonMoyenne(List<Etudiant> promo, int nbGroupeVoulus){
        int nbEtudiants = promo.size();
        int tailleMin = nbEtudiants / nbGroupeVoulus;
        int tailleMax = 0;

        if (nbEtudiants % nbGroupeVoulus != 0) {
            tailleMax = tailleMin + 1;
        } else {
            tailleMax = tailleMin;
        }

        System.out.println("Groupes entre "+ tailleMin + " et " + tailleMax + " etudiants.");

        List<Groupe> groupes = new ArrayList<>();
        for(int i = 0; i < nbGroupeVoulus; i++){
            groupes.add(new Groupe(i + 1));
        }

        List<Etudiant> triEtu = triEtudiant(promo);

        repartitionEtudiant(triEtu, groupes, nbGroupesVoulus);

        if (estValide(groupes, tailleMin, tailleMax)) {
            System.out.println("Succès de mon algo Glouton Moyenne et le score global est : " + calculerScoreTotal(groupes));
            return groupes;
        } 
        
        else {
            System.out.println("Échec de mon algo Glouton Moyenne car au moins 1 des contraintes n'est pas respectée.");
            return null; 
        }
    }

    /*
     * Cette fonction vérifie si TOUS les groupes respectent les règles du jeu.
     * Elle renvoie 'true' si tout est bon, 'false' si un seul groupe pose problème.
     */
    private boolean estValide(List<Groupe> groupes, int min, int max) {
        
        // On regarde chaque groupe un par un
        for (Groupe g : groupes) {
            
            // Règle 1 : La taille (calculée juste avant dans le code)
            // Si le groupe est trop petit OU trop grand -> C'est false.
            if (g.taille() < min || g.taille() > max) {
                System.out.println("Problème taille sur le groupe " + g.getIdGroupe());
                return false; 
            }

            // Règle 2 : La mixité (Contrainte)
            // On utilise ta méthode compterFilles() de la classe Groupe
            if (g.compterFilles() < MIN_FILLES) {
                System.out.println("Manque de filles dans le groupe " + g.getIdGroupe());
                return false;
            }
        }

        return true;
    }

    private double calculerScoreTotal(List<Groupe> groupes) {
        if (groupes == null || groupes.isEmpty()) return Double.MAX_VALUE;

        double minMoy = 20.0, maxMoy = 0.0;
        double minBac = Double.MAX_VALUE, maxBac = 0.0;

        for (Groupe g : groupes) {
            double moy = g.getMoyenneGroupe();
            double bac = g.getScoreBacTotal();
            
            if (moy < minMoy) minMoy = moy;
            if (moy > maxMoy) maxMoy = moy;
            
            if (bac < minBac) minBac = bac;
            if (bac > maxBac) maxBac = bac;
        }
        
        // Optimisation : Minimiser (Ecart Moyenne + Ecart Bac) [Source 13]
        return (maxMoy - minMoy) + (maxBac - minBac);
    }
}