/*
 * zbynek-music-tool - various music files manipulation utilities
 *
 * Copyright 2024-2024 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/kvr000/zbynek-music-utils/ https://www.linkedin.com/in/zbynek-vyskovsky/
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

package com.github.kvr000.zbynekmusicutils.musictool;

import com.github.kvr000.zbynekmusicutils.musictool.command.SvgToPdfCommand;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.dryuf.cmdline.app.AppContext;
import net.dryuf.cmdline.app.BeanFactory;
import net.dryuf.cmdline.app.CommonAppContext;
import net.dryuf.cmdline.app.guice.GuiceBeanFactory;
import net.dryuf.cmdline.command.AbstractParentCommand;
import net.dryuf.cmdline.command.Command;
import net.dryuf.cmdline.command.CommandContext;
import net.dryuf.cmdline.command.HelpOfHelpCommand;
import net.dryuf.cmdline.command.RootCommandContext;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Map;


/**
 * ZbynekMusicTool entry point.  This class only executes subcommands.
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Log4j2
public class ZbynekMusicTool extends AbstractParentCommand
{
	private Options options;

	public static void main(String[] args)
	{
		runMain(args, (args0) -> {
			AppContext appContext = new CommonAppContext(Guice.createInjector(new GuiceModule()).getInstance(BeanFactory.class));
			return appContext.getBeanFactory().getBean(ZbynekMusicTool.class).run(
				new RootCommandContext(appContext).createChild(null, "zbynek-music-tool", null),
				Arrays.asList(args0)
			);
		});
	}

	protected CommandContext createChildContext(CommandContext commandContext, String name, boolean isHelp)
	{
		return commandContext.createChild(this, name, Map.of(Options.class, options));
	}

	@Override
	protected boolean parseOption(CommandContext context, String arg, ListIterator<String> args) throws Exception
	{
		switch (arg) {
		case "-o":
			options.output = needArgsParam(options.output, args);
			return true;

		default:
			return super.parseOption(context, arg, args);
		}
	}

	@Override
	public void createOptions(CommandContext context)
	{
		this.options = new Options();
	}

	@Override
	protected String configHelpTitle(CommandContext context)
	{
		return "zbynek-music-tool - various music manipulation tools";
	}

	@Override
	protected Map<String, String> configOptionsDescription(CommandContext context)
	{
		return ImmutableMap.of(
			"-o output", "output filename"
		);
	}

	@Override
	protected Map<String, Class<? extends Command>> configSubCommands(CommandContext context)
	{
		return ImmutableMap.<String, Class<? extends Command>>builder()
			.put("svg-to-pdf", SvgToPdfCommand.class)
			.put("help", HelpOfHelpCommand.class)
			.build();
	}

	protected Map<String, String> configCommandsDescription(CommandContext context)
	{
		return ImmutableMap.<String, String>builder()
			.put("svg-to-pdf", "Convert SVG inputs to PDF file")
			.put("help [command]", "Prints help")
			.build();
	}

	@Data
	public static class Options
	{
		String output;
	}

	public static class GuiceModule extends AbstractModule
	{
		@Override
		@SneakyThrows
		protected void configure()
		{
		}

		@Provides
		public BeanFactory beanFactory(Injector injector)
		{
			return new GuiceBeanFactory(injector);
		}
	}
}
