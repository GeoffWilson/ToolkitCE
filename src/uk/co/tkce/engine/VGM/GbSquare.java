package uk.co.tkce.engine.VGM;

public class GbSquare extends GbEnv
{
	int phase;

	final int period() { return (2048 - frequency()) * 4; }

	void reset()
	{
		phase = 0;
		super.reset();
		delay = 0x40000000; // TODO: less hacky (never clocked until first trigger)
	}

	boolean write_register( int frame_phase, int reg, int old_data, int data )
	{
		boolean result = super.write_register( frame_phase, reg, old_data, data );
		if ( result )
			delay = period();
		return result;
	}

	static final byte [] duty_offsets = { 1, 1, 3, 7 };
	static final byte [] duties = { 1, 2, 4, 6 };

	void run( int time, int end_time )
	{
		final int duty_code = regs [1] >> 6;
		final int duty_offset = duty_offsets [duty_code];
		final int duty = duties [duty_code];
		int playing = 0;
		int amp = 0;
		int phase = (this.phase + duty_offset) & 7;

		if ( output != null )
		{
			if ( volume != 0 )
			{
				playing = -enabled;

				if ( phase < duty )
					amp = volume & playing;

				// Treat > 16 kHz as DC
				if ( frequency() > 2041 && delay < 32 )
				{
					amp = (volume * duty) >> 3 & playing;
					playing = 0;
				}
			}

			if ( dac_enabled() == 0 )
			{
				playing = 0;
				amp = 0;
			}
			else
			{
				amp -= dac_bias;
			}

			int delta = amp - last_amp;
			if ( delta != 0 )
			{
				last_amp = amp;
				output.addDelta( time, delta * vol_unit );
			}
		}

		time += delay;
		if ( time < end_time )
		{
			final int period = this.period();
			if ( playing == 0 )
			{
				// maintain phase
				int count = (end_time - time + period - 1) / period;
				phase = (phase + count) & 7;
				time += count * period;
			}
			else
			{
				final BlipBuffer output = this.output;
				// TODO: eliminate ugly +dac_bias -dac_bias adjustments
				int delta = ((amp + dac_bias) * 2 - volume) * vol_unit;
				do
				{
					if ( (phase = (phase + 1) & 7) == 0 || phase == duty )
						output.addDelta( time, delta = -delta );
				}
				while ( (time += period) < end_time );

				last_amp = (delta < 0 ? 0 : volume) - dac_bias;
			}
			this.phase = (phase - duty_offset) & 7;
		}
		delay = time - end_time;
	}
}
