package uk.co.tkce.engine.VGM;

public final class GbWave extends GbOsc
{
	int wave_pos;
	int sample_buf_high;
	int sample_buf;
	static final int wave_size = 32;
	int [] wave = new int [wave_size];

	int period() { return (2048 - frequency()) * 2; }
	int dac_enabled() { return regs [0] & 0x80; }

	int access( int addr )
	{
		if ( enabled != 0 )
			addr = 0xFF30 + (wave_pos >> 1);
		return addr;
	}

	void reset()
	{
		wave_pos = 0;
		sample_buf_high = 0;
		sample_buf = 0;
		length = 256;
		super.reset();
	}

	boolean write_register( int frame_phase, int reg, int old_data, int data )
	{
		final int max_len = 256;

		switch ( reg )
		{
		case 1:
			length = max_len - data;
			break;

		case 4:
			if ( write_trig( frame_phase, max_len, old_data ) != 0 )
			{
				wave_pos = 0;
				delay    = period() + 6;
				sample_buf = sample_buf_high;
			}
			// fall through
		case 0:
			if ( dac_enabled() == 0 )
				enabled = 0;
		}

		return false;
	}

	void run( int time, int end_time )
	{
		int volume_shift = regs [2] >> 5 & 3;
		int playing = 0;

		if ( output != null )
		{
			playing = -enabled;
			if ( --volume_shift < 0 )
			{
				volume_shift = 7;
				playing = 0;
			}

			int amp = sample_buf & playing;

			if ( frequency() > 0x7FB && delay < 16 )
			{
				// 16 kHz and above, act as DC at mid-level
				// (really depends on average level of entire wave,
				// but this is good enough)
				amp = 8;
				playing = 0;
			}

			amp >>= volume_shift;

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
			int wave_pos = (this.wave_pos + 1) & (wave_size - 1);
			final int period = this.period();
			if ( playing == 0 )
			{
				// maintain phase
				int count = (end_time - time + period - 1) / period;
				wave_pos += count; // will be masked below
				time += count * period;
			}
			else
			{
				final BlipBuffer output = this.output;
				int last_amp = this.last_amp + dac_bias;
				do
				{
					int amp = wave [wave_pos] >> volume_shift;
					wave_pos = (wave_pos + 1) & (wave_size - 1);
					int delta;
					if ( (delta = amp - last_amp) != 0 )
					{
						last_amp = amp;
						output.addDelta( time, delta * vol_unit );
					}
				}
				while ( (time += period) < end_time );
				this.last_amp = last_amp - dac_bias;
			}
			wave_pos = (wave_pos - 1) & (wave_size - 1);
			this.wave_pos = wave_pos;
			if ( enabled != 0 )
			{
				sample_buf_high = wave [wave_pos & ~1];
				sample_buf      = wave [wave_pos];
			}
		}
		delay = time - end_time;
	}
}
