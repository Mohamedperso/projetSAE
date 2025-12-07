import java.util.ArrayList;
import java.util.List;

public class Etudiant {
    private int id;
    private String nom;
    private String prenom;
    
    // Attributs pour mes criteres spécifiques
    private char genre;             // Pour la contrainte 1
    private String typeBac;         // Pour le critere 2
    private double moyenneGenerale; // Pour le critere 1

    public Etudiant(int id, String nom, char genre, String typeBac, double moyenneGenerale) {
        this.id = id;
        this.nom = nom;
        this.genre = genre;
        this.typeBac = typeBac;
        this.moyenneGenerale = moyenneGenerale;
    }


    /*
    Calculer le coefficient.
    GM/GI = 1.4, STI = 1.2, Autre = 1.0
    */

    public double getPuissanceBac() {
        if (this.typeBac == null) return 1.0;
        
        switch (this.typeBac.toUpperCase()) {
            case "GM":
                return 1.4;
            case "GI":
                return 1.4;
            case "STI":
                return 1.2;
            default:
                return 1.0; // Cas "A" (Autre)
        }
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public char getGenre() { return genre; }
    public String getTypeBac() { return typeBac; }
    public double getMoyenneGenerale() { return moyenneGenerale; }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + typeBac + ", " + genre + ")";
    }
}