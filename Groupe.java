import java.util.ArrayList;
import java.util.List;

public class Groupe {
    private int idGroupe;
    private List<Etudiant> etudiants;

    // Constructeur :
    public Groupe(int idGroupe) {
        this.idGroupe = idGroupe;
        this.etudiants = new ArrayList<>();
    }

    // Ajoute un étudiant à la liste
    public void ajouterEtudiant(Etudiant e) {
        this.etudiants.add(e);
    }
    
    // Retire un étudiant de la liste
    public void retirerEtudiant(Etudiant e) {
        this.etudiants.remove(e);
    }

    // Retourne le nombre d'étudiants dans le groupe
    // Sert à vérifier la contrainte : Min <= Taille <= Max
    public int taille() {
        return etudiants.size();
    }

/*
    Contrainte Obligatoire (Hard) : Nombre de filles
    Doit parcourir la liste et compter ceux dont le genre est 'F'
*/
    public int compterFilles() {
        int compte = 0;
        for(int i = 0; i < taille(); i++){
            if(etudiants.get(i).getGenre() == 'F') { compte++; }
        }
        return compte;
    }

/*
    Critère d'Optimisation 1 (Soft) : Moyenne Générale du groupe
    Doit faire la moyenne des moyennes des étudiants.
*/
    public double getMoyenneGroupe() {

        if(taille() == 0) { return 0.0; }

        double moyenneGenerale = 0.0; 
        for(int i = 0; i < taille(); i++){
            moyenneGenerale += etudiants.get(i).getMoyenneGenerale();
        }
        return moyenneGenerale / taille();
    }

/*
    Critère d'Optimisation 2 : Score BAC total du groupe
    Correction : On renvoie la Somme brute pour équilibrer la "puissance"
*/
    public double getScoreBacTotal() {
        if (taille() == 0) { return 0.0; }
        
        double puissanceBacGroupe = 0.0;
        for (int i = 0; i < taille(); i++){
            puissanceBacGroupe += etudiants.get(i).getPuissanceBac();
        }
        
        return puissanceBacGroupe;
    }
    
    // Getters
    public List<Etudiant> getEtudiants() { return etudiants; }
    
    public int getIdGroupe() { return idGroupe; }
}