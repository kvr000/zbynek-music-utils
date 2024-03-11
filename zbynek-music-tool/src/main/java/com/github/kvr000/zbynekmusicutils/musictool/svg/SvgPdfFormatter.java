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

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SvgPdfFormatter
{
	public void transform(InputStream input, OutputStream output) throws IOException
	{
		Transcoder transcoder = new PDFTranscoder();
		TranscoderInput transcoderInput = new TranscoderInput(input);
		TranscoderOutput transcoderOutput = new TranscoderOutput(output);
		try {
			transcoder.transcode(transcoderInput, transcoderOutput);
		}
		catch (TranscoderException e) {
			throw new IOException(e);
		}

	}
}
