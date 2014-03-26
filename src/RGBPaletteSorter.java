public class RGBPaletteSorter
{	
	private int[]  	inPalette;      	// a local copy of the disorderly palette
	private int[] 	fCycle;
	private int[] 	fDist; 				// distances to all other colors from current color.
	private int 	fIteration; 		// The current iteration of the path minimizing algorithm
	private int 	paletteSize;
	private int 	fStartColor;		// Starting color for the path
	private int 	minDistance;

	/**
	Initialize inPalette, Mask off alpha for optimization.
	@param startPalette Palette to be sorted
	 */
	public RGBPaletteSorter(int[] startPalette)
	{
		this.paletteSize = startPalette.length;
		this.inPalette = startPalette; 
	}

	public RGBPaletteSorter()
	{
	}

	// Return with alpha added back in
	public int[] getPalette()
	{
		for (int n = 0; n < paletteSize; n++)
		{
			inPalette[n] = 0xFF000000 | inPalette[n];
		}

		return inPalette;
	}

	/**
	Debug Utility: Dump current palette to System.out.
	 */
	static int cnt3=0;
	public void dumpPalette() 
	{
		if(cnt3==0)
		{
			cnt3++;
		}
		for (int n = 0; n < paletteSize; n++)
		{
			System.out.println(n+": "+ (0xFF000000 | inPalette[n]) );
		}
	}

	/**
	Calculates the total path distance of the PaletteSorter object
	which is the length of the traveling salesman's "tour" through the palette.

	@returns total path distance thru color space
	 */
	static int cnt4=0;
	public final int getTotalRGBPathDistance()
	{
		if(cnt4==0)
		{
			cnt4++;
		}

		int totalDistance = 0;
		for (int i = 1; i<paletteSize; i++)
		{
			totalDistance += DistanceRGBs(inPalette[i-1],inPalette[i]);
		}
		totalDistance += DistanceRGBs(inPalette[(paletteSize-1)],inPalette[0]);
		if(cnt4==1)
		{
			cnt4++;
		}
		return totalDistance;
	}

	/**
	Calculates distance between 2 colors in 3d color space.

	@returns path distance thru color space
	 */
	static int cnt5=0;
	public final int DistanceRGBs(int rgb1, int rgb2)
	{
		if(cnt5==0)
		{
			cnt5++;
		}
		int d_Red   = (( rgb1 & 0xFF0000) - (rgb2 & 0xFF0000)) >> 16;
		int d_Green = (( rgb1 & 0x00FF00) - (rgb2 & 0x00FF00)) >> 8;
		int d_Blue 	= ( rgb1 & 0x0000FF) - (rgb2 & 0x0000FF);
		return ((d_Red * d_Red) + (d_Blue * d_Blue) + (d_Green * d_Green));
	}

	static int cnt77=0;
	public void orderPalette()
	{
		if(cnt77==0)
		{
			cnt77++;
		}

		int 	nextColor;
		int[]  	outPalette = new int[paletteSize];  //the ordered palette

		//Init Insertions
		fStartColor = 0;

		fCycle 	= new int[paletteSize];
		fDist 	= new int[paletteSize];
		for (int i = 0; i < paletteSize; i++) 
		{
			fCycle[i] = -1;
			fDist[i] = DistanceRGBs(inPalette[0],inPalette[i]);
		}
		fCycle[0] 	= fStartColor;
		minDistance = 0;

		//Repeat Insertions		
		for (int i = 1; i < paletteSize; i++)  //Put remaining elements in order
		{
			fIteration = i;
			nextColor = findNextColor();
			insertColor(nextColor);

			// Initialize for the next iteration
			for (int j = 0; j<paletteSize; j++)
			{
				if (fCycle[j] == -1)
				{
					int test  = DistanceRGBs(inPalette[nextColor],inPalette[j]);
					if (test < fDist[j]) fDist[j] = test;
				}
			}
		}

		int index;
		index = fStartColor;
		for (int i = 0; i< paletteSize; i++)
		{
			outPalette[i] = inPalette[index];
			index = fCycle[index];
		}

		inPalette = outPalette;

		if(cnt77==0)
		{
			cnt77++;
		}
	}

	/**
	Finds farthest unused color from current color.
	 */
	static int cnt727 = 0;
	private final int findNextColor() //uses fCycle, fDist ,paletteSize                      
	{
		if(cnt727==0)
		{
			cnt727++;
		}

		int MaxDist 	= Integer.MIN_VALUE;
		int farthest 	= 0;

		for (int j = 0; j < paletteSize; j++)
		{
			if (fCycle[j] == -1)
			{
				if (fDist[j] > MaxDist) 
				{
					MaxDist = fDist[j];
					farthest = j;
				}
			}
		}
		return farthest;
	}

	private final void insertColor (int nextColor)
	{
		int insCost, newCost;
		int index, nextIndex, end1, end2, j;

		insCost = Integer.MAX_VALUE;
		index = fStartColor;
		end2 = 0;
		end1 = 0;

		for (j = 0; j <= fIteration; j++)  //For each color in the current iteration
		{
			nextIndex = fCycle[index]; 		//Get the next index (starting at the starting color)
			int d1 = DistanceRGBs(inPalette[index],inPalette[nextColor]);
			int d2 = DistanceRGBs(inPalette[index],inPalette[nextIndex]);

			if (index != nextIndex)  //Calculate the "cost" of inserting the index color
			{
				newCost = d1 + DistanceRGBs(inPalette[nextColor],inPalette[nextIndex]) - d2;
			}
			else
			{
				newCost = d1 + d2;
			}

			if (newCost < insCost)   //find the lowest insertion cost
			{
				insCost = newCost;
				end1 = index;
				end2 = nextIndex;
			}
			index = nextIndex;
		}

		fCycle[nextColor] = end2;
		fCycle[end1] = nextColor;

		minDistance += insCost; //add insCost to the minimum distance
	}
}
