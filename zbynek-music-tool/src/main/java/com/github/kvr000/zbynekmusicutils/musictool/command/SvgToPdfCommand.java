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

import com.github.kvr000.zbynekmusicutils.musictool.TimeFormat;
import com.github.kvr000.zbynekmusicutils.musictool.ZbynekMusicTool;
import com.github.kvr000.zbynekmusicutils.musictool.svg.SvgPdfFormatter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dryuf.base.function.ThrowingCallable;
import net.dryuf.base.function.ThrowingConsumer;
import net.dryuf.cmdline.command.AbstractCommand;
import net.dryuf.cmdline.command.CommandContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import javax.inject.Inject;
import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;


@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SvgToPdfCommand extends AbstractCommand
{
	private final SvgPdfFormatter svgPdfFormatter;

	private final TimeFormat timeFormat;

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
			case "--since" -> {
				options.since = timeFormat.parseLast(needArgsParam(options.since, args), ZoneId.systemDefault());
				return true;
			}
			case "--till" -> {
				options.till = timeFormat.parseLast(needArgsParam(options.till, args), ZoneId.systemDefault());
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
		if (options.since != null) {
			final Stream<File> stream;
			if (options.inputs.isEmpty()) {
				stream = FileUtils.listFiles(new File("."), new String[]{ "svg" }, false).stream();
			}
			else {
				stream = options.inputs.stream()
					.map(File::new);
			}
			options.inputs = stream
				.map(f -> Pair.of(f, f.lastModified()))
				.filter(p ->
					!options.since.isAfter(Instant.ofEpochMilli(p.getRight())) &&
						(options.till == null || options.till.isAfter(Instant.ofEpochMilli(p.getRight())))
				)
				.sorted(Comparator.comparing(Pair::getRight))
				.map(p -> p.getLeft().toString())
				.collect(ImmutableList.toImmutableList());
		}
		else if (options.till != null) {
			return usage(context, "Option --till can be specified only with --since");
		}
		else if (options.inputs.isEmpty()) {
			return usage(context, "Input files are required");
		}
		return EXIT_CONTINUE;
	}

	@Override
	public int execute() throws Exception
	{
		try (
			PDDocument doc = new PDDocument();
			ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
		) {
			PDFMergerUtility merger = new PDFMergerUtility();
			merger.setDocumentMergeMode(PDFMergerUtility.DocumentMergeMode.OPTIMIZE_RESOURCES_MODE);

			PDDocumentInformation metadata = new PDDocumentInformation();
			if (options.title != null) {
				metadata.setTitle(options.title);
			}
			doc.setDocumentInformation(metadata);

			options.inputs.stream()
				.map(input -> CompletableFuture.supplyAsync(ThrowingCallable.sneakySupplier(() -> {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					svgPdfFormatter.transform(new File(input), output);
					return output.toByteArray();
				}), executor))
				.toList()
				.forEach(ThrowingConsumer.sneaky(future -> {
					try (PDDocument source = Loader.loadPDF(future.join())) {
						merger.appendDocument(doc, source);
					}
				}));
			doc.save(new File(mainOptions.getOutput()));
		}

		return 0;
	}

	@Override
	protected Map<String, String> configOptionsDescription(CommandContext context)
	{
		return ImmutableMap.of(
			"--since time", "filter files since specified time, can be yyyy-MM-ddThh:mm:ss or [N:]N{d|h|m|s}",
			"--till time", "filter files till specified time exclusive, can be yyyy-MM-ddThh:mm:ss or [N:]N{d|h|m|s}"
		);
	}

	public static class Options
	{
		List<String> inputs;

		String title;

		Instant since;

		Instant till;
	}
}
