package uk.co.tkce.engine.VGM;// Nintendo NES sound chip emulator
// http://www.slack.net/~ant/

/* Copyright (C) 2003-2010 Shay Green. This module is free software; you
can redistribute it and/or modify it under the terms of the GNU Lesser
General Public License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version. This
module is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
details. You should have received a copy of the GNU Lesser General Public
License along with this module; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA */

import uk.co.tkce.engine.VGM.BlipBuffer;

public final class NesApu
{
	public NesApu()
	{
		oscs [0] = square1;
		oscs [1] = square2;
		oscs [2] = triangle;
		oscs [3] = noise;
		oscs [4] = dmc;
	}
	
	public void setOutput( BlipBuffer b ) { output = b; }
	
	// Resets oscillators and internal state
	public void reset( NesCpu cpu, boolean palMode )
	{
		dmc.cpu     = cpu;
		dmc.palMode = palMode;
		
		framePeriod    = palMode ? 8314 : 7458;
		frameTime      = framePeriod;
		lastTime       = 0;
		irqFlag        = 0;
		dmc.oscEnables = 0;
		
		square1 .reset();
		square2 .reset();
		triangle.reset();
		noise   .reset();
		dmc     .reset();
		
		write( 0, 0x4017, 0x00 );
		write( 0, 0x4015, 0x00 );
		
		for ( int addr = 0x4000; addr <= 0x4013; addr++ )
			write( 0, addr, (addr & 3) != 0 ? 0x00 : 0x10 );
		
		dmc.lastAmp = dmc.dac = 0; // prevents click
	}
	
	// Writes data to address at specified time
	public static final int startAddr = 0x4000;
	public static final int endAddr   = 0x4017;
	public void write( int time, int addr, int data )
	{
		assert 0 <= data && data < 0x100;
		assert startAddr <= addr && addr <= endAddr;
		
		runUntil( time );
		
		if ( addr < 0x4014 )
		{
			// Write to channel
			int index = (addr - startAddr) >> 2;
			NesOsc osc = oscs [index];
			
			int reg = addr & 3;
			osc.regs       [reg] = data;
			osc.regWritten [reg] = true;
			
			if ( index == 4 )
			{
				// handle DMC specially
				dmc.write_register( reg, data );
			}
			else if ( reg == 3 )
			{
				// load length counter
				if ( (dmc.oscEnables >> index & 1) != 0 )
					osc.lengthCounter = length_table [data >> 3 & 0x1F];
				
				// reset square phase
				if ( index < 2 )
					((NesSquare) osc).phase = NesSquare.phaseRange - 1;
			}
		}
		else if ( addr == 0x4015 )
		{
			// Channel enables
			for ( int i = oscCount; i-- > 0; )
				if ( (data >> i & 1) == 0 )
					oscs [i].lengthCounter = 0;
			
			dmc.irqFlag = 0;
			
			int justEnabled = data & ~dmc.oscEnables;
			dmc.oscEnables = data;
			
			if ( (justEnabled & 0x10) != 0 )
				dmc.start();
		}
		else if ( addr == 0x4017 )
		{
			// Frame mode
			frameMode = data;
			
			if ( (data & 0x40) != 0 )
				irqFlag = 0;
			
			// mode 1
			frameTime = time;
			framePhase = 0;
			
			if ( (data & 0x80) == 0 )
			{
				// mode 0
				framePhase = 1;
				frameTime += framePeriod;
			}
		}

	}
	
	// Reads from status register at specified time
	public int read( int time )
	{
		runUntil( time );
		
		int result = (dmc.irqFlag << 7) | (irqFlag << 6);
		irqFlag = 0;
		
		for ( int i = 0; i < oscCount; i++ )
			if ( oscs [i].lengthCounter != 0 )
				result |= 1 << i;
		
		return result;
	}
	
	// Runs all oscillators up to specified time, ends current time frame, then
	// starts a new frame at time 0
	public void endFrame( int endTime )
	{
		if ( endTime > lastTime )
			runUntil( endTime );
		
		assert frameTime >= endTime;
		frameTime -= endTime;
		
		assert lastTime >= endTime;
		lastTime -= endTime;
	}
	
	static final int [] length_table = {
		0x0A, 0xFE, 0x14, 0x02, 0x28, 0x04, 0x50, 0x06,
		0xA0, 0x08, 0x3C, 0x0A, 0x0E, 0x0C, 0x1A, 0x0E, 
		0x0C, 0x10, 0x18, 0x12, 0x30, 0x14, 0x60, 0x16,
		0xC0, 0x18, 0x48, 0x1A, 0x10, 0x1C, 0x20, 0x1E
	};
	
	static final int oscCount = 5;
	final NesOsc [] oscs = new NesOsc [oscCount];
	final NesSquare   square1  = new NesSquare();
	final NesSquare   square2  = new NesSquare();
	final NesTriangle triangle = new NesTriangle();
	final NesNoise    noise    = new NesNoise();
	final NesDmc      dmc      = new NesDmc();
	BlipBuffer output;
	int framePeriod;
	int frameTime;
	int framePhase;
	int lastTime;
	int frameMode;
	int irqFlag;
	
	void runUntil( int endTime )
	{
		assert endTime >= lastTime; // endTime must not be before previous time
		if ( endTime == lastTime )
			return;
		
		while ( true )
		{
			// run oscillators
			int time = endTime;
			if ( time > frameTime )
				time = frameTime;
			
			square1 .run( output, lastTime, time );
			square2 .run( output, lastTime, time );
			triangle.run( output, lastTime, time );
			noise   .run( output, lastTime, time );
			dmc     .run( output, lastTime, time );
			lastTime = time;
			
			if ( time == endTime )
				break;
			
			// run frame sequencer
			frameTime += framePeriod;
			switch ( framePhase++ )
			{
			case 0:
				if ( (frameMode & 0xC0) == 0 )
					irqFlag = 1;
			case 2:
				// 120 Hz
				square1 .clockLength( 0x20 );
				square2 .clockLength( 0x20 );
				triangle.clockLength( 0x80 ); // different bit for halt flag on triangle
				noise   .clockLength( 0x20 );
				
				square1.clockSweep( -1 );
				square2.clockSweep(  0 );
				break;
			
			case 3:
				// 60 Hz
				framePhase = 0;
				if ( (frameMode & 0x80) != 0 )
					frameTime += framePeriod; // frame 3 is almost twice as long in mode 1
				break;
			}
			
			// 240 Hz
			square1 .clockEnvelope();
			square2 .clockEnvelope();
			triangle.clockLinearCounter();
			noise   .clockEnvelope();
		}
	}
}
