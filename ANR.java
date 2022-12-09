import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/*
*Fait avec l'aider complémentaire de raphael L
*
*/

public class ANR<E extends Comparable<E>> extends AbstractCollection<E> 
{
	private Noeud racine;
	private int taille;
	private Comparator<? super E> cmp;
	private final Noeud sentinelle = new Noeud(null);

	private class Noeud
	{
		E cle;
		Noeud gauche;
		Noeud droit;
		Noeud pere;
		char  couleur;

		Noeud(E cle) 
		{
			this.cle = cle;
			couleur = 'N';
			
			gauche = sentinelle;
			droit = sentinelle;
			pere = sentinelle;
		}
		
		Noeud(E cle, char coul) 
		{
      this.cle = cle;
      couleur = coul;
    }

		/**
		 * Renvoie le noeud contenant la clé minimale du sous-arbre enraciné
		 * dans ce noeud
		 * 
		 * @return le noeud contenant la clé minimale du sous-arbre enraciné
		 *         dans ce noeud
		 */
		Noeud minimum() {
			Noeud x = this;
			while (x.gauche != sentinelle) x = x.gauche;
			
			return x;
		}

		/**
		 * Renvoie le successeur de ce noeud
		 * 
		 * @return le noeud contenant la clé qui suit la clé de ce noeud dans
		 *         l'ordre des clés, null si c'es le noeud contenant la plus
		 *         grande clé
		 */
		Noeud suivant() {
			Noeud x = this;
			if (x.droit != sentinelle) 
				return x.droit.minimum();
			
			Noeud y = x.pere;
			while (y != sentinelle && x == y.droit) 
			{
			    x = y;
			    y = y.pere;
			}
			
			return y;
		}
	}
	
	
	
	/**
	 * Les itérateurs doivent parcourir les éléments dans l'ordre ! Ceci peut se
   * faire facilement en utilisant {@link Noeud#minimum()} et
   * {@link Noeud#suivant()}
   */
  private class ANRIterator implements Iterator<E> {
    private Noeud suiv, prec;
    
    public ANRIterator()
    {
      prec = sentinelle;
      if ( racine == sentinelle ) 
        suiv = sentinelle;
      else 
        suiv = racine.minimum();        
    }
    
    public boolean hasNext() 
    {
      return suiv != sentinelle;
    }
    
    public E next() 
    {
      if ( suiv == sentinelle ) throw new NoSuchElementException();
      prec = suiv;
      suiv = suiv.suivant();
      return prec.cle;
    }

    public void remove() 
    {
      if ( prec == sentinelle)  throw new IllegalStateException();
      suiv = supprimer(prec); 
      prec = sentinelle;
    }
  }

	// Constructeurs

	/**
	 * Crée un arbre vide. Les éléments sont ordonnés selon l'ordre naturel
	 */
	@SuppressWarnings("unchecked")
	public ANR() {
	  cmp = (Comparator<E>) Comparator.naturalOrder();
		racine = sentinelle;
	}

	/**
	 * Crée un arbre vide. Les éléments sont comparés selon l'ordre imposé par
	 * le comparateur
	 * 
	 * @param cmp
	 *            le comparateur utilisé pour définir l'ordre des éléments
	 */
	public ANR(Comparator<? super E> cmp) 
	{
	  this.cmp = cmp;
		racine = sentinelle;
	}

	/**
	 * Constructeur par recopie. Crée un arbre qui contient les mêmes éléments
	 * que c. L'ordre des éléments est l'ordre naturel.
	 * 
	 * @param c
	 *            la collection à copier
	 */
	@SuppressWarnings("unchecked")
	public ANR(Collection<? extends E> c) 
	{
	  racine = sentinelle;
	  this.cmp = (Comparator<E>) Comparator.naturalOrder();
		addAll(c);
	}

	@Override
	public Iterator<E> iterator() {return new ANRIterator();}

	@Override
	public int size() {return taille;}

	/**
	 * Recherche une clé. Cette méthode peut être utilisée par
	 * {@link #contains(Object)} et {@link #remove(Object)}
	 * 
	 * @param o
	 *            la clé à chercher
	 * @return le noeud qui contient la clé ou null si la clé n'est pas trouvée.
	 */
	private Noeud rechercher( Object o) {


		Noeud x = racine;
		E elementActuel = (E)o;

		while (x != sentinelle &&  cmp.compare(elementActuel, x.cle) !=0) 
		{
			x = cmp.compare(elementActuel, x.cle)<1 ? x.gauche : x.droit;
		}

		return x;
	}

	/**
	 * Supprime le noeud z. Cette méthode peut être utilisée dans
	 * {@link #remove(Object)} et {@link Iterator#remove()}
	 * 
	 * @param z
	 *            le noeud à supprimer
	 * @return le noeud contenant la clé qui suit celle de z dans l'ordre des
	 *         clés. Cette valeur de retour peut être utile dans
	 *         {@link Iterator#remove()}
	 */
	private Noeud supprimer(Noeud z) {
	  Noeud y ,x;
	  Noeud svt = z.suivant();

	  if (z.gauche == sentinelle || z.droit == sentinelle)
		    y = z;
	  else
	    y = svt;
	  // y est le nœud à détacher
	
	  if (y.gauche != sentinelle)
	    x = y.gauche;
	  else
	    x = y.droit;
	  // x est le fils unique de y ou null si y n'a pas de fils
	
	  x.pere = y.pere;
	
	  if (y.pere == sentinelle) // suppression de la racine
	  { 
	    racine = x;
	  } else 
	  {
	    if (y == y.pere.gauche)
	      y.pere.gauche = x;
	    else
	      y.pere.droit = x;
	  }
	
	  if (y != z) 
	  {
		  z.cle = y.cle;
		  svt = z;
	  }
	  if (y.couleur == 'N') 
	    supprimerCorrection(x);

		taille--;
		return svt;
	}
	
	private void supprimerCorrection(Noeud x) {
	  Noeud w;
    while (x != racine && x.couleur == 'N') 
    {
      if (x == x.pere.gauche) {
        w = x.pere.droit; // le frère de x
      if (w.couleur == 'R') 
      {
        // cas 1
        w.couleur = 'N';
        x.pere.couleur = 'R';
        rotationGauche(x.pere);
        w = x.pere.droit;
      }
      if (w.gauche.couleur == 'N' && w.droit.couleur == 'N') 
      {
        // cas 2
        w.couleur = 'R';
          x = x.pere;
      } else 
      {
        if (w.droit.couleur == 'N') 
        {
          // cas 3
          w.gauche.couleur = 'N';
          w.couleur = 'R';
            rotationDroite(w);
            w = x.pere.droit;
        }
        // cas 4
        w.couleur = x.pere.couleur;
        x.pere.couleur = 'N';
        w.droit.couleur = 'N';
        rotationGauche(x.pere);
        x = racine;
      }
      } else 
      {
        // (*) est vérifié ici
        if (x == x.pere.droit) 
        {
          w = x.pere.gauche; // le frère de x
          if (w.couleur == 'R') 
          {
            // cas 1
            w.couleur = 'N';
            x.pere.couleur = 'R';
            rotationGauche(x.pere);
            w = x.pere.gauche;
          }
          if (w.droit.couleur == 'N' && w.gauche.couleur == 'N') 
          {
            // cas 2
            w.couleur = 'R';
            x = x.pere;
          } else {
            if (w.gauche.couleur == 'N') 
            {
              // cas 3
              w.droit.couleur = 'N';
              w.couleur = 'R';
              rotationGauche(w);
              w = x.pere.gauche;
            }
            // cas 4
            w.couleur = x.pere.couleur;
            x.pere.couleur = 'N';
            w.gauche.couleur = 'N';
            rotationDroite(x.pere);
            x = racine;
          }
        }
      }
    }
	  // (**) est vérifié ici
	  x.couleur = 'N';
	}
	

	// Pour un "joli" affichage

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		toString(racine, buf, "", maxStrLen(racine));
		return buf.toString();
	}

	private void toString(Noeud x, StringBuffer buf, String path, int len) {
		if (x == sentinelle)
			return;
		toString(x.droit, buf, path + "D", len);
		for (int i = 0; i < path.length(); i++) {
			for (int j = 0; j < len + 6+3; j++)
				buf.append(' ');
			char c = ' ';
			if (i == path.length() - 1)
				c = '+';
			else if (path.charAt(i) != path.charAt(i + 1))
				c = '|';
			buf.append(c);
		}
		buf.append("-- " + x.cle.toString() + "(" + x.couleur + ")");
		if (x.gauche != sentinelle || x.droit != sentinelle) {
			buf.append(" --");
			for (int j = x.cle.toString().length(); j < len; j++)
				buf.append('-');
			buf.append('|');
		}
		buf.append("\n");
		toString(x.gauche, buf, path + "G", len);
	}

	private int maxStrLen(Noeud x) 
	{
		return x == sentinelle ? 0 : Math.max(x.cle.toString().length(),
				Math.max(maxStrLen(x.gauche), maxStrLen(x.droit)));
	}


	/**
   * Renvoie la racine de l'arbre
   * 
   * @return le noeud racine
   */	
	public Noeud getRacine() { return this.racine; }
	
	/**
   * Renvoie le comparator de l'arbre
   * 
   * @return le comparator de l'arbre
   */ 
	public Comparator<? super E> getCmp() { return this.cmp; }
	
	
	/**
   * Ajoute un noeud dans l'arbre
   * 
   * @param z le noeud à rajouter
   */ 
	public void ajouter(Noeud z) {
		  if( racine == sentinelle ) 
		  {
		    z.couleur = 'N';
			  racine = z;
		  }
		  else 
		  {
			  Noeud y = sentinelle;
			  Noeud x = racine;
			  
			  while (x != sentinelle) 
			  {
			    y = x;
			    if ( cmp.compare(z.cle, x.cle) < 0 ) 
			    {
			    	x = x.gauche;
			    }
			    else {
			    	x = x.droit;
			    }
			  }
			  z.pere = y;
			  if (y == sentinelle) 
			  { 
			    racine = z;
			  } else 
			  {
			    if (cmp.compare(z.cle, y.cle) < 0)
			      y.gauche = z;
			    else
			      y.droit = z;
			  }
			  z.gauche = z.droit = sentinelle;
			  z.couleur = 'R';

			  ajouterCorrection(z);
		  }
		  taille++;
	}

	private void ajouterCorrection( Noeud n){
		// re organisation de l'arbre, en remontant vers la racine
		while(n != racine && n.pere.couleur == 'R' )
		{
			if(n.pere == n.pere.pere.gauche)
			{
				Noeud y = n.pere.pere.droit;
				if( y.couleur == 'R' )
				{
					n.pere.couleur = 'N';
					y.couleur = 'N';
					n.pere.pere.couleur = 'R';
					n = n.pere.pere;
				}else
				{
					if(n == n.pere.droit)
					{
						n = n.pere;
						rotationGauche(n);
					}
					n.pere.couleur = 'N';
					n.pere.pere.couleur = 'R';
					rotationDroite(n.pere.pere);
				}
			}else
			{
				Noeud y = n.pere.pere.gauche;
				if( y.couleur == 'R' )
				{
					n.pere.couleur = 'N';
					y.couleur = 'N';
					n.pere.pere.couleur = 'R';
					n = n.pere.pere;
				}else
				{
					if(n == n.pere.gauche)
					{
						n = n.pere;
						rotationDroite(n);
					}
					n.pere.couleur = 'N';
					n.pere.pere.couleur = 'R';
					rotationGauche(n.pere.pere);
				}
			}
		}
		racine.couleur = 'N';
	}

	private void rotationGauche(Noeud z) {
		Noeud tmp = z.droit;
		z.droit = tmp.gauche;

		if(tmp.gauche != sentinelle)
		{
			tmp.gauche.pere = z;
		}

		tmp.pere = z.pere;
		if(z.pere==sentinelle) 
		{
			racine = tmp;
		}else
		{
			if(z.pere.gauche == z)
			{
				z.pere.gauche = tmp;
			}else
			{
				z.pere.droit = tmp;
			}
		}

		tmp.gauche = z;
		z.pere = tmp;
	}

	private void rotationDroite(Noeud z) {
		Noeud tmp = z.gauche;
		z.gauche = tmp.droit;

		if (tmp.droit != sentinelle) 
		{
			tmp.droit.pere = z;
		}

		tmp.pere = z.pere;
		if (z.pere == sentinelle) 
		{
			racine = tmp;
		} else 
		{
			if (z.pere.droit == z) 
			{
				z.pere.droit = tmp;
			} else 
			{
				z.pere.gauche = tmp;
			}
		}

		tmp.droit = z;
		z.pere = tmp;
	}
	
	public boolean add(E e) 
	{
		ajouter( new Noeud(e));
		return true;
	}

	public boolean addAll( Collection<? extends E> c ) {
		for(E element : c) {
			ajouter(new Noeud(element));
		}
		
		return true;
	}

	public void clear() {
		racine = sentinelle;
	}

	public boolean contains(Object o) 
	{
		if( rechercher(o) != sentinelle ) {
			return true;
		}else{
			return false;
		}
	}
	
	public boolean containsAll(Collection<?> c) {
		
		for( Object element: c) 
		{
			if( rechercher(element) == sentinelle ) 
				return false;
		}
		
		return true;
	}
	
	public boolean isEmpty() {
		if ( racine == sentinelle ) return true;
		
		return false;
	}
	
	public boolean remove(Object o) 
	{
		Noeud n = rechercher(o);
		if( n == sentinelle ) 
			return false;
		
		supprimer(n);
		return true;
	}
	

	public boolean removeAll(Collection<?> c) 
	{

		for(Object o : c) 
		{
			Noeud n = rechercher(o);
			if( n == sentinelle ) 
				return false;
			
			supprimer(n);
		}
		
		return true;
	}
	
	public Object[] toArray() 
	{
		Object[] objets = new Object[size()];
		Iterator i = iterator();
		int cpt = 0;

		while( i.hasNext() ) {
			objets[cpt++] = i.next();
		}
		
		return objets;
	}
	
	public static void main(String[] args) 
	{
		Scanner myObj = new Scanner(System.in);
		String choixDeTest;  
		final int longeurStringPourLeTE = 10;  //Je trouvais ça moche de le mettre en Upper
		do{
			System.out.println("Taper 1 pour tester les différents cas");
			System.out.println("Taper 2 pour tester le temps d'execution du programme avec des strings");
			System.out.println("Taper 3 pour tester le temps d'execution du programme avec des ints");
			System.out.print("Quel est votre choix : ");
			choixDeTest = myObj.nextLine();

		
			System.out.println(); // Pour faire joli


		}while(!choixDeTest.equals("1") && !choixDeTest.equals("2")&& !choixDeTest.equals("3"));

		if(choixDeTest.equals("1"))  //Sert à tester les différents cas pouvant être rencontrés
		{
			ArrayList<String> list = new ArrayList<>();

			list.add("g");
			list.add("u");
			list.add("x");
			list.add("e");
			list.add("h");
			list.add("t");
			list.add("k");
			list.add("z");
			list.add("m");
			list.add("a");
			list.add("j");
			
			Collection<String> anr = new ANR<>(list);
			String border = "====================================================";

			System.out.println(border);
			System.out.println("     Test 00: Affichage normal de l'arbre       \n");	
			System.out.println(anr);

			System.out.println(border);
			System.out.println("  Test 01: Supprimer un noeud avec deux feuille (g)  \n");	
			anr.remove("g");
			System.out.println(anr);

			System.out.println(border);
			System.out.println("       Test 02: Supprimer une feuille (m)      \n");	
			anr.remove("m");
			System.out.println(anr);

			System.out.println(border);
			System.out.println("  Test 03: Supprimer un noeud avec une feuille (x)  \n");	
			anr.remove("x");
			System.out.println(anr);


			System.out.println(border);
			System.out.println("       Test 04: Supprimer la racine  (k)        \n");	
			anr.remove("k");
			System.out.println(anr);
			System.out.println(border);

		}

		if(choixDeTest.equals("2"))  //Sert à calculer la vitesse d'execution du programme avec des string
		{

			System.out.println("Nombre d'ajout | Temps d'execution");
			long start = System.currentTimeMillis(); //Permettre le calcul du temps d'exec
			int totAjout = 10000000 ; // 100 million
			ANR<String> arbre = new ANR<>();

			for(int nbAjout=0;nbAjout<totAjout;nbAjout++)
			{
				arbre.add(RandomString(longeurStringPourLeTE));

				if(nbAjout%100000 ==0 )
				{
					long tempsActuel = System.currentTimeMillis() - start;
					System.out.println(nbAjout +" , "+ tempsActuel); //Utile pour faire un doc permettant de tracer la courbe d'ou la virgule
				}
			}			
		}
		if(choixDeTest.equals("3"))  //Sert à calculer la vitesse d'execution du programme avec des int
		{

			System.out.println("Nombre d'ajout | Temps d'execution");
			long start = System.currentTimeMillis(); //Permettre le calcul du temps d'exec
			int totAjout = 10000000 ; // 100 million
			ANR<Integer> arbre = new ANR<>();

			for(int nbAjout=0;nbAjout<totAjout;nbAjout++)
			{
				arbre.add((int)(Math.random()*(10+1)));

				if(nbAjout%100000 ==0 )
				{
					long tempsActuel = System.currentTimeMillis() - start;
					System.out.println(nbAjout +" , "+ tempsActuel); //Utile pour faire un doc permettant de tracer la courbe d'ou la virgule
				}
			}
		}
	
	}

	public static String RandomString(int stringLength) 
	{
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = stringLength;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
		.limit(targetStringLength)
		.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		.toString();

		return generatedString;
	}
}
