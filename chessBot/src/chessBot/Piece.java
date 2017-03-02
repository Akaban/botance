package chessBot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Piece {
	
	enum nomPiece { Pawn,Knight,King,Queen,Bishop,Rook,Empty };
	
	private nomPiece type;
	private playColor.color couleur;
	
	
	
	public static BufferedImage[] dataPiece;
	public static final nomPiece[] mapPiece = {nomPiece.Pawn,nomPiece.Knight,nomPiece.King,nomPiece.Queen,nomPiece.Bishop,nomPiece.Rook,nomPiece.Empty};
	public static final playColor.color[] mapColor = {playColor.color.WHITE,playColor.color.BLACK};
	
	
	public Piece(nomPiece type,playColor.color couleur)
	{
		this.type = type;
		this.couleur = couleur;
	}
	
	

	
	public static String toStringEnum(nomPiece type)
	{
		String ret;
		
		if (type == null) return "empty";
		
		switch (type)
		{
		case Pawn :
			ret = "pawn";
			break;
		case Knight :
			ret = "knight";
			break;
		case King :
			ret = "king";
			break;
		case Queen :
			ret = "queen";
			break;
		case Bishop:
			ret = "bishop";
			break;
		case Rook:
			ret = "rook";
			break;
		default :
			ret = "empty";
				
		}
		return ret;
		}
	
	public char toChar()
	{
		char ret;
		
		if (this.type == null) return '%';
		
		switch (this.type)
		{
		case Pawn :
			ret = 'p';
			break;
		case Knight :
			ret = 'n';
			break;
		case King :
			ret = 'k';
			break;
		case Queen :
			ret = 'q';
			break;
		case Bishop:
			ret = 'b';
			break;
		case Rook:
			ret = 'r';
			break;
		default :
			ret = '%';
				
		}
		
		if(this.couleur == playColor.color.WHITE) ret = Character.toUpperCase(ret);
		
		return ret;
	
		
		
	}
	
	public static void initImageData(Echiquier e) throws IOException, AWTException
	{
		

		
		int[][] pieces_debut = {
				{1,0}, {0,1}, {0,4} , {0,3},
				{0,2}, {0,0} , {2,0}};
		
		dataPiece = new BufferedImage[7];

		for(int countPiece=0; countPiece < 7 ; countPiece++)
		{
			int[] coord = pieces_debut[countPiece];
			Case c;
			
			c = e.getEchiquier()[coord[0]][coord[1]];
			
			BufferedImage lookingAt = robotHelper.traitementContour(new Robot().createScreenCapture(c.getRectangle()),c.getColor());
			
			String casename = toStringEnum(mapPiece[countPiece]);
			ImageIO.write(lookingAt, "png", new File(Main.path + casename+".png"));
			
			dataPiece[countPiece] = lookingAt;
							
		}
	
		
		System.out.println("finished init");

}
}
