package abcmap.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipDirectory {

	private ArrayList<File> files;

	public ZipDirectory() {
		files = new ArrayList<File>();
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	/**
	 * Dezippe une archive dans un dossier
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public void unzipArchive(File source, File destination) throws IOException {

		// verifier l'archive source
		if (source.isFile() == false) {
			throw new IllegalArgumentException("Source archive is not a file");
		}

		// verifier le dossier destination
		if (destination.isDirectory() == false) {
			throw new IllegalArgumentException(
					"Destination directory is not a directory");
		}

		byte[] buffer = new byte[1024];
		ZipInputStream zis;
		zis = new ZipInputStream(new FileInputStream(source.getAbsolutePath()));

		files.clear();

		ZipEntry ze = zis.getNextEntry();
		while (ze != null) {

			String fileName = ze.getName();
			File newFile = new File(destination.getAbsolutePath()
					+ File.separator + fileName);

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
			}

			catch (IOException e) {
				throw e;
			}

			finally {
				try {
					fos.close();
				} catch (Exception e1) {
					throw e1;
				}
			}

			files.add(newFile);

			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
	}

	public void zipDirectory(File source, File destination, boolean overwrite)
			throws IOException {

		// verifier si le dossier source existe
		if (source.isDirectory() == false) {
			throw new IllegalArgumentException(
					"Source directory is not a directory");
		}

		String[] filesInDir = source.list();

		// verifier si le fichier destination existe, et si il doit etre ecras
		if (destination.isFile()) {
			if (overwrite == false) {
				throw new IllegalArgumentException("File already exist: "
						+ destination.getAbsolutePath());
			} else {
				destination.delete();
				destination.createNewFile();
			}
		}

		else {
			destination.createNewFile();
		}

		// vider la liste des fichiers a compresser
		files.clear();

		byte[] buffer = new byte[1024];

		FileOutputStream fos;
		ZipOutputStream zos;

		try {
			fos = new FileOutputStream(destination);
			zos = new ZipOutputStream(fos);

			for (String file : filesInDir) {

				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(source
						+ File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();

				files.add(new File(source + File.separator + file));

			}

		} catch (Exception ex) {
			throw new IOException(ex.getMessage());
		}

		try {
			zos.closeEntry();
			zos.close();
		} catch (Exception e) {
		}

	}

}
