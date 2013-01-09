package uk.co.tkce.engine.VGM;

public final class NesTriangle extends NesOsc
{
	static final int phaseRange = 16;
	int phase;
	int linearCounter;

	void reset()
	{
		linearCounter = 0;
		phase         = phaseRange;
		super.reset();
	}

	void clockLinearCounter()
	{
		if ( regWritten [3] )
			linearCounter = regs [0] & 0x7F;
		else if ( linearCounter != 0 )
			linearCounter--;

		if ( (regs [0] & 0x80) == 0 )
			regWritten [3] = false;
	}

	int calc_amp()
	{
		int amp = phaseRange - phase;
		if ( amp < 0 )
			amp = phase - (phaseRange + 1);
		return amp;
	}

	void run( BlipBuffer output, int time, int endTime )
	{
		final int timer_period = period() + 1;

		// to do: track phase when period < 3
		// to do: Output 7.5 on dac when period < 2? More accurate, but results in more clicks.

		int delta = updateAmp( calc_amp() );
		if ( delta != 0 )
			output.addDelta( time, delta * triangleUnit );

		time += delay;
		if ( lengthCounter == 0 || linearCounter == 0 || timer_period < 3 )
		{
			time = endTime;
		}
		else if ( time < endTime )
		{
			int volume = triangleUnit;
			if ( phase > phaseRange )
			{
				phase -= phaseRange;
				volume = -volume;
			}

			do
			{
				if ( --phase != 0 )
				{
					output.addDelta( time, volume );
				}
				else
				{
					phase = phaseRange;
					volume = -volume;
				}
			}
			while ( (time += timer_period) < endTime );

			if ( volume < 0 )
				phase += phaseRange;
			lastAmp = calc_amp();
		}
		delay = time - endTime;
	}
}
