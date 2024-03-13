/*
 * zbynek-music-tool - various music files manipulation utilities
 *
 * Copyright 2024-2024 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/zbynek-music-utils/ https://www.linkedin.com/in/zbynek-vyskovsky/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kvr000.zbynekmusicutils.musictool.svg;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.IOUtils;
import org.apache.fop.svg.PDFTranscoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SvgPdfFormatter
{
	public static final boolean HAS_INKSCAPE = checkInkspacePresence();

	public void transform(File input, OutputStream output) throws IOException
	{
		if (HAS_INKSCAPE) {
			try (InputStream stream = runProgram(0, input, new String[]{
				"inkscape", "-o",  "-",  "--export-type", "pdf", "--", input.toString()
			})) {
				IOUtils.copy(stream, output);
				output.close();
			}
		}
		else {
			Transcoder transcoder = new PDFTranscoder();
			try (InputStream stream = new FileInputStream(input)) {
				TranscoderInput transcoderInput = new TranscoderInput(stream);
				TranscoderOutput transcoderOutput = new TranscoderOutput(output);
					transcoder.transcode(transcoderInput, transcoderOutput);
			}
			catch (TranscoderException e) {
				throw new IOException(e);
			}
		}
	}

	private static boolean checkInkspacePresence()
	{
		try (InputStream input = runProgram(0, null, new String[]{ "inkscape", "--version" })) {
			input.readAllBytes();
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}

	private static InputStream runProgram(int maxExit, File input, String[] command) throws IOException
	{
		Process proc = new ProcessBuilder()
			.command(command)
			.redirectOutput(ProcessBuilder.Redirect.PIPE)
			.redirectInput(input != null ? ProcessBuilder.Redirect.from(input) : ProcessBuilder.Redirect.PIPE)
			.start();

		if (input == null) {
			proc.getOutputStream().close();
		}

		return new FilterInputStream(proc.getInputStream()) {
			@Override
			public int read() throws IOException {
				byte[] b = new byte[1];
				int read = read(b);
				if (read < 0) {
					checkExitCode();
				}
				return read;
			}

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				int read = in.read(b, off, len);
				if (read < 0) {
					checkExitCode();
				}
				return read;
			}

			private void checkExitCode() throws IOException
			{
				try {
					int exit = proc.waitFor();
					if (exit > maxExit) {
						throw new IOException("Process exited with error: exit=" + exit + " : " + String.join(" ", command));
					}
				}
				catch (InterruptedException e) {
					throw new IOException(e);
				}
			}
		};
	}
}
