package uk.co.tkce.engine.VGM;

public class NesEnvelope extends NesOsc
{
	int envVolume;
	int envDelay;

	void clockEnvelope()
	{
		int period = regs [0] & 15;
		if ( regWritten [3] )
		{
			regWritten [3] = false;
			envDelay = period;
			envVolume = 15;
		}
		else if ( --envDelay < 0 )
		{
			envDelay = period;
			if ( (envVolume | (regs [0] & 0x20)) != 0 )
				envVolume = (envVolume - 1) & 15;
		}
	}

	int volume()
	{
		if ( lengthCounter == 0 )
			return 0;

		if ( (regs [0] & 0x10) != 0 )
			return regs [0] & 0x0F;

		return envVolume;
	}

	void reset()
	{
		envVolume = 0;
		envDelay  = 0;
		super.reset();
	}
}
