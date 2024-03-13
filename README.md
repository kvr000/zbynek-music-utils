# Zbynek Music Utilities - zbynek-music-tool command line utility

Command line tool to manipulate music files.


## Download

- https://github.com/kvr000/zbynek-music-utils/releases/download/master/zbynek-music-tool
- https://github.com/kvr000/zbynek-music-utils/releases/tag/master


## Usage

```
Usage: zbynek-music-tool options... command...
zbynek-music-tool - various music manipulation tools

Options:
-o output    output filename

Commands:
svg-to-pdf        Convert SVG inputs to PDF file
help [command]    Prints help
```

#### General Options


### svg-to-pdf

```
Usage: zbynek-music-tool svg-to-pdf options... files...

Options:
--since time    filter files since specified time, can be yyyy-MM-ddThh:mm:ss or [N:]N{d|h|m|s}
--till time     filter files till specified time exclusive, can be yyyy-MM-ddThh:mm:ss or [N:]N{d|h|m|s}

Parameters:
files...    input files (defaults to *.svg if --since is specified)
```

Converts input SVG files to PDF file.

#### Options

- `--since time` : filter files since specified time, can be yyyy-MM-ddThh:mm:ss or [N:]N{d|h|m|s}
- `--till time` : filter files till specified time exclusive, can be yyyy-MM-ddThh:mm:ss or [N:]N{d|h|m|s}


## Build

You need to install:
- java 21+
- maven

Debian or Ubuntu:
```
sudo apt -y install openjdk-21-jdk maven
```

RedHat or Suse:
```
sudo yum -y install openjdk-21-jdk maven
```

MacOs:
```
brew install openjdk-21-jdk maven
```

Build:
```
git clone https://github.com/kvr000/zbynek-music-utils.git
cd zbynek-music-utils/
mvn package

./zbynek-music-tool/target/zbynek-music-tool -h
```


## License

The code is released under version 2.0 of the [Apache License][].

## Stay in Touch

Author: Zbynek Vyskovsky

Feel free to contact me at kvr000@gmail.com  and http://github.com/kvr000/ and http://github.com/kvr000/zbynek-music-utils/

LinkedIn: https://www.linkedin.com/in/zbynek-vyskovsky/


[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
