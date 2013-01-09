package uk.co.tkce.engine.VGM;

public final class GbNoise extends GbEnv
{
	int bits;

	boolean write_register( int frame_phase, int reg, int old_data, int data )
	{
		if ( reg == 3 )
		{
			int p = period();
			if ( p != 0 )
				delay %= p; // TODO: not entirely correct
		}

		if ( super.write_register( frame_phase, reg, old_data, data ) )
			bits = 0x7FFF;

		return false;
	}

	static final byte [] noise_periods = { 8, 16, 32, 48, 64, 80, 96, 112 };

	int period()
	{
		int shift = regs [3] >> 4;
		int p = noise_periods [regs [3] & 7] << shift;
		if ( shift >= 0x0E )
			p = 0;
		return p;
	}

	void run( int time, int end_time )
	{
		int feedback = (1 << 14) >> (regs [3] & 8);
		int playing = 0;
		int amp = 0;

		if ( output != null )
		{
			if ( volume != 0 )
			{
				playing = -enabled;

				if ( (bits & 1) == 0 )
					amp = volume & playing;
			}

			if ( dac_enabled() != 0 )
			{
				amp -= dac_bias;
			}
			else
			{
				amp = 0;
				playing = 0;
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
			if ( period == 0 )
			{
				time = end_time;
			}
			else
			{
				int bits = this.bits;
				if ( playing == 0 )
				{
					// maintain phase
					int count = (end_time - time + period - 1) / period;
					time += count * period;

					// TODO: be sure this doesn't drag performance too much
					bits ^= (feedback << 1) & -(bits & 1);
					feedback *= 3;
					do
					{
						bits = (bits >> 1) ^ (feedback & -(bits & 2));
					}
					while ( --count > 0 );
					bits &= ~(feedback << 1);
				}
				else
				{
					final BlipBuffer output = this.output;
					// TODO: eliminate ugly +dac_bias -dac_bias adjustments
					int delta = ((amp + dac_bias) * 2 - volume) * vol_unit;

					do
					{
						int changed = bits + 1;
						bits >>= 1;
						if ( (changed & 2) != 0 )
						{
							bits |= feedback;
							output.addDelta( time, delta = -delta );
						}
					}
					while ( (time += period) < end_time );

					last_amp = (delta < 0 ? 0 : volume) - dac_bias;
				}
				this.bits = bits;
			}
		}
		delay = time - end_time;
	}
}
