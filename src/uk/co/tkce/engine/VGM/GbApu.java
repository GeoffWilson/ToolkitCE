package uk.co.tkce.engine.VGM;// Nintendo Game Boy sound emulator
// http://www.slack.net/~ant/

/* Copyright (C) 2003-2007 Shay Green. This module is free software; you
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

public final class GbApu
{
	public GbApu()
	{
		oscs [0] = square1;
		oscs [1] = square2;
		oscs [2] = wave;
		oscs [3] = noise;
		
		reset();
	}
	
	// Resets oscillators and internal state
	public void setOutput( BlipBuffer center, BlipBuffer left, BlipBuffer right )
	{
		outputs [1] = right;
		outputs [2] = left;
		outputs [3] = center;
		
		for ( int i = osc_count; --i >= 0; )
			oscs [i].output = outputs [oscs [i].output_select];
	}
	
	private void update_volume()
	{
		final int unit = (int) (1.0 / osc_count / 15 / 8 * 65536);
		
		// TODO: doesn't handle left != right volume (not worth the complexity)
		int data = regs [vol_reg - startAddr];
		int left  = data >> 4 & 7;
		int right = data & 7;
		int vol_unit = (left > right ? left : right) * unit;
		for ( int i = osc_count; --i >= 0; )
			oscs [i].vol_unit = vol_unit;
	}
	
	private void reset_regs()
	{
		for ( int i = 0x20; --i >= 0; )
			regs [i] = 0;
		
		for ( int i = osc_count; --i >= 0; )
			oscs [i].reset();
		
		update_volume();
	}
	
	static final int initial_wave [] = {
		0x84,0x40,0x43,0xAA,0x2D,0x78,0x92,0x3C,
		0x60,0x59,0x59,0xB0,0x34,0xB8,0x2E,0xDA
	};
	
	public void reset()
	{
		frame_time  = 0;
		last_time   = 0;
		frame_phase = 0;
		
		reset_regs();
		
		for ( int i = 16; --i >= 0; )
			write( 0, i + wave_ram, initial_wave [i] );
	}
	
	private void run_until( int end_time )
	{
		assert end_time >= last_time; // end_time must not be before previous time
		if ( end_time == last_time )
			return;
		
		while ( true )
		{
			// run oscillators
			int time = end_time;
			if ( time > frame_time )
				time = frame_time;
			
			square1.run( last_time, time );
			square2.run( last_time, time );
			wave   .run( last_time, time );
			noise  .run( last_time, time );
			last_time = time;
			
			if ( time == end_time )
				break;
			
			// run frame sequencer
			frame_time += frame_period;
			switch ( frame_phase++ )
			{
			case 2:
			case 6:
				// 128 Hz
				square1.clock_sweep();
			case 0:
			case 4:
				// 256 Hz
				square1.clock_length();
				square2.clock_length();
				wave   .clock_length();
				noise  .clock_length();
				break;
			
			case 7:
				// 64 Hz
				frame_phase = 0;
				square1.clock_envelope();
				square2.clock_envelope();
				noise  .clock_envelope();
			}
		}
	}
	
	// Runs all oscillators up to specified time, ends current time frame, then
	// starts a new frame at time 0
	public void endFrame( int end_time )
	{
		if ( end_time > last_time )
			run_until( end_time );
		
		assert frame_time >= end_time;
		frame_time -= end_time;
		
		assert last_time >= end_time;
		last_time -= end_time;
	}
	
	static void silence_osc( int time, GbOsc osc )
	{
		int amp = osc.last_amp;
		if ( amp != 0 )
		{
			osc.last_amp = 0;
			if ( osc.output != null )
				osc.output.addDelta( time, -amp * osc.vol_unit );
		}
	}
	
	// Reads and writes at addr must satisfy start_addr <= addr <= end_addr
	public static final int startAddr = 0xFF10;
	public static final int endAddr   = 0xFF3F;
	
	public void write( int time, int addr, int data )
	{
		assert startAddr <= addr && addr <= endAddr;
		assert 0 <= data && data < 0x100;
		
		if ( addr < status_reg && (regs [status_reg - startAddr] & power_mask) == 0 )
			return;
		
		run_until( time );
		int reg = addr - startAddr;
		if ( addr < wave_ram )
		{
			int old_data = regs [reg];
			regs [reg] = data;
			
			if ( addr < vol_reg )
			{
				int index = reg / 5;
				GbOsc osc = oscs [index];
				int r = reg - index * 5;
				osc.regs [r] = data;
				osc.write_register( frame_phase, r, old_data, data );
			}
			else if ( addr == vol_reg && data != old_data )
			{
				for ( int i = osc_count; --i >= 0; )
					silence_osc( time, oscs [i] );
				
				update_volume();
			}
			else if ( addr == stereo_reg )
			{
				for ( int i = osc_count; --i >= 0; )
				{
					GbOsc osc = oscs [i];
					int bits = data >> i;
					osc.output_select = (bits >> 3 & 2) | (bits & 1);
					BlipBuffer output = outputs [osc.output_select];
					if ( osc.output != output )
					{
						silence_osc( time, osc );
						osc.output = output;
					}
				}
			}
			else if ( addr == status_reg && ((data ^ old_data) & power_mask) != 0 )
			{
				frame_phase = 0;
				if ( (data & power_mask) == 0 )
				{
					for ( int i = osc_count; --i >= 0; )
						silence_osc( time, oscs [i] );
				
					reset_regs();
				}
			}
		}
		else // wave data
		{
			addr = wave.access( addr );
			regs [addr - startAddr] = data;
			int index = (addr & 0x0F) * 2;
			wave.wave [index    ] = data >> 4;
			wave.wave [index + 1] = data & 0x0F;
		}
	}
	
	static final int masks [] = {
		0x80,0x3F,0x00,0xFF,0xBF,
		0xFF,0x3F,0x00,0xFF,0xBF,
		0x7F,0xFF,0x9F,0xFF,0xBF,
		0xFF,0xFF,0x00,0x00,0xBF,
		0x00,0x00,0x70,
		0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF
	};
	
	// Reads from address at specified time
	public int read( int time, int addr )
	{
		assert startAddr <= addr && addr <= endAddr;
		
		run_until( time );
		
		if ( addr >= wave_ram )
			addr = wave.access( addr );
		
		int index = addr - startAddr;
		int data = regs [index];
		if ( index < masks.length )
			data |= masks [index];
		
		if ( addr == status_reg )
		{
			data &= 0xF0;
			if ( square1.enabled != 0 ) data |= 1;
			if ( square2.enabled != 0 ) data |= 2;
			if ( wave   .enabled != 0 ) data |= 4;
			if ( noise  .enabled != 0 ) data |= 8;
		}
		
		return data;
	}

	static final int vol_reg    = 0xFF24;
	static final int stereo_reg = 0xFF25;
	static final int status_reg = 0xFF26;
	static final int wave_ram   = 0xFF30;
	static final int frame_period = 4194304 / 512; // 512 Hz
	
	static final int power_mask = 0x80;
	
	static final int osc_count = 4;
	final GbOsc [] oscs = new GbOsc [osc_count];
	int frame_time;
	int last_time;
	int         frame_phase;
	final BlipBuffer [] outputs = new BlipBuffer [4];
	
	final GbSweepSquare  square1 = new GbSweepSquare();
	final GbSquare       square2 = new GbSquare();
	final GbWave         wave    = new GbWave();
	final GbNoise        noise   = new GbNoise();
	final int [] regs = new int [endAddr - startAddr + 1];
}
