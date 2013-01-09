package uk.co.tkce.engine.VGM;

public class GbOsc
{
	static final boolean gbc_02 = false; // TODO: allow to be set?
	static final int trigger_mask   = 0x80;
	static final int length_enabled = 0x40;
	static final int dac_bias       = 7;

	BlipBuffer output;
	int output_select;
	final int [] regs = new int [5];

	int vol_unit;
	int delay;
	int last_amp;
	int length;
	int enabled;

	void reset()
	{
		output        = null;
		output_select = 0;
		delay         = 0;
		last_amp      = 0;
		length        = 64;
		enabled       = 0;

		for ( int i = 5; --i >= 0; )
			regs [i] = 0;
	}

	void clock_length()
	{
		if ( (regs [4] & length_enabled) != 0 && length != 0 )
		{
			if ( --length <= 0 )
				enabled = 0;
		}
	}

	int frequency() { return (regs [4] & 7) * 0x100 + regs [3]; }

	boolean write_register( int frame_phase, int reg, int old_data, int data ) { return false; }

	int write_trig( int frame_phase, int max_len, int old_data )
	{
		int data = regs [4];

		if ( gbc_02 && (frame_phase & 1) != 0 && (old_data & length_enabled) == 0 && length != 0 )
			length--;

		if ( (data & trigger_mask) != 0 )
		{
			enabled = 1;
			if ( length == 0 )
			{
				length = max_len;
				if ( gbc_02 && (frame_phase & 1) != 0 && (data & length_enabled) != 0 )
					length--;
			}
		}

		if ( gbc_02 && length == 0 )
			enabled = 0;

		return data & trigger_mask;
	}
}
