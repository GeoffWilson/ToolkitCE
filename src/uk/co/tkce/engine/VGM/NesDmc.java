package uk.co.tkce.engine.VGM;

public final class NesDmc extends NesOsc
{
	static final int loop_flag = 0x40;

	int address;    // address of next byte to read
	int period;
	//int length_counter; // bytes remaining to play (already defined in NesOsc)
	int buf;
	int bits_remain;
	int bits;
	boolean buf_full;
	boolean silence;
	int dac;
	int irqEnabled;
	int irqFlag;
	boolean palMode;

	// in DMC since it needs to clear it
	int oscEnables;
	NesCpu cpu;

	void reset()
	{
		address = 0;
		dac = 0;
		buf = 0;
		bits_remain = 1;
		bits = 0;
		buf_full = false;
		silence = true;

		irqFlag    = 0;
		irqEnabled = 0;

		super.reset();
		period = 0x1AC;
	}

	static final int [] dmc_period_table = {
		428, 380, 340, 320, 286, 254, 226, 214, // NTSC
		190, 160, 142, 128, 106,  84,  72,  54,

		398, 354, 316, 298, 276, 236, 210, 198, // PAL
		176, 148, 132, 118,  98,  78,  66,  50
	};

	void reload_sample()
	{
		address = 0x4000 + regs [2] * 0x40;
		lengthCounter = regs [3] * 0x10 + 1;
	}

	static final int [] dac_table =
	{
		 0, 1, 2, 3, 4, 5, 6, 7, 7, 8, 9,10,11,12,13,14,
		15,15,16,17,18,19,20,20,21,22,23,24,24,25,26,27,
		27,28,29,30,31,31,32,33,33,34,35,36,36,37,38,38,
		39,40,41,41,42,43,43,44,45,45,46,47,47,48,48,49,
		50,50,51,52,52,53,53,54,55,55,56,56,57,58,58,59,
		59,60,60,61,61,62,63,63,64,64,65,65,66,66,67,67,
		68,68,69,70,70,71,71,72,72,73,73,74,74,75,75,75,
		76,76,77,77,78,78,79,79,80,80,81,81,82,82,82,83,
	};

	void write_register( int addr, int data )
	{
		if ( addr == 0 )
		{
			period = dmc_period_table [(data & 15) + (palMode ? 16 : 0)];

			irqEnabled = 1;
			if ( (data & 0xC0) != 0x80 )
			{
				irqEnabled = 0;
				irqFlag    = 0;
			}
		}
		else if ( addr == 1 )
		{
			// adjust lastAmp so that "pop" amplitude will be properly non-linear
			// with respect to change in dac
			data &= 0x7F;
			lastAmp = data - dac_table [data] + dac_table [dac];
			dac = data;
		}
	}

	void start()
	{
		reload_sample();
		fill_buffer();
	}

	void fill_buffer()
	{
		if ( !buf_full && lengthCounter != 0 )
		{
			// Read byte via CPU
			buf = cpu.cpuRead( 0x8000 + address );
			address = (address + 1) & 0x7FFF;
			buf_full = true;

			if ( --lengthCounter == 0 ) // Reached end of sample
			{
				if ( (regs [0] & loop_flag) != 0 )
				{
					reload_sample();
				}
				else
				{
					oscEnables &= ~0x10;
					irqFlag = irqEnabled;
				}
			}
		}
	}

	void run( BlipBuffer output, int time, int endTime )
	{
		int delta = updateAmp( dac );
		if ( delta != 0 )
			output.addDelta( time, delta * dmcUnit );

		time += delay;
		if ( time < endTime )
		{
			if ( silence && !buf_full )
			{
				int count = (endTime - time + period - 1) / period;
				bits_remain = (bits_remain - 1 + 8 - (count % 8)) % 8 + 1;
				time += count * period;
			}
			else
			{
				do
				{
					if ( !silence )
					{
						int step;
						int newDac = dac + (step = (bits << 2 & 4) - 2);
						// if ( newDac >= 0 && newDac <= 0x7F )
						if ( (byte) newDac >= 0 )
						{
							dac = newDac;
							output.addDelta( time, step * dmcUnit );
						}
						bits >>= 1;
					}

					if ( --bits_remain == 0 )
					{
						bits_remain = 8;
						silence = true;
						if ( buf_full )
						{
							buf_full = false;
							silence  = false;
							bits     = buf;
							fill_buffer();
						}
					}
				}
				while ( (time += period) < endTime );

				lastAmp = dac;
			}
		}
		delay = time - endTime;
	}
}
