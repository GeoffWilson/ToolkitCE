package uk.co.tkce.engine.VGM;// Video game music player that runs emulator and plays through speaker
// http://www.slack.net/~ant/

/* Load a music file into player, then start a track. Volume can be
adjusted, track can be paused and resumed, a new track can be started,
or a new file can be loaded at any time.

The file is specified as an HTTP address and optional filename to use
if it's a ZIP archive. To avoid loading file more than necessary over
HTTP, the most recently loaded file is kept in memory and a load request
for the same URL is eliminated. This allows a web page to switch between
several tracks in a ZIP archive or of a multi-track music file, without
having to keep track of whether the file was already loaded. */

import javax.sound.sampled.*;
import java.io.*;

/* Copyright (C) 2007-2008 Shay Green. This module is free software; you
can redistribute it and/or modify it under the terms of the GNU Lesser
General Public License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version. This
module is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
details. You should have received a copy of the GNU Lesser General Public
License along with this module; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA */

public class VGMPlayer extends EmuPlayer
{
	int sampleRate;
	
	public VGMPlayer( int sampleRate ) { this.sampleRate = sampleRate; }
	
	// Stops playback and loads file from given URL (HTTP only).
	// If it's an archive (.zip) then path specifies the file within
	// the archive.
	public void loadFile( String url, String path ) throws Exception
	{
		stop();
		
		if ( !loadedUrl.equals( url ) || !loadedPath.equals( path ) )
		{
			byte [] data = readFile( url, path );
			
			String name = url.toUpperCase();
			if ( name.endsWith( ".ZIP" ) )
				name = path.toUpperCase();
			
			if ( name.endsWith( ".GZ" ) )
				name = name.substring( 0, name.length() - 3 );
			
			MusicEmu emu = createEmu( name );
			if ( emu == null )
				return; // TODO: throw exception?
			int actualSampleRate = emu.setSampleRate( sampleRate );
			emu.loadFile( data );
			
			// now that new emulator is ready, replace old one
			setEmu( emu, actualSampleRate );
			loadedUrl  = url;
			loadedPath = path;
		}
	}
	
	// Stops and closes current file and unloads things from memory
	void closeFile() throws Exception
	{
		stop();
		setEmu( null, 0 );
		archiveUrl  = "";
		archiveData = null;
		loadedUrl   = "";
		loadedPath  = "";
	}
	
// private
	
	String loadedUrl  = ""; // URL and path of file loaded into emulator
	String loadedPath = "";
	
	String archiveUrl = ""; // URL of (ZIP) file cached in archiveData
	byte [] archiveData;
	
	// Creates appropriate emulator for given filename
	MusicEmu createEmu( String name )
	{
		if ( name.endsWith( ".VGM" ) || name.endsWith( ".VGZ" ) )
			return new VgmEmu();
		
		if ( name.endsWith( ".GBS" ) )
			return new GbsEmu();
		
		if ( name.endsWith( ".NSF" ) )
			return new NsfEmu();
		
		if ( name.endsWith( ".SPC" ) )
			return new SpcEmu();
		
		return null;
	}
			
	// Loads given URL and file within archive, and caches archive for future access
	byte [] readFile( String url, String path ) throws Exception
	{
		InputStream in = null;
		String name = url.toUpperCase();
		if ( !name.endsWith( ".ZIP" ) )
		{
			archiveData = null; // dump previously cached ZIP file
			archiveUrl  = "";
			
			in = DataReader.openHttp( url );
			//System.out.println( "Load " + url );
		}
		else
		{
			if ( !archiveUrl.equals( url ) )
			{
				archiveData = DataReader.loadData( DataReader.openHttp( url ) );
				archiveUrl = url;
				//System.out.println( "Load " + url );
			}
			
			in   = DataReader.openZip( new ByteArrayInputStream( archiveData ), path );
			name = path.toUpperCase();
			//System.out.println( "Unzip " + url );
		}
		
		if ( name.endsWith( ".GZ" ) || name.endsWith( ".VGZ" ) )
			in = DataReader.openGZIP( in );
		
		return DataReader.loadData( in );
	}
}
