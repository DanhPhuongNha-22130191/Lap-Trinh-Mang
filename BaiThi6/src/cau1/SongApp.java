package cau1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SongApp {
	private List<Song> songs;
	private RandomAccessFile raf;
	private final int NAME_SIZE = 25;
	int totalSongs;
	int recSize;

	public SongApp(String path, List<Song> songs) throws IOException {
		this.songs = songs;
		raf = new RandomAccessFile(path, "rw");
		if (raf.length() == 0) {
			totalSongs = 0;
			recSize = NAME_SIZE * 2 + 1 + 1 + 4;
			raf.writeInt(totalSongs);
			raf.writeInt(recSize);
		} else {
			raf.seek(0);
			totalSongs = raf.readInt();
			recSize = raf.readInt();
		}
	}

	public void addSong(Song song) throws IOException {
		raf.seek(raf.length());
		song.writeSong(raf, NAME_SIZE);
		totalSongs++;
		raf.seek(0);
		raf.writeInt(totalSongs);
	}

	public boolean delete(String name) throws IOException {
		for (int i = 0; i < totalSongs; i++) {
			raf.seek(8 + i * recSize);
			Song s = Song.read(raf, NAME_SIZE);
			if (!s.deleted && name.equals(s.name)) {
				raf.seek(8 + i * recSize);
				raf.writeByte(1);
				return true;
			}
		}
		return false;
	}

	public List<Song> list() throws IOException {
		List<Song> listSongs = new ArrayList<Song>();
		for (int i = 0; i < totalSongs; i++) {
			raf.seek(8 + i * recSize);
			Song s = Song.read(raf, NAME_SIZE);
			if (!s.deleted) {
				listSongs.add(s);
			}
		}
		return listSongs;
	}

	public Song get(int index) throws IOException {
		if (index < 0 || index >= totalSongs)
			return null;
		raf.seek(8 + index * recSize);
		Song s = Song.read(raf, NAME_SIZE);
		return s.deleted ? null : s;
	}

	public boolean update(int index, Song newSong) throws IOException {
		if (index < 0 || index >= totalSongs)
			return false;
		raf.seek(8 + index * recSize);
		Song s = Song.read(raf, NAME_SIZE);
		if (s.deleted)
			return false;
		raf.seek(8 + index * recSize);
		newSong.writeSong(raf, NAME_SIZE);
		return true;
	}

	public static void main(String[] args) throws IOException {
		String path = "E://PlayList.data";
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}

		List<Song> songs = new ArrayList<Song>();
		SongApp app = new SongApp(path, songs);
//        app.addSong(new Song(false, (byte) 2, "RAIN", 120.5f));
        System.out.println(app.get(0));
//		System.out.println(app.update(0, new Song(false, (byte) 1, "WANT", 120.5f)));
//		for (Song song : app.list()) {
//			System.out.println(song);
//		}
	}
}
