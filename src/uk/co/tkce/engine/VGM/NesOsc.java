package uk.co.tkce.engine.VGM;

public class NesOsc
{
	static final int squareUnit   = (int) (0.125 / 15  * 65535);
	static final int triangleUnit = (int) (0.150 / 15  * 65535);
	static final int noiseUnit    = (int) (0.095 / 15  * 65535);
	static final int dmcUnit      = (int) (0.450 / 127 * 65535);

	final int [] regs = new int [4];
	final boolean [] regWritten = new boolean [4];
	int lengthCounter;// length counter (0 if unused by oscillator)
	int delay;        // delay until next (potential) transition
	int lastAmp;     // last amplitude oscillator was outputting

	void clockLength( int halt_mask )
	{
		if ( lengthCounter != 0 && (regs [0] & halt_mask) == 0 )
			lengthCounter--;
	}

	int period() { return (regs [3] & 7) * 0x100 + (regs [2] & 0xFF); }

	void reset()
	{
		delay = 0;
		lastAmp = 0;
	}

	int updateAmp( int amp )
	{
		int delta = amp - lastAmp;
		lastAmp = amp;
		return delta;
	}
}
