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

package com.github.kvr000.zbynekmusicutils.musictool.command;

import com.github.kvr000.zbynekmusicutils.musictool.ZbynekMusicTool;
import com.github.kvr000.zbynekmusicutils.musictool.svg.SvgPdfFormatter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dryuf.base.function.ThrowingCallable;
import net.dryuf.cmdline.command.AbstractCommand;
import net.dryuf.cmdline.command.CommandContext;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SvgToPdfCommand extends AbstractCommand
{
	private final SvgPdfFormatter svgPdfFormatter;

	private final ZbynekMusicTool.Options mainOptions;

	private Options options = new Options();

	@Override
	protected boolean parseOption(CommandContext context, String option, ListIterator<String> args) throws Exception
	{
		switch (option) {
			case "--title" -> {
				options.title = needArgsParam(options.title, args);
				return true;
			}
		}
		return super.parseOption(context, option, args);
	}

	@Override
	protected int parseNonOptions(CommandContext context, ListIterator<String> args) throws Exception
	{
		options.inputs = ImmutableList.copyOf(args);
		return EXIT_CONTINUE;
	}

	@Override
	protected int validateOptions(CommandContext context, ListIterator<String> args) throws Exception
	{
		if (mainOptions.getOutput() == null) {
			return usage(context, "-o output file is requried");
		}
		if (options.inputs.isEmpty()) {
			return usage(context, "Input files are required");
		}
		return EXIT_CONTINUE;
	}

	@Override
	public int execute() throws Exception
	{
		PDFMergerUtility merger = new PDFMergerUtility();
		try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {

			options.inputs.stream()
				.map(input -> CompletableFuture.supplyAsync(ThrowingCallable.sneakySupplier(() -> {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					try (InputStream stream = new FileInputStream(input)) {
						svgPdfFormatter.transform(stream, output);
					}
					return output.toInputStream();
				}), executor))
				.toList()
				.stream()
				.forEach(future -> merger.addSource(future.join()));
		}

		PDDocumentInformation metadata = new PDDocumentInformation();
		if (options.title != null) {
			metadata.setTitle(options.title);
		}
		merger.setDestinationDocumentInformation(metadata);
		merger.setDestinationFileName(mainOptions.getOutput());
		merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

		return 0;
	}

	@Override
	protected Map<String, String> configParametersDescription(CommandContext context)
	{
		return ImmutableMap.of(
		);
	}

	@Override
	protected Map<String, String> configOptionsDescription(CommandContext context) {
		return ImmutableMap.of(
		);
	}

	public static class Options
	{
		List<String> inputs;

		String title;
	}
}
