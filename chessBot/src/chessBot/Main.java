package chessBot;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.AWTException;
import java.awt.Point;
import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;
import chessBot.robotHelper;


public class Main {

	static final playColor.color white = playColor.color.WHITE;
	static final playColor.color black = playColor.color.BLACK;
	
	static playColor.color couleurDeJeu = null; //null = autodetect
	static final boolean capture = false; //si le bot doit capturer les screen des pieces

	//random
	static double factorDelayMin = 0.3d; //au minimum factorDelayMin * botDelay
	static double factorDelayMax = 2d; //au maximum factorDelayMax * botDelay
	
	//botDelay

	//bullet = 150 ms
	//blitz 10 = 5000 ms
	//blitz 3 = 2000 ms
	//autre = 10000 ms
	static final int botDelay = 150; //le temps que le bot met à reflechir
	static final double botDelayOpeningFactor = 0.5; //botDelayOpening * botDelay = le temps que le bot met à reflechir pendant l'opening
	static final int openingBounds = 4; //passé ce nombre de coups le bot considère que ce n'est plus l'opening
	static final int accelerationFactor = 8; //Tout les accelerationFactor coup diminue factorDelayMax de factorDelayMin
	
	//TODO
	//Plus le temps passe moins le bot doit prendre de temps
	//workaround : diminuer factorDelayMax de y tout les x coups?
	//ce serait sympa d'avoir le timer mais peut être overkill
	
	static int relance = 0;
	static playColor.color couleurEnnemi;
	
	//static final String path = "./data/";
	//static final String path = "C:\\Users\\Seven\\Documents\\git\\chessBot\\data\\";
	static final String path = "C:\\Users\\Seven\\git\\botance\\chessBot\\data\\";
	static int x;
	static int y;

	static Point mouseLoc1;
	static Point mouseLoc2;
	static int size;
		
	static Case caseptr;
	
	static double distance (Point p) {
	    return (Math.sqrt(p.x * p.x + p.y * p.y));
	    }
	
	
	public static void jeu(Echiquier e, StockfishInterface s, Robot r) throws IOException, InterruptedException, AWTException{
		
		if(couleurDeJeu == e.getTurn()){ // a lui de jouer
			//System.out.println("a moi de jouer");
			
			if(e.getNbCoup() % accelerationFactor == 0)
				factorDelayMax -= factorDelayMin;
			
			String[] nextMove = null;
			String coup=null;
			String score=null;
			String score_t=null;
			double randomDelayFactor;
			if (e.getNbCoup() <= openingBounds)
				randomDelayFactor = botDelayOpeningFactor;
			else
				randomDelayFactor =(factorDelayMin + (Math.random() * (factorDelayMax - factorDelayMin)));
			
			try {
			nextMove = s.nextMove(e.getFen(), (int) (botDelay * randomDelayFactor));
			coup=nextMove[0];
			score_t = nextMove[1].split(" ")[0];
			score = nextMove[1].split(" ")[1];
			
			}
			catch(ArrayIndexOutOfBoundsException exception) //stockfish a planté
			{
				//on le relance sans les roque
				if(relance == 0)
				{
				System.out.println("Stockish a planté. On le relance sans roque");
				e.zeroCastle();
				s.startOver();
				e.readPieces();
				relance++;
				return;
				}
				else
				{
					exception.printStackTrace();
					System.exit(1);
				}
			}
			System.out.println("Je Joue: " + coup);
			Case[] cases = e.PGNtoPtr(coup);
			robotHelper.jouerCoup(cases, r);
			e.inverseTurn();
			
			if(couleurDeJeu == playColor.color.BLACK)
				e.augmenterNbCoup();
			
			if(score.equals("1") && score_t.equals("mate"))
			{
				//fin du game
				System.out.println("Fin du film");
				System.out.println("You've got chessBoss'd");
				System.exit(0);
			}
			
			
			//mise a jour echiquier
			e.updateEchiquier(cases);
			r.delay(400);
			
		}
		else{ // pas a lui de jouer
			// lecture
			int[][] old = Echiquier.deepCopy(e.simpleArea());
			while(couleurDeJeu != e.getTurn()){
				
				e.readPieces();
				int[][] sa1 = e.simpleArea();
				int bint = Echiquier.equalSimpleAreaInt(old,e.simpleArea(),e);
				
				
				//r.delay(500);
								
				if(bint > 0){ 
					r.delay(400);
					e.readPieces();
					e.inverseTurn();
					
					if(couleurEnnemi == playColor.color.BLACK)
						e.augmenterNbCoup();
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception
	{
		Robot r = new Robot();
		

		int[] coord = robotHelper.findEchiquierChess(); //TODO Améliorer la fonction findEchiquierChess (fonctionne uniquement avec les couleurs de pixels)

		mouseLoc1 = new Point(coord[2],coord[3]);
		mouseLoc2 = new Point(coord[0],coord[1]);
		mouseLoc2.translate(-mouseLoc1.x, -mouseLoc1.y);
		size=(int)distance(mouseLoc2);
				
		if(size == 6000)
			throw new Exception("Echiquier non trouvé !");
		
		System.out.println(size);
		
		Echiquier e = new Echiquier(mouseLoc1.x,mouseLoc1.y,size,couleurDeJeu);
		//e.zeroCastle();
		if(couleurDeJeu == null) //autodetect
		{
		Case casedetect = e.getEchiquier()[7][0];
		Rectangle rec = casedetect.getAdjustedRectangle();
		BufferedImage img = new Robot().createScreenCapture(rec);
		couleurDeJeu = casedetect.detectColorPiece(img);
		e = new Echiquier(mouseLoc1.x,mouseLoc1.y,size,couleurDeJeu);
		}
		
		couleurEnnemi = playColor.inverseColor(couleurDeJeu);
		
		Piece.initImageData(e,capture);
		
		e.readPieces();
		
		System.out.println(e.getFen());
		
		StockfishInterface s = new StockfishInterface();
		
		
		while(true) {
			jeu(e, s, r);
		}

	}

}
