package cau1;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Song {
	boolean deleted;
	byte genre;
	String name;
	float size;

	public Song(boolean deleted, byte genre, String string, float size) {
		super();
		this.deleted = deleted;
		this.genre = genre;
		this.name = string;
		this.size = size;
	}

	@Override
	public String toString() {
		return deleted + "\t" + genre + "\t" + name + "\t" + size;
	}

	public void writeSong(RandomAccessFile raf, int songNameLen) throws IOException {
		raf.writeByte(deleted ? 1 : 0);
		raf.writeByte(genre);
		writeSongName(raf, name, songNameLen);
		raf.writeFloat(size);
	}

	public void writeSongName(RandomAccessFile raf, String name, int songNameLen) throws IOException {
		for (int i = 0; i < songNameLen; i++) {
			if (i >= name.length())
				raf.writeChar(0);
			else
				raf.writeChar(name.charAt(i));
		}

	}

	public static Song read(RandomAccessFile raf, int songNameLen) throws IOException {
		boolean deleted = raf.readByte() == 1;
		byte genre = raf.readByte();
		String songName = readSongName(raf, songNameLen);
		float size = raf.readFloat();
		return new Song(deleted, genre, songName, size);

	}

	public static String readSongName(RandomAccessFile raf, int songNameLen) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < songNameLen; i++) {
			char c = raf.readChar();
			if (c == 0)
				continue;
			sb.append(c);
		}
		return sb.toString();
	}

}
